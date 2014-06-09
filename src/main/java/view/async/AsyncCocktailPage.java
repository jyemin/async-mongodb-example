package view.async;

import org.mongodb.Document;
import view.CocktailPage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AsyncCocktailPage extends CocktailPage {

    private CountDownLatch latch = new CountDownLatch(1);

    public synchronized void setPreviousCocktail(final Document previousCocktail) {
        super.setPreviousCocktail(previousCocktail);
        if (getNextCocktail() != null && latch.getCount() == 1) {
            display();
            latch.countDown();
        }
    }

    public synchronized void setNextCocktail(final Document nextCocktail) {
        super.setNextCocktail(nextCocktail);
        if (getPreviousCocktail() != null && latch.getCount() == 1) {
            display();
            latch.countDown();
        }
    }

    public synchronized void displayError(final Throwable e) {
        if (latch.getCount() == 1) {
            super.displayError(e);
            latch.countDown();
        }
    }

    public void await(final long timeout, TimeUnit timeUnit) throws InterruptedException {
        latch.await(timeout, timeUnit);
    }
}
