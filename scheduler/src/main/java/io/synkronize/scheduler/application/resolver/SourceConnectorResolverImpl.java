package io.synkronize.scheduler.application.resolver;

import io.synkronize.connector.source.spi.SourceConnector;
import io.synkronize.extension.connector.runtime.SourceConnectorRegistry;
import io.synkronize.scheduler.application.exception.SourceConnectorClassNotFound;
import io.synkronize.scheduler.core.connector.SourceConnectorMetadata;
import io.synkronize.scheduler.core.connector.SourceConnectorMetadataHolder;
import io.synkronize.scheduler.core.resolver.SourceConnectorResolver;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@ApplicationScoped
public class SourceConnectorResolverImpl implements SourceConnectorResolver {

    private static final Logger logger = LoggerFactory.getLogger(SourceConnectorResolverImpl.class);

    @Override
    public SourceConnector resolve(String sourceType) {
        Class<? extends SourceConnector> sourceConnectorClass = SourceConnectorRegistry.get(sourceType);
        if (sourceConnectorClass == null)
            throw new SourceConnectorClassNotFound("Source connector class for type " + sourceType + " not found");

        logger.info("Instantiating source connector {}", sourceConnectorClass.getCanonicalName());
        SourceConnector sourceConnector = instantiate(sourceConnectorClass);
        logger.info("Source connector {} instantiated", sourceConnectorClass.getCanonicalName());

        return sourceConnector;
    }

    @Override
    public SourceConnector resolve(String sourceId, String envId) {
        SourceConnectorMetadata metadata = SourceConnectorMetadataHolder.get(envId, sourceId);
        if (metadata == null)
            throw new SourceConnectorClassNotFound("Source connector metadata for source " + sourceId + " and env " + envId + " not found");

        return metadata.getConnector();
    }

    private SourceConnector instantiate(Class<? extends SourceConnector> clazz) {
        try {
            Constructor<? extends SourceConnector> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            String msg = "No default constructor found for {}";
            logger.error(msg, clazz.getCanonicalName(), e);
            throw new RuntimeException(msg, e);
        } catch (InvocationTargetException e) {
            String msg = "Error while invoking constructor {}";
            logger.error(msg, clazz.getCanonicalName(), e);
            throw new RuntimeException(msg, e);
        } catch (InstantiationException e) {
            String msg = "Error while instantiating {}";
            logger.error(msg, clazz.getCanonicalName(), e);
            throw new RuntimeException(msg, e);
        } catch (IllegalAccessException e) {
            String msg = "An illegal access exception was thrown while trying to instantiate {}";
            logger.error(msg, clazz.getCanonicalName(), e);
            throw new RuntimeException(msg, e);
        }
    }
}
