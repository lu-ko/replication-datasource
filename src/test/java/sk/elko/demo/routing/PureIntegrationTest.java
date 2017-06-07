package sk.elko.demo.routing;

import sk.elko.demo.routing.configuration.PureReplicationRoutingDataSourceConfiguration;
import sk.elko.demo.routing.datasource.PureReplicationRoutingDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Replication with {@link PureReplicationRoutingDataSource}.
 */
@ContextConfiguration(classes = {PureReplicationRoutingDataSourceConfiguration.class})
@DirtiesContext
public class PureIntegrationTest extends AbstractReplicationDataSourceIntegrationTest {
}
