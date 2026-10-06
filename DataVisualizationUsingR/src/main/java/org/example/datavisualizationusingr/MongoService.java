package org.example.datavisualizationusingr;

import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

public class MongoService {
    private static String URL = "mongodb://localhost:27017";
    public static double getValue(int index) {
        try (MongoClient client = MongoClients.create(URL)) {
            MongoDatabase database = client.getDatabase("SWE307PROJ1");
            MongoCollection<Document> collection = database.getCollection("data");

            Document document = collection.find(eq("index", index)).first();

            if (document == null) {
                throw new RuntimeException("No document found for index: " + index);
            }

            double value = ((Number)document.get("Col-1")).doubleValue();

            return value;
        }
    }
}
