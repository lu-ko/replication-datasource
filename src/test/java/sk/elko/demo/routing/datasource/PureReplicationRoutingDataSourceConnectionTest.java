package sk.elko.demo.routing.datasource;

import sk.elko.demo.routing.datasource.PureReplicationRoutingDataSource;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;

public class PureReplicationRoutingDataSourceConnectionTest {

    private static DataSource replicationDataSource;

    @BeforeClass
    public static void setUpClass() throws Exception {
        DataSource writeDataSource = new EmbeddedDatabaseBuilder()
                .setName("writeDb")
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .addScript("classpath:/script/writedb.sql").build();

        DataSource readDataSource = new EmbeddedDatabaseBuilder()
                .setName("readDb")
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .addScript("classpath:/script/readdb.sql").build();

        replicationDataSource = new PureReplicationRoutingDataSource(writeDataSource, readDataSource);
    }

    private String queryName(Connection connection, Integer id) throws Exception {
        PreparedStatement statement = connection.prepareStatement("select * from users where id = ?");
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        String name = resultSet.getString("name");
        resultSet.close();
        statement.close();

        return name;
    }

    @Test
    public void fromRead() throws Exception {
        Connection connection = replicationDataSource.getConnection();
        connection.setReadOnly(true);

        String name = queryName(connection, 1);
        assertThat(name).isEqualTo("read_1");

        connection.close();
    }

    @Test
    public void fromWrite() throws Exception {
        Connection connection = replicationDataSource.getConnection();
        connection.setReadOnly(false);

        String name = queryName(connection, 3);
        assertThat(name).isEqualTo("write_3");

        connection.close();
    }

    @Test
    public void read_write_switch_fail() throws Exception {
        Connection connection = replicationDataSource.getConnection();

        connection.setReadOnly(false);
        String readOnlyFalseName = queryName(connection, 1);
        assertThat(readOnlyFalseName).isEqualTo("write_1");

        connection.setReadOnly(true);
        String readOnlyTrueName = queryName(connection, 2);
        assertThat(readOnlyTrueName).as("If connection reused, readOnly configuration follows the first setReadOnly value.").isEqualTo("write_2");

        connection.close();
    }

}
