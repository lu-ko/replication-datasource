package sk.elko.demo.routing.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Replication routing data source for applications running with Spring.
 */
public class SpringReplicationRoutingDataSource extends AbstractRoutingDataSource {
    private Logger log = LoggerFactory.getLogger(SpringReplicationRoutingDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceType = TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? "read" : "write";
        log.info("current dataSourceType : {}", dataSourceType);
        return dataSourceType;
    }
}
