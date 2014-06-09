package sync;

import org.mongodb.Document;
import org.mongodb.MongoClient;
import org.mongodb.MongoClientOptions;
import org.mongodb.MongoClientURI;
import org.mongodb.MongoClients;
import org.mongodb.MongoCollection;
import view.CocktailPage;

import java.io.PrintStream;
import java.net.UnknownHostException;

public class SynchronousCocktailDisplay {

    private final MongoClient client;
    private final MongoCollection<Document> collection;

    public SynchronousCocktailDisplay() throws UnknownHostException {
        client = MongoClients.create(new MongoClientURI("mongodb://localhost"), MongoClientOptions.builder().build());
        collection = client.getDatabase("cookbook").getCollection("cocktails");
    }

    public void display(final String name, final PrintStream out) {
        CocktailPage page = new CocktailPage(out);

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
        String name = "Pomegranate Margarita";
        PrintStream printStream = System.out;

        new SynchronousCocktailDisplay().display(name, printStream);
   }
}
