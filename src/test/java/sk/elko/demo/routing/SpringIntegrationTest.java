package sk.elko.demo.routing;

import sk.elko.demo.routing.configuration.SpringReplicationRoutingDataSourceConfiguration;
import sk.elko.demo.routing.datasource.SpringReplicationRoutingDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Replication with {@link SpringReplicationRoutingDataSource}.
 */
@ContextConfiguration(classes = {SpringReplicationRoutingDataSourceConfiguration.class})
@DirtiesContext
public class SpringIntegrationTest extends AbstractReplicationDataSourceIntegrationTest {
}
