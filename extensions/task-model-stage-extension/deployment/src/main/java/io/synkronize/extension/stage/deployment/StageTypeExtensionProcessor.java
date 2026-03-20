package io.synkronize.extension.stage.deployment;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.synkronize.commons.model.SinkPipelineStage;
import io.synkronize.commons.model.stage.StageType;
import io.synkronize.extension.stage.runtime.StageTypeRegistryRecorder;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;

import java.util.HashMap;
import java.util.Map;

public class StageTypeExtensionProcessor {

    private static final DotName OBJECT_DOT_NAME = DotName.createSimple(Object.class.getName());
    private static final DotName STAGE_TYPE_DOT_NAME = DotName.createSimple(StageType.class.getName());
    private static final DotName SINK_PIPELINE_STAGE_DOT_NAME = DotName.createSimple(SinkPipelineStage.class.getName());

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem("synkronize-task-model-stage-extension");
    }

    @BuildStep
    StageTypeImplementationsBuildItem collectStageTypeImplementations(CombinedIndexBuildItem combinedIndex) {
        IndexView index = combinedIndex.getIndex();
        Map<String, String> implementationsByType = new HashMap<>();

        for (AnnotationInstance annotation : index.getAnnotations(STAGE_TYPE_DOT_NAME)) {
            if (annotation.target().kind() != AnnotationTarget.Kind.CLASS) {
                throw new IllegalStateException("@StageType must target a class declaration");
            }

            ClassInfo stageClassInfo = annotation.target().asClass();
            if (!isSinkPipelineStageSubtype(index, stageClassInfo)) {
                throw new IllegalStateException(
                        "Class " + stageClassInfo.name()
                                + " is annotated with @StageType but does not extend "
                                + SinkPipelineStage.class.getName()
                );
            }

            AnnotationValue stageTypeValue = annotation.value();
            if (stageTypeValue == null || stageTypeValue.asString().isBlank()) {
                throw new IllegalStateException(
                        "@StageType value must be non-empty for class " + stageClassInfo.name()
                );
            }

            String stageType = stageTypeValue.asString();
            String className = stageClassInfo.name().toString();
            String existing = implementationsByType.putIfAbsent(stageType, className);

            if (existing != null && !existing.equals(className)) {
                throw new IllegalStateException(
                        "Duplicate @StageType value '" + stageType + "' for classes "
                                + existing + " and " + className
                );
            }
        }

        return new StageTypeImplementationsBuildItem(implementationsByType);
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void registerStageTypeImplementations(
            StageTypeImplementationsBuildItem item,
            StageTypeRegistryRecorder recorder
    ) throws ClassNotFoundException {
        Map<String, Class<? extends SinkPipelineStage>> implementationsByType = new HashMap<>();

        for (Map.Entry<String, String> implementation : item.getImplementationsByType().entrySet()) {
            Class<?> implementationClass = Class.forName(
                    implementation.getValue(),
                    false,
                    Thread.currentThread().getContextClassLoader()
            );
            if (!SinkPipelineStage.class.isAssignableFrom(implementationClass)) {
                throw new IllegalStateException(
                        "Class " + implementationClass.getName() + " does not extend " + SinkPipelineStage.class.getName()
                );
            }
            implementationsByType.put(
                    implementation.getKey(),
                    implementationClass.asSubclass(SinkPipelineStage.class)
            );
        }

        recorder.init(implementationsByType);
    }

    private boolean isSinkPipelineStageSubtype(IndexView index, ClassInfo classInfo) {
        DotName current = classInfo.name();

        while (current != null && !OBJECT_DOT_NAME.equals(current)) {
            if (SINK_PIPELINE_STAGE_DOT_NAME.equals(current)) {
                return true;
            }

            ClassInfo currentClass = index.getClassByName(current);
            if (currentClass == null) {
                return false;
            }
            current = currentClass.superName();
        }

        return false;
    }
}
