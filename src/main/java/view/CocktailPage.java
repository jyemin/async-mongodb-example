package view;

import org.mongodb.Document;

public class CocktailPage {
    private Document cocktail;
    private Document previousCocktail;
    private Document nextCocktail;

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
        System.out.println();
        System.out.println("Featured cocktail: " + cocktail);
        System.out.println();
        System.out.println("Previous: " + previousCocktail);
        System.out.println("Next:     " + nextCocktail);
        System.out.println();
        System.out.flush();
    }

    public void displayError(final Throwable e) {
        System.out.println();
        System.out.println(e.getMessage());
        System.out.println();
        System.out.flush();
    }
}
