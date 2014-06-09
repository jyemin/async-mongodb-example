package sync;

import org.mongodb.Document;
import org.mongodb.MongoClient;
import org.mongodb.MongoClientOptions;
import org.mongodb.MongoClientURI;
import org.mongodb.MongoClients;
import org.mongodb.MongoCollection;
import view.CocktailPage;

import java.net.UnknownHostException;

public class SynchronousDisplayCocktail {

    static MongoClient client;
    static MongoCollection<Document> collection;

    public static void main(String[] args) throws UnknownHostException {
        client = MongoClients.create(new MongoClientURI("mongodb://localhost"), MongoClientOptions.builder().build());
        collection = client.getDatabase("cookbook").getCollection("cocktails");

        String name = "Pomegranate Margarita";

        CocktailPage page = new CocktailPage();

        try {
            Document cocktail = collection.find(new Document("name", name)).getOne();

            page.setCocktail(cocktail);

            int cocktailId = cocktail.getInteger("_id");

            page.setPreviousCocktail(getPrevious(cocktailId));
            page.setNextCocktail(getNext(cocktailId));

            page.display();

        } catch (Throwable t) {
            page.displayError(t);
        } finally {
            client.close();
        }
    }

    private static Document getPrevious(final int cocktailId) {
        return collection.find(new Document("_id", new Document("$lt", cocktailId)))
                         .sort(new Document("_id", -1))
                         .fields(new Document("name", 1))
                         .getOne();
    }

    private static Document getNext(final int cocktailId) {
        return collection.find(new Document("_id", new Document("$gt", cocktailId)))
                         .sort(new Document("_id", 1))
                         .fields(new Document("name", 1))
                         .getOne();
    }
}
