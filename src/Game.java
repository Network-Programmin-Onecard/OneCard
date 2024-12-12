import java.util.*;

public class Game {
    private final Map<String, List<Card>> playerHands = new HashMap<>();

    public void addPlayer(String playerName) {
        playerHands.put(playerName, new ArrayList<>()); // 빈 손패로 초기화
    }

    public List<Card> getPlayerHand(String playerName) {
        return playerHands.getOrDefault(playerName, new ArrayList<>());
    }

    public boolean playTurn(String playerName, Card card) {
        List<Card> hand = playerHands.get(playerName);
        if (!hand.contains(card)) {
            throw new IllegalStateException("손패에 없는 카드를 제출하려 했습니다!");
        }
        hand.remove(card); // 카드 제출
        return hand.isEmpty(); // 손패가 비었으면 게임 종료
    }

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

    public void removePlayer(String playerName) {
        playerHands.remove(playerName);
    }

    public void startGame(List<String> clientNames) {
        Deck deck = new Deck(); // 새로운 덱 생성
        deck.shuffle(); // 덱 셔플
    
        for (String clientName : clientNames) {
            List<Card> hand = new ArrayList<>(deck.drawCards(8)); // 각 플레이어에게 8장 배분
            playerHands.put(clientName, hand); // 플레이어 손패 저장
        }
    }
    
}