package io.synkronize.executor.sink.provider;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import io.synkronize.commons.model.SinkConnector;
import io.synkronize.executor.sink.Sink;
import io.synkronize.executor.sink.provider.utils.JsonDeserializer;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MongoDBSinkProvider implements SinkProvider {

    private static final String DB_NAME = "synkronize";
    private static final String COLLECTION_NAME = "tasks";

    private final MongoClient mongoClient;
    private final JsonDeserializer deserializer;

    public MongoDBSinkProvider(MongoClient mongoClient, JsonDeserializer deserializer) {
        this.mongoClient = mongoClient;
        this.deserializer = deserializer;
    }

    @Override
    public List<SinkConnector> getSinksFromActiveVersion(String taskId) {
        MongoDatabase db = getDatabase(DB_NAME);
        List<Bson> pipeline = Arrays.asList(
                Aggregates.match(Filters.eq("_id", taskId)),
                Aggregates.addFields(
                        new Field<>("currentVersion",
                                new Document("$arrayElemAt", Arrays.asList(
                                        new Document("$filter", new Document()
                                                .append("input", "$versions")
                                                .append("as", "v")
                                                .append("cond", new Document("$eq", Arrays.asList("$$v.isCurrent", true)))
                                        ),
                                        0
                                ))
                        )
                ),

                Aggregates.replaceWith(
                        new Document()
                                .append("taskId", "$_id")
                                .append("sinks", "$currentVersion.sinks")
                )
        );

        List<Document> results = db.getCollection(COLLECTION_NAME)
                .aggregate(pipeline).into(new ArrayList<>());

        if (results.isEmpty()) {
            return List.of();
        }

        List<SinkConnector> sinkConnectors = new ArrayList<>();
        Document document = results.getFirst();
        document.getList("sinks", Document.class)
                .forEach(d -> {
                    sinkConnectors.add(deserializer.deserialize(d.toJson(), SinkConnector.class));
                });

        return sinkConnectors;
    }

    @Override
    public Optional<Sink> findSink(String taskId, String version, String sinkId) {
        return Optional.empty();
    }

    private MongoDatabase getDatabase(String databaseName) {
        return mongoClient.getDatabase(databaseName);
    }
}
