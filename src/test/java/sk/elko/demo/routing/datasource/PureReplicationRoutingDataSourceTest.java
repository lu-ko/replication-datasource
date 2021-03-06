package sk.elko.demo.routing.datasource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PureReplicationRoutingDataSourceTest {

    @Mock
    private DataSource writeDataSource;

    @Mock
    private DataSource readDataSource;

    @Mock
    private Connection writeConnection;

    @Mock
    private Connection readConnection;

    @Mock
    private Statement writeStatement;

    @Mock
    private Statement readStatement;

    private PureReplicationRoutingDataSource pureReplicationRoutingDataSource;

    @Before
    public void setUp() throws Exception {
        given(writeDataSource.getConnection()).willReturn(writeConnection);
        given(readDataSource.getConnection()).willReturn(readConnection);
        given(writeConnection.createStatement()).willReturn(writeStatement);
        given(readConnection.createStatement()).willReturn(readStatement);

        pureReplicationRoutingDataSource = new PureReplicationRoutingDataSource();
        pureReplicationRoutingDataSource.setWriteDataSource(writeDataSource);
        pureReplicationRoutingDataSource.setReadDataSource(readDataSource);
        pureReplicationRoutingDataSource.setDefaultAutoCommit(false);
        pureReplicationRoutingDataSource.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        pureReplicationRoutingDataSource.init();
    }

    @Test
    public void afterInit() throws Exception {
        verifyZeroInteractions(writeDataSource, writeConnection, readDataSource, readConnection);
    }

    @Test
    public void init_without_defaultConfigurations_should_get_writeDataSourceConnection() throws Exception {
        writeDataSource = mock(DataSource.class);
        readDataSource = mock(DataSource.class);
        writeConnection = mock(Connection.class);
        readConnection = mock(Connection.class);

        given(writeDataSource.getConnection()).willReturn(writeConnection);
        given(writeConnection.getAutoCommit()).willReturn(true);
        given(writeConnection.getTransactionIsolation()).willReturn(Connection.TRANSACTION_REPEATABLE_READ);

        pureReplicationRoutingDataSource = new PureReplicationRoutingDataSource(writeDataSource, readDataSource);

        assertThat(pureReplicationRoutingDataSource.defaultAutoCommit()).isTrue();
        assertThat(pureReplicationRoutingDataSource.defaultTransactionIsolation()).isEqualTo(Connection.TRANSACTION_REPEATABLE_READ);
        verifyZeroInteractions(readDataSource, readConnection);
    }

    @Test
    public void shouldDelegateToWriteDataSourceGetLogWriter() throws Exception {
        PrintWriter printWriter = mock(PrintWriter.class);
        given(writeDataSource.getLogWriter()).willReturn(printWriter);

        assertThat(pureReplicationRoutingDataSource.getLogWriter()).isSameAs(printWriter);
    }

    @Test
    public void shouldDelegateToWriteAndReadDataSourceSetLogWriter() throws Exception {
        PrintWriter printWriter = new PrintWriter(new StringWriter());

        pureReplicationRoutingDataSource.setLogWriter(printWriter);

        verify(writeDataSource).setLogWriter(eq(printWriter));
        verify(readDataSource).setLogWriter(eq(printWriter));
    }

    @Test
    public void shouldDelegateToWriteDataSourceGetLoginTimeout() throws Exception {
        given(writeDataSource.getLoginTimeout()).willReturn(5);

        assertThat(pureReplicationRoutingDataSource.getLoginTimeout()).isEqualTo(5);
    }

    @Test
    public void shouldDelegateToWriteAndReadDataSourceSetLoginTimeout() throws Exception {
        pureReplicationRoutingDataSource.setLoginTimeout(11);

        verify(writeDataSource).setLoginTimeout(11);
        verify(readDataSource).setLoginTimeout(11);
    }


    @Test
    public void shouldGetConnectionGivenProxy() throws Exception {
        Connection connection = pureReplicationRoutingDataSource.getConnection();
        assertThat(connection).isNotNull();
        assertThat(connection.getClass().getName()).contains("Proxy");

        verifyZeroInteractions(writeDataSource, readDataSource);
    }

    @Test
    public void shouldGetConnectionWithUsernameGivenProxy() throws Exception {
        Connection connection = pureReplicationRoutingDataSource.getConnection("username", "password");
        assertThat(connection).isNotNull();
        assertThat(connection.getClass().getName()).contains("Proxy");

        verifyZeroInteractions(writeDataSource, readDataSource);
    }

    @Test
    public void shouldGetConnectionNotCalled__for_close() throws Exception {
        Connection connection = pureReplicationRoutingDataSource.getConnection();
        connection.close();

        verifyZeroInteractions(writeDataSource, readDataSource);
    }

    @Test
    public void shouldDelegateToReadDataSource_readOnly_true_statement() throws Exception {
        Connection connection = pureReplicationRoutingDataSource.getConnection();
        connection.setReadOnly(true);

        Statement statement = connection.createStatement();
        statement.close();
        connection.close();

        verify(readDataSource).getConnection();
        verify(readConnection).createStatement();
        verify(readConnection).close();
        verify(readStatement).close();

        verifyZeroInteractions(writeDataSource, writeConnection);
    }


    @Test
    public void shouldDelegateToWriteDataSource_readOnly_false_statement() throws Exception {
        Connection connection = pureReplicationRoutingDataSource.getConnection();
        connection.setReadOnly(false);

        Statement statement = connection.createStatement();
        statement.close();
        connection.close();

        verify(writeDataSource).getConnection();
        verify(writeConnection).createStatement();
        verify(writeConnection).close();
        verify(writeStatement).close();

        verifyZeroInteractions(readDataSource, readConnection);
    }

    @Test
    public void shouldDelegateGetConnectionWithUsername_readOnly_true_statement() throws Exception {
        final String expectedUsername = "myusername";
        final String expectedPassword = "mypassword";

        given(writeDataSource.getConnection(expectedUsername, expectedPassword)).willReturn(writeConnection);

        Connection connection = pureReplicationRoutingDataSource.getConnection(expectedUsername, expectedPassword);
        connection.setReadOnly(false);

        Statement statement = connection.createStatement();
        statement.close();
        connection.close();

        verify(writeDataSource).getConnection(expectedUsername, expectedPassword);
        verify(writeConnection).createStatement();
        verify(writeConnection).close();
        verify(writeStatement).close();

        verifyZeroInteractions(readDataSource, readConnection);
    }
}