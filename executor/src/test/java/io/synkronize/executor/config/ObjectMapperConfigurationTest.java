package io.synkronize.executor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.synkronize.commons.model.SinkPipelineStage;
import io.synkronize.commons.model.stage.LogPipelineStage;
import io.synkronize.extension.stage.runtime.StageTypeRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ObjectMapperConfigurationTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        StageTypeRegistry.set(Map.of(
                "log", LogPipelineStage.class,
                "dummy", DummyPipelineStage.class
        ));
        mapper = new ObjectMapper();
        new ObjectMapperConfiguration().customize(mapper);
    }

    @AfterEach
    void tearDown() {
        StageTypeRegistry.set(Map.of());
    }

    @Test
    void shouldDeserializeLogStageUsingRegisteredType() throws Exception {
        String payload = """
                {
                  "order": 1,
                  "type": "log",
                  "enabled": true,
                  "config": {
                    "level": "INFO"
                  }
                }
                """;

        SinkPipelineStage result = mapper.readValue(payload, SinkPipelineStage.class);

        assertInstanceOf(LogPipelineStage.class, result);
        LogPipelineStage logStage = (LogPipelineStage) result;
        assertTrue(logStage.isEnabled());
        assertEquals("INFO", logStage.getConfig().get("level"));
    }

    @Test
    void shouldDeserializeCustomStageFromRegistry() throws Exception {
        String payload = """
                {
                  "order": 5,
                  "type": "dummy",
                  "enabled": true,
                  "value": "ok"
                }
                """;

        SinkPipelineStage result = mapper.readValue(payload, SinkPipelineStage.class);

        assertInstanceOf(DummyPipelineStage.class, result);
        DummyPipelineStage dummy = (DummyPipelineStage) result;
        assertEquals("ok", dummy.getValue());
    }

    static final class DummyPipelineStage extends SinkPipelineStage {

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
