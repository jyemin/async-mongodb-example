package callback;

import org.mongodb.Document;
import org.mongodb.MongoClientOptions;
import org.mongodb.MongoClientURI;
import org.mongodb.MongoFuture;
import org.mongodb.async.MongoClient;
import org.mongodb.async.MongoClients;
import org.mongodb.async.MongoCollection;
import view.async.AsyncCocktailPage;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class CallbackBasedCocktailDisplay {

    static MongoClient client;
    static MongoCollection<Document> collection;

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        client = MongoClients.create(new MongoClientURI("mongodb://localhost"), MongoClientOptions.builder().build());
        collection = client.getDatabase("cookbook").getCollection("cocktails");

        String name = "Pomegranate Margarita";

        AsyncCocktailPage page = new AsyncCocktailPage(System.out);

        MongoFuture<Document> cocktailFuture = collection.find(new Document("name", name)).one();

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

    private static MongoFuture<Document> getNext(final int cocktailId) {
        return collection.find(new Document("_id", new Document("$gt", cocktailId)))
                         .sort(new Document("_id", 1))
                         .fields(new Document("name", 1))
                         .one();
    }

    private static MongoFuture<Document> getPrevious(final int cocktailId) {
        return collection.find(new Document("_id", new Document("$lt", cocktailId)))
                         .sort(new Document("_id", -1))
                         .fields(new Document("name", 1))
                         .one();
    }

}
