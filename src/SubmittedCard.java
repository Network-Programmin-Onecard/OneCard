import java.util.ArrayList;
import java.util.List;

public class SubmittedCard {
    private List<Card> submittedCards;

    public SubmittedCard() {
        this.submittedCards = new ArrayList<>();
    }

    public void addCard(Card card) {
        submittedCards.add(card);
    }

    public Card getTopCard() {
        if (!submittedCards.isEmpty()) {
            return submittedCards.get(submittedCards.size() - 1);
        }
        return null;
    }

    public List<Card> resetPile() {
        if (submittedCards.size() <= 1) {
            return new ArrayList<>();
        }

        List<Card> cardsToReturn = new ArrayList<>(submittedCards.subList(0, submittedCards.size() - 1));
        Card topCard = getTopCard();
        submittedCards.clear();
        if (topCard != null) {
            submittedCards.add(topCard);
        }
        return cardsToReturn;
    }

    public boolean canSubmit(Card card) {
        Card topCard = getTopCard();
        if (topCard == null) {
            return true; // 첫 번째 카드는 항상 제출 가능
        }
        return card.getSuit().equals(topCard.getSuit()) || card.getRank().equals(topCard.getRank());
    }

    public void checkingCard(){
        System.out.println("현재 제출된 카드 맨 위? : " + this.submittedCards.get(0));
    }
    
}
