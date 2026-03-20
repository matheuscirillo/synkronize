package io.synkronize.executor.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import io.quarkus.mongodb.runtime.MongoClientCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoClientConfigCustomizer implements MongoClientCustomizer {

    private final Logger log = LoggerFactory.getLogger(MongoClientConfigCustomizer.class);

    @Override
    public MongoClientSettings.Builder customize(MongoClientSettings.Builder builder) {
        log.info("Customizing MongoClientSettings");
        return builder.applyConnectionString(new ConnectionString("mongodb://localhost:27017/"));
    }
}