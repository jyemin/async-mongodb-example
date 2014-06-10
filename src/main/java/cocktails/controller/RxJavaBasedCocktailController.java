package cocktails.controller;

import cocktails.view.AsyncCocktailPage;
import org.mongodb.Document;
import org.mongodb.MongoClientOptions;
import org.mongodb.MongoClientURI;
import org.mongodb.async.rxjava.MongoClient;
import org.mongodb.async.rxjava.MongoClients;
import org.mongodb.async.rxjava.MongoCollection;
import rx.Observable;
import rx.Subscriber;

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

        cocktailObservable.subscribe(new Subscriber<Document>() {
            @Override
            public void onError(final Throwable e) {
                page.displayError(e);
            }

            @Override
            public void onNext(final Document cocktail) {
                page.setCocktail(cocktail);
            }

            @Override
            public void onCompleted() {
                int cocktailId = page.getCocktail().getInteger("_id");

                getPrevious(cocktailId).subscribe(new PrevOrNextSubscriber(page) {
                    @Override
                    public void onNext(final Document previous) {
                        page.setPreviousCocktail(previous);
                    }
                });

                getNext(cocktailId).subscribe(new PrevOrNextSubscriber(page) {
                    @Override
                    public void onNext(final Document next) {
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

    private abstract static class PrevOrNextSubscriber extends Subscriber<Document> {
        private final AsyncCocktailPage page;

        public PrevOrNextSubscriber(final AsyncCocktailPage page) {
            this.page = page;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(final Throwable e) {
            page.displayError(e);
        }
    }

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        String name = "Margarita";
        PrintStream printStream = System.out;

        new RxJavaBasedCocktailController().display(name, printStream);
    }
}
