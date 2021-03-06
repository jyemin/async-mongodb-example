package cocktails.controller;

import cocktails.view.AsyncCocktailPage;
import org.mongodb.Document;
import org.mongodb.MongoClientOptions;
import org.mongodb.MongoClientURI;
import org.mongodb.MongoFuture;
import org.mongodb.async.MongoClient;
import org.mongodb.async.MongoClients;
import org.mongodb.async.MongoCollection;

import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Display a cocktail using asynchronous MongoDB database calls with registered callbacks.
 */
public class CallbackBasedCocktailController {

    private final MongoClient client;
    private final MongoCollection<Document> collection;

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        String name = "Margarita";
        PrintStream printStream = System.out;

        new CallbackBasedCocktailController().display(name, printStream);
    }

    public CallbackBasedCocktailController() throws UnknownHostException {
        client = MongoClients.create(new MongoClientURI("mongodb://localhost"), MongoClientOptions.builder().build());
        collection = client.getDatabase("top_ten").getCollection("cocktails");

        // Insert sample data into collection
        collection.tools().drop().get();
        collection.insert(CocktailData.getSampleData()).get();  // Do it synchronously
    }

    public void display(final String name, final PrintStream printStream) throws InterruptedException {
        AsyncCocktailPage page = new AsyncCocktailPage(printStream);

        MongoFuture<Document> cocktailFuture = collection.find(new Document("name", name)).one();

        // Since register method takes a SAM (Single Abstract Method) as a parameter, use a lambda
        cocktailFuture.register((cocktail, e) -> {
            if (e != null) {
                page.displayError(e);
            } else {
                page.setCocktail(cocktail);
                int cocktailId = cocktail.getInteger("_id");
                getPrevious(cocktailId).register((previous, e1) -> {
                    if (e1 != null) {
                        page.displayError(e1);
                    } else {
                        page.setPreviousCocktail(previous);
                    }
                });

                getNext(cocktailId).register((next, e1) -> {
                    if (e1 != null) {
                        page.displayError(e1);
                    } else {
                        page.setNextCocktail(next);
                    }
                });
            }
        });

        try {
            page.await(5, TimeUnit.SECONDS);
        } finally {
            client.close();
        }
    }

    private MongoFuture<Document> getNext(final int cocktailId) {
        return collection.find(new Document("_id", new Document("$gt", cocktailId)))
                         .sort(new Document("_id", 1))
                         .fields(new Document("name", 1))
                         .one();
    }

    private MongoFuture<Document> getPrevious(final int cocktailId) {
        return collection.find(new Document("_id", new Document("$lt", cocktailId)))
                         .sort(new Document("_id", -1))
                         .fields(new Document("name", 1))
                         .one();
    }
}
