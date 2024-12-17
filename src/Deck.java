import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Deck {
    private List<Card> cards;   // 카드 배열. 게임이 시작하면 셔플된 후 플레이어에게 배분, 남은 카드는 해당 배열에 존재재

    public List<Card> getCards(){
        return cards;
    }

    public Deck() {
        cards = new ArrayList<>();
        String[] suits = { "Spade", "Heart", "Clover", "Diamond" };
        String[] ranks = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A" };

        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(new Card(rank, suit));
            }
        }
    }

    public Card getFirstCard() {
        if (!cards.isEmpty()) {
            return cards.get(0);
        } else {
            System.out.println("ERROR: Deck is empty!");
            return null; // 리스트가 비어있으면 null 반환
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
        System.out.println("덱 셔플 완료: "); // 디버깅
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
            Card card = getFirstCard();
            cards = null;
            List<Card> returnedCards = submittedCard.resetFile(); // 제출된 카드에서 나머지 카드 가져옴
            if (!returnedCards.isEmpty()) {
                cards.addAll(returnedCards); // 덱에 추가
                shuffle(); // 셔플
                Card returnlastCard = cards.get(0);
                cards.set(0, card);
                cards.add(returnlastCard);
                System.out.println("Deck replenished from submitted cards and shuffled!");

            } else {
                System.out.println("No cards available in submitted pile to replenish!");
            }
        }
    }
}
