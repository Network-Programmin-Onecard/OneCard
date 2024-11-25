import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Card card = (Card) obj;
        return rank.equals(card.rank) && suit.equals(card.suit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, suit);
    }
}
