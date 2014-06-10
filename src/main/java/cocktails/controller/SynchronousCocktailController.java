package cocktails.controller;

import cocktails.view.CocktailPage;
import org.mongodb.Document;
import org.mongodb.MongoClient;
import org.mongodb.MongoClientOptions;
import org.mongodb.MongoClientURI;
import org.mongodb.MongoClients;
import org.mongodb.MongoCollection;

import java.io.PrintStream;
import java.net.UnknownHostException;

/**
 * Display a cocktail using synchronous MongoDB database calls.
 */
public class SynchronousCocktailController {

    private final MongoClient client;
    private final MongoCollection<Document> collection;

    public SynchronousCocktailController() throws UnknownHostException {
        client = MongoClients.create(new MongoClientURI("mongodb://localhost"), MongoClientOptions.builder().build());
        collection = client.getDatabase("top_ten").getCollection("cocktails");

        // Insert sample data into collection
        collection.tools().drop();
        collection.insert(CocktailData.getSampleData());
    }

    public void display(final String name, final PrintStream out) {
        CocktailPage page = new CocktailPage(out);

        try {
            Document cocktail = collection.find(new Document("name", name)).getOne();

            page.setCocktail(cocktail);

            int cocktailId = cocktail.getInteger("_id");

            Document previousCocktail = getPrevious(cocktailId);
            Document nextCocktail = getNext(cocktailId);

            page.setPreviousCocktail(previousCocktail);
            page.setNextCocktail(nextCocktail);

            page.display();

        } catch (Throwable t) {
            page.displayError(t);
        } finally {
            client.close();
        }
    }

    private Document getPrevious(final int cocktailId) {
        return collection.find(new Document("_id", new Document("$lt", cocktailId)))
                         .sort(new Document("_id", -1))
                         .fields(new Document("name", 1))
                         .getOne();
    }

    private Document getNext(final int cocktailId) {
        return collection.find(new Document("_id", new Document("$gt", cocktailId)))
                         .sort(new Document("_id", 1))
                         .fields(new Document("name", 1))
                         .getOne();
    }

    public static void main(String[] args) throws UnknownHostException {
        String name = "Margarita";
        PrintStream printStream = System.out;

        new SynchronousCocktailController().display(name, printStream);
   }
}
