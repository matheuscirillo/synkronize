package io.synkronize.executor.model;

import java.util.Map;
import java.util.Objects;

public final class SynkronizeMessage {
    private String uniqueId;
    private String taskId;
    private String envId;
    private long timestamp;
    private Type type;
    private Content content;

    public SynkronizeMessage(String uniqueId,
                             String taskId,
                             String envId,
                             long timestamp,
                             Type type,
                             Content content) {
        this.uniqueId = uniqueId;
        this.taskId = taskId;
        this.envId = envId;
        this.timestamp = timestamp;
        this.type = type;
        this.content = content;
    }

    public String uniqueId() {
        return uniqueId;
    }

    public void uniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String taskId() {
        return taskId;
    }

    public void taskId(String taskId) {
        this.taskId = taskId;
    }

    public String envId() {
        return envId;
    }

    public void envId(String envId) {
        this.envId = envId;
    }

    public long timestamp() {
        return timestamp;
    }

    public void timestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Type type() {
        return type;
    }

    public void type(Type type) {
        this.type = type;
    }

    public Content content() {
        return content;
    }

    public void content(Content content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SynkronizeMessage) obj;
        return Objects.equals(this.uniqueId, that.uniqueId) &&
                Objects.equals(this.taskId, that.taskId) &&
                Objects.equals(this.envId, that.envId) &&
                this.timestamp == that.timestamp &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId, taskId, envId, timestamp, type, content);
    }

    @Override
    public String toString() {
        return "SynkronizeMessage[" +
                "uniqueId=" + uniqueId + ", " +
                "taskId=" + taskId + ", " +
                "envId=" + envId + ", " +
                "timestamp=" + timestamp + ", " +
                "type=" + type + ", " +
                "content=" + content + ']';
    }


    public enum Type {
        TEXT,
        BINARY
    }

    public static final class Content {
        private String message;
        private Map<String, String> attributes;

        public Content(String message, Map<String, String> attributes) {
            this.message = message;
            this.attributes = attributes;
        }

        public String message() {
            return message;
        }

        public void message(String message) {
            this.message = message;
        }

        public Map<String, String> attributes() {
            return attributes;
        }

        public void attributes(Map<String, String> attributes) {
            this.attributes = attributes;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Content) obj;
            return Objects.equals(this.message, that.message) &&
                    Objects.equals(this.attributes, that.attributes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(message, attributes);
        }

        @Override
        public String toString() {
            return "Content[" +
                    "message=" + message + ", " +
                    "attributes=" + attributes + ']';
        }
    }
}
