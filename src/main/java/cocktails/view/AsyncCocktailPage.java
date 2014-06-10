package cocktails.view;

import org.mongodb.Document;

import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * A cocktail display page that can handle asynchronous events for its data.  It will display the page when all of its data is available.
 */
public class AsyncCocktailPage extends CocktailPage {

    private CountDownLatch latch = new CountDownLatch(1);

    public AsyncCocktailPage(final PrintStream printStream) {
        super(printStream);
    }

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
        if (!latch.await(timeout, timeUnit)) {
            throw new InterruptedException("Timed out waiting for page to display");
        }
    }
}
