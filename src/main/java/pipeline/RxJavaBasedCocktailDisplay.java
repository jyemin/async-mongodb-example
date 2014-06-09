package pipeline;

import org.mongodb.Document;
import org.mongodb.MongoClientOptions;
import org.mongodb.MongoClientURI;
import org.mongodb.async.rxjava.MongoClient;
import org.mongodb.async.rxjava.MongoClients;
import org.mongodb.async.rxjava.MongoCollection;
import rx.Observable;
import rx.Subscriber;
import view.async.AsyncCocktailPage;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class RxJavaBasedCocktailDisplay {

    static MongoClient client;
    static MongoCollection<Document> collection;


    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        client = MongoClients.create(new MongoClientURI("mongodb://localhost"), MongoClientOptions.builder().build());
        collection = client.getDatabase("cookbook").getCollection("cocktails");

        String name = "Pomegranate Margarita";

        final AsyncCocktailPage page = new AsyncCocktailPage(System.out);

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

    private static Observable<Document> getNext(final int cocktailId) {
        return collection.find(new Document("_id", new Document("$gt", cocktailId)))
                         .sort(new Document("_id", 1))
                         .fields(new Document("name", 1))
                         .one();
    }

    private static Observable<Document> getPrevious(final int cocktailId) {
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
}
