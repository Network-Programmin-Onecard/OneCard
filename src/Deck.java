import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Deck {
    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        String[] suits = { "Spades", "Hearts", "Clubs", "Diamonds" };
        String[] ranks = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A" };
        String imagePath;

        for (String suit : suits) {
            for (String rank : ranks) {
                imagePath = suit + "/" + rank;
                cards.add(new Card(rank, suit, imagePath));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
        System.out.println("덱 셔플 완료: " + cards); // 디버깅
    }

    public Card dealCard() {
        if (!cards.isEmpty()) {
            return cards.remove(0);
        }
        return null;
    }

    public List<Card> drawCards(int count) {
        if (cards.size() < count) {
            throw new IllegalStateException("덱에 카드가 부족합니다! 남은 카드 수: " + cards.size());
        }
    
        List<Card> drawnCards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Card dealtCard = dealCard();
            System.out.println("카드 분배됨: " + dealtCard); // 디버깅
            drawnCards.add(dealtCard);
        }
        return drawnCards;
    }

    public int size() {
        return cards.size();
    }

    public void replenishFromSubmittedCards(SubmittedCard submittedCard) {
        // 덱이 비었거나 카드가 1장만 남은 경우
        if (size() <= 1) {
            Card topCard = submittedCard.getTopCard(); // 제출된 카드의 맨 위 카드
            List<Card> returnedCards = submittedCard.resetPile(); // 제출된 카드에서 나머지 카드 가져옴

            if (!returnedCards.isEmpty()) {
                cards.addAll(returnedCards); // 덱에 추가
                shuffle(); // 셔플
                System.out.println("Deck replenished from submitted cards and shuffled!");

                // 제출된 카드에 맨 위 카드는 다시 추가
                if (topCard != null) {
                    submittedCard.addCard(topCard);
                }
            } else {
                System.out.println("No cards available in submitted pile to replenish!");
            }
        }
    }
}
