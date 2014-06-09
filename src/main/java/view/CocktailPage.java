package view;

import org.mongodb.Document;

import java.io.PrintStream;

public class CocktailPage {
    private final PrintStream printStream;
    
    private Document cocktail;
    private Document previousCocktail;
    private Document nextCocktail;

    public CocktailPage(final PrintStream printStream) {
        this.printStream = printStream;
    }

    public Document getCocktail() {
        return cocktail;
    }

    public Document getPreviousCocktail() {
        return previousCocktail;
    }

    public Document getNextCocktail() {
        return nextCocktail;
    }

    public void setCocktail(final Document cocktail) {
        this.cocktail = cocktail;
    }

    public synchronized void setPreviousCocktail(final Document previousCocktail) {
        this.previousCocktail = previousCocktail;
    }

    public synchronized void setNextCocktail(final Document nextCocktail) {
        this.nextCocktail = nextCocktail;
    }

    public void display() {
        printStream.println();
        printStream.println("Featured cocktail: " + cocktail);
        printStream.println();
        printStream.println("Previous: " + previousCocktail);
        printStream.println("Next:     " + nextCocktail);
        printStream.println();
        printStream.flush();
    }

    public void displayError(final Throwable e) {
        printStream.println();
        printStream.println(e.getMessage());
        printStream.println();
        printStream.flush();
    }
}
