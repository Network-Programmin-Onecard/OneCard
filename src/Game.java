import java.util.*;

public class Game {
    private final Map<String, List<Card>> playerHands = new HashMap<>(); // 플레이어 이름과 손패를 매핑
    private final Deck deck; // 덱 객체 (Deck 클래스 사용)
    public SubmittedCard submittedCards; // 제출된 카드 관리 객체

    public Game() {
        this.deck = new Deck(); // 덱 생성
        this.submittedCards = new SubmittedCard(); // 제출된 카드 관리 객체 생성
        deck.shuffle(); // 덱 셔플
    }

    /**
     * 새로운 플레이어 추가
     */
    public synchronized void addPlayer(String playerName) {
        playerHands.put(playerName, new ArrayList<>()); // 빈 손패로 초기화
    }

    /**
     * 플레이어 삭제
     */
    public synchronized void removePlayer(String playerName) {
        playerHands.remove(playerName);
    }

    /**
     * 특정 플레이어의 손패 반환
     */
    public synchronized List<Card> getPlayerHand(String playerName) {
        return playerHands.getOrDefault(playerName, new ArrayList<>());
    }

    /**
     * 게임 시작 - 플레이어에게 카드 배분
     */
    public synchronized void startGame(List<String> clientNames) {
        // 각 플레이어에게 8장씩 배분
        for (String clientName : clientNames) {
            List<Card> hand = deck.drawCards(8); // 덱에서 8장 추출
            playerHands.put(clientName, hand); // 손패 등록
        }

        // 남은 카드 중 첫 번째 카드를 제출된 카드로 설정
        Card firstCard = deck.dealCard(); // 덱에서 첫 번째 카드 추출
        if (firstCard != null) {
            submittedCards.addCard(firstCard); // 제출된 카드 관리 객체에 추가
        } else {
            throw new IllegalStateException("덱에서 제출할 첫 번째 카드가 없습니다!");
        }
    }

    /**
     * 플레이어가 카드 제출
     */
    public synchronized boolean playTurn(String playerName, Card card) {
        List<Card> hand = playerHands.get(playerName);

        // 손패에서 카드 제거 및 제출된 카드 스택에 추가
        hand.remove(card);

        // 덱 상태 확인 및 보충
        // deck.replenishFromSubmittedCards(submittedCards);

        // 손패가 비었으면 게임 종료
        return hand.isEmpty();
    }

    /**
     * 플레이어의 손패 직렬화 (클라이언트로 전송용)
     */
    public String serializeHand(String playerName) {
        List<Card> hand = getPlayerHand(playerName);
        StringBuilder sb = new StringBuilder();
        for (Card card : hand) {
            sb.append(card.getRank()).append(" ").append(card.getSuit()).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1); // 마지막 쉼표 제거
        }
        return sb.toString();
    }

    /**
     * 제출된 카드 더미의 최상위 카드 반환
     */
    public synchronized Card getTopSubmittedCard() {
        return this.submittedCards.getTopCard();
    }

    /**
     * 덱에 남은 카드 수 반환
     */
    public synchronized int getRemainingDeckSize() {
        return deck.size();
    }

    public List<Card> getRemainingDeck() {
        return deck.getCards(); // Deck 클래스의 getCards() 호출
    }

    public String updateSubmittedCard(Card card){
        submittedCards.addCard(card);
        return getTopSubmittedCard().toString();
    }
}
