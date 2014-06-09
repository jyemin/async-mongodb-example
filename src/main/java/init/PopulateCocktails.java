package init;

import org.mongodb.Document;
import org.mongodb.MongoClient;
import org.mongodb.MongoClients;
import org.mongodb.MongoCollection;
import org.mongodb.connection.ServerAddress;

import static java.util.Arrays.asList;

public class PopulateCocktails {
    public static void main(String[] args) {
        MongoClient clients = MongoClients.create(new ServerAddress());

        try {
            MongoCollection<Document> collection = clients.getDatabase("cookbook").getCollection("cocktails");
            collection.tools().drop();

            collection.insert(new Document("_id", 1)
                              .append("name", "Watermelon Mojito")
                              .append("ingredients", asList("Rum", "Watermelon", "Mint")));

            collection.insert(new Document("_id", 2)
                              .append("name", "Pomegranate Margarita")
                              .append("ingredients", asList("Tequila", "Orange Cointreau", "Lime")));

            collection.insert(new Document("_id", 3)
                              .append("name", "Gin and Tonic")
                              .append("ingredients", asList("Gin", "Tonic", "Lime")));

            collection.insert(new Document("_id", 4)
                              .append("name", "Mint Julep")
                              .append("ingredients", asList("", "", "")));

            collection.insert(new Document("_id", 5)
                              .append("name", "Daiquiri")
                              .append("ingredients", asList("", "", "")));

            collection.insert(new Document("_id", 6)
                              .append("name", "Sangria")
                              .append("ingredients", asList("", "", "")));

            collection.insert(new Document("_id", 7)
                              .append("name", "Michelada")
                              .append("ingredients", asList("", "", "")));

            collection.insert(new Document("_id", 8)
                              .append("name", "Martini")
                              .append("ingredients", asList("", "", "")));

            collection.insert(new Document("_id", 9)
                              .append("name", "Manhattan")
                              .append("ingredients", asList("", "", "")));

            collection.insert(new Document("_id", 10)
                              .append("name", "Pimm's Cup")
                              .append("ingredients", asList("", "", "")));
        } finally {
            clients.close();
        }
    }
}
