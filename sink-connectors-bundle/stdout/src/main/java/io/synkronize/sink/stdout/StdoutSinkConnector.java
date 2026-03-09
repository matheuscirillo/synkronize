package io.synkronize.sink.stdout;

import io.synkronize.connector.sink.spi.SinkConnector;
import io.synkronize.connector.sink.spi.SynkronizeSinkConnector;
import io.synkronize.connector.sink.spi.context.SinkContext;

@SynkronizeSinkConnector("stdout")
public class StdoutSinkConnector implements SinkConnector {

    private String taskId;

    @Override
    public void onInit(SinkContext sinkContext) {
        this.taskId = sinkContext.taskId();
    }

    @Override
    public void execute(String message) {
        System.out.printf("[STDOUT SINK CONNECTOR] - Task Id: %s, Message: %s\n", this.taskId, message);
    }

    @Override
    public boolean isClosed() {
        return false;
    }
}
