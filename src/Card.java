import java.util.Objects;

public class Card {
    private String rank;
    private String suit;

    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public void setSuit(String newSuit){
        this.suit = newSuit;
    }
    
    public String getRank() {
        return rank;
    }

    public String getSuit() {
        return suit;
    }

    public boolean matches(Card other) {
        return this.suit.equals(other.suit) || this.rank.equals(other.rank);
    }

    @Override
    public String toString() {
        return rank + "-" + suit;
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
