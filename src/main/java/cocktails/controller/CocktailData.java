package cocktails.controller;

import org.mongodb.Document;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public class CocktailData {
    public static List<Document> getSampleData() {
        return Arrays.asList(
                            new Document("_id", 1)
                            .append("name", "Watermelon Mojito")
                            .append("ingredients", asList("Rum", "Watermelon", "Mint")),

                            new Document("_id", 2)
                            .append("name", "Margarita")
                            .append("ingredients", asList("Tequila", "Cointreau", "Lime")),

                            new Document("_id", 3)
                            .append("name", "Gin and Tonic")
                            .append("ingredients", asList("Gin", "Tonic", "Lime")),

                            new Document("_id", 4)
                            .append("name", "Mint Julep")
                            .append("ingredients", asList("Simple syrup", "Bourbon", "Mint")),

                            new Document("_id", 5)
                            .append("name", "Daiquiri")
                            .append("ingredients", asList("Lime", "Sugar", "Rum")),

                            new Document("_id", 6)
                            .append("name", "Sangria")
                            .append("ingredients", asList("Brandy", "Triple Sec", "Orange Juice", "Red wine", "Club soda")),

                            new Document("_id", 7)
                            .append("name", "Michelada")
                            .append("ingredients", asList("Lemon", "Soy Sauce", "Clamato", "Beer")),

                            new Document("_id", 8)
                            .append("name", "Martini")
                            .append("ingredients", asList("Vodka", "Olives")),

                            new Document("_id", 9)
                            .append("name", "Manhattan")
                            .append("ingredients", asList("Rye Whisky", "Sweet Vermouth", "Angostura bitters")),

                            new Document("_id", 10)
                            .append("name", "Pimm's Cup")
                            .append("ingredients", asList("English cucumber wheel", "Lemon wheel", "Pimm's No. 1", "Ginger ale")));

    }
}
