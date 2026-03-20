package io.synkronize.executor.engine.pipeline.stage;

import io.synkronize.commons.model.stage.XSLTPipelineStage;
import io.synkronize.executor.model.SynkronizeMessage;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

public class XSLTStage extends Stage<XSLTPipelineStage> {

    private final Source xsltSource;

    public XSLTStage(XSLTPipelineStage sinkPipelineStage) {
        super(sinkPipelineStage);
        this.xsltSource = new StreamSource(new StringReader(sinkPipelineStage.getTransformation()));
    }

    @Override
    public SynkronizeMessage process(SynkronizeMessage input) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(xsltSource);
            transformer.setOutputProperty(OutputKeys.INDENT, "no");

            StringWriter writer = new StringWriter();
            transformer.transform(new StreamSource(new StringReader(input.content().message())),
                    new StreamResult(writer));
            input.content().message(writer.toString());

            return input;
        } catch (TransformerException e) {
            // TODO
            throw new RuntimeException(e);
        }
    }
}
