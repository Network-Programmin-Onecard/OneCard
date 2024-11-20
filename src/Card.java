public class Card {
    private String rank;
    private String suit;
    private String imagePath;

    public Card(String rank, String suit, String imagePath) {
        this.rank = rank;
        this.suit = suit;
        this.imagePath = imagePath;
    }

    public String getRank() {
        return rank;
    }

    public String getSuit() {
        return suit;
    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean matches(Card other) {
        return this.suit.equals(other.suit) || this.rank.equals(other.rank);
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
