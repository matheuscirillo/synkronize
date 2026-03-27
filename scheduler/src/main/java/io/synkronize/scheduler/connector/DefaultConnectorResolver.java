package io.synkronize.scheduler.connector;

import io.synkronize.connector.source.spi.SourceConnector;
import io.synkronize.connector.source.spi.SourceConnectorFactory;
import io.synkronize.connector.source.spi.context.task.TaskContext;
import io.synkronize.extension.connector.runtime.SourceConnectorRegistry;
import io.synkronize.scheduler.connector.exception.ConnectorClassNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@ApplicationScoped
public class DefaultConnectorResolver implements ConnectorResolver {

    private static final Logger logger = LoggerFactory.getLogger(DefaultConnectorResolver.class);

    @Override
    public SourceConnector resolve(String sourceType, TaskContext taskContext) {
        Class<? extends SourceConnectorFactory> factoryClass = SourceConnectorRegistry.get(sourceType);
        if (factoryClass == null)
            throw new ConnectorClassNotFoundException("Source connector factory for type " + sourceType + " not found");

        logger.info("Creating source connector via factory {}", factoryClass.getCanonicalName());
        SourceConnectorFactory factory = instantiateFactory(factoryClass);
        SourceConnector sourceConnector = factory.create(taskContext);
        logger.info("Source connector {} created", sourceConnector.getClass().getCanonicalName());

        return sourceConnector;
    }

    @Override
    public SourceConnector resolve(String sourceId, String envId) {
        ConnectorMetadata metadata = ConnectorRegistry.get(envId, sourceId);
        if (metadata == null)
            throw new ConnectorClassNotFoundException("Source connector metadata for source " + sourceId + " and env " + envId + " not found");

        return metadata.getConnector();
    }

    private SourceConnectorFactory instantiateFactory(Class<? extends SourceConnectorFactory> clazz) {
        try {
            Constructor<? extends SourceConnectorFactory> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            String msg = "No default constructor found for %s".formatted(clazz.getCanonicalName());
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        } catch (InvocationTargetException e) {
            String msg = "Error while invoking factory constructor for %s".formatted(clazz.getCanonicalName());
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        } catch (InstantiationException e) {
            String msg = "Error while instantiating %s".formatted(clazz.getCanonicalName());
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        } catch (IllegalAccessException e) {
            String msg = "Illegal access while trying to instantiate %s".formatted(clazz.getCanonicalName());
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
}
