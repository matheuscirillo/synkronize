package io.synkronize.executor.engine.pipeline.stage.factory;

import io.synkronize.commons.model.stage.XSLTPipelineStage;
import io.synkronize.executor.engine.pipeline.stage.XSLTStage;
import jakarta.enterprise.context.Dependent;

@Dependent
public class XSLTStageFactory implements StageFactory<XSLTStage, XSLTPipelineStage> {

    @Override
    public XSLTStage newInstance(XSLTPipelineStage sinkPipelineStage) {
        return new XSLTStage(sinkPipelineStage);
    }
}
