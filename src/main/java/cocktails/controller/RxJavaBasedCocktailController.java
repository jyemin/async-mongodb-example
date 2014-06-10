package cocktails.controller;

import cocktails.view.AsyncCocktailPage;
import org.mongodb.Document;
import org.mongodb.MongoClientOptions;
import org.mongodb.MongoClientURI;
import org.mongodb.async.rxjava.MongoClient;
import org.mongodb.async.rxjava.MongoClients;
import org.mongodb.async.rxjava.MongoCollection;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Display a cocktail using synchronous MongoDB database calls wrapped in RxJava Observable.
 */
public class RxJavaBasedCocktailController {

    private final MongoClient client;
    private final MongoCollection<Document> collection;

    public RxJavaBasedCocktailController() throws UnknownHostException {
        client = MongoClients.create(new MongoClientURI("mongodb://localhost"), MongoClientOptions.builder().build());
        collection = client.getDatabase("top_ten").getCollection("cocktails");

        // Insert sample data into collection
        collection.tools().drop().toBlockingObservable().single();
        collection.insert(CocktailData.getSampleData()).toBlockingObservable().single();
    }

    public void display(final String name, final PrintStream printStream) throws InterruptedException {
        final AsyncCocktailPage page = new AsyncCocktailPage(printStream);

        Observable<Document> cocktailObservable = collection.find(new Document("name", name)).one();
        Observable<AsyncCocktailPage> observablePage = cocktailObservable
                .map(cocktail -> {
                    page.setCocktail(cocktail);
                    int cocktailId = page.getCocktail().getInteger("_id");
                    return Observable.zip(getPrevious(cocktailId), getNext(cocktailId), new Func2<Document, Document, AsyncCocktailPage>() {
                        @Override
                        public AsyncCocktailPage call(Document previous, Document next) {
                            page.setPreviousCocktail(previous);
                            page.setNextCocktail(next);
                            page.display();
                            return page;
                        }
                    });
                })
                .flatMap(asyncCocktailPageObservable -> asyncCocktailPageObservable)
                .timeout(5, TimeUnit.SECONDS)
                .doOnError(page::displayError);

        try {
            observablePage.toBlockingObservable().first();
        } finally {
            client.close();
        }
    }

    private Observable<Document> getNext(final int cocktailId) {
        return collection.find(new Document("_id", new Document("$gt", cocktailId)))
                         .sort(new Document("_id", 1))
                         .fields(new Document("name", 1))
                         .one();
    }

    private Observable<Document> getPrevious(final int cocktailId) {
        return collection.find(new Document("_id", new Document("$lt", cocktailId)))
                         .sort(new Document("_id", -1))
                         .fields(new Document("name", 1))
                         .one();
    }

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        String name = "Margarita";
        PrintStream printStream = System.out;

        new RxJavaBasedCocktailController().display(name, printStream);
    }
}
