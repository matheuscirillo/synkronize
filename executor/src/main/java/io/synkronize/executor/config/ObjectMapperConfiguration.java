package io.synkronize.executor.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import io.quarkus.jackson.ObjectMapperCustomizer;
import io.synkronize.commons.model.SinkPipelineStage;
import io.synkronize.extension.stage.runtime.StageTypeRegistry;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Singleton
public class ObjectMapperConfiguration implements ObjectMapperCustomizer {

    private final Logger log = LoggerFactory.getLogger(ObjectMapperConfiguration.class);

    @Override
    public void customize(ObjectMapper mapper) {
        NamedType[] stageSubtypes = StageTypeRegistry.getStageClasses()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new NamedType(entry.getValue(), entry.getKey()))
                .toArray(NamedType[]::new);
        if (stageSubtypes.length > 0) {
            mapper.registerSubtypes(stageSubtypes);
        }

        mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override
            public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config,
                                                           AnnotatedClass ac,
                                                           JavaType baseType) {
                if (SinkPipelineStage.class.isAssignableFrom(ac.getRawType())) {
                    return new StdTypeResolverBuilder()
                            .init(JsonTypeInfo.Id.NAME, null)
                            .inclusion(JsonTypeInfo.As.PROPERTY)
                            .typeProperty("type")
                            .typeIdVisibility(true);
                }
                return super.findTypeResolver(config, ac, baseType);
            }
        });
    }
}
