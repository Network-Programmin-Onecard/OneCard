import java.util.*;

public class Game {
    private final Deck deck;
    private final SubmittedCard submittedCard;
    private final Map<String, List<Card>> clientHands; // 클라이언트별 손패
    private String currentPlayer; // 현재 턴인 플레이어
    private Card initialCard;

    public Game() {
        deck = new Deck();
        submittedCard = new SubmittedCard();
        clientHands = new LinkedHashMap<>(); // 순서 유지
    }

    // 게임 시작: 덱 셔플 및 카드 배분
    public void startGame(List<String> clientNames) {
        deck.shuffle();

        for (String clientName : clientNames) {
            clientHands.put(clientName, deck.drawCards(8)); // 8장씩 배분
        }

        // 첫 번째 카드 제출
        initialCard = deck.dealCard();
        if (initialCard != null) {
            submittedCard.addCard(initialCard);
            System.out.println("제출된 첫 번째 카드: " + initialCard);
        }

        // 첫 번째 플레이어 설정
        Iterator<String> iterator = clientHands.keySet().iterator();
        currentPlayer = iterator.hasNext() ? iterator.next() : null;
    }

    // 카드 제출
    // Game.java
    public boolean playTurn(String clientName, Card card) {
        if (!clientName.equals(currentPlayer)) {
            throw new IllegalStateException("현재 턴이 아닙니다!");
        }
    
        List<Card> playerHand = clientHands.get(clientName);
        if (!playerHand.contains(card)) {
            throw new IllegalStateException("손패에 없는 카드를 제출하려 했습니다!");
        }
    
        if (submittedCard.canSubmit(card)) {
            submittedCard.addCard(card); // 제출된 카드에 추가
            playerHand.remove(card); // 손패에서 제거
            System.out.println("서버: " + clientName + "가 " + card + " 제출");
            return playerHand.isEmpty(); // 손패가 비었는지 확인 (게임 종료 조건)
        } else {
            throw new IllegalStateException("제출할 수 없는 카드입니다!");
        }
    }
    

    public List<String> getPlayers() {
        return new ArrayList<>(clientHands.keySet());
    }

    // 클라이언트 추가
    public void addPlayer(String clientName) {
        if (clientHands.containsKey(clientName)) {
            throw new IllegalStateException("이미 존재하는 클라이언트입니다.");
        }
        clientHands.put(clientName, new ArrayList<>()); // 빈 손패로 초기화
    }

    // 클라이언트 제거
    public void removePlayer(String clientName) {
        clientHands.remove(clientName);
    }

    // 턴 전환
    private void advanceTurn() {
        Iterator<String> iterator = clientHands.keySet().iterator();
        boolean foundCurrent = false;

        while (iterator.hasNext()) {
            String nextPlayer = iterator.next();
            if (foundCurrent) {
                currentPlayer = nextPlayer;
                return;
            }
            if (nextPlayer.equals(currentPlayer)) {
                foundCurrent = true;
            }
        }

        // 마지막 플레이어 후 처음으로 돌아감
        currentPlayer = clientHands.keySet().iterator().next();
    }

    // 현재 게임 상태 반환
    public String getGameState() {
        StringBuilder state = new StringBuilder();
        state.append("현재 턴: ").append(currentPlayer).append("\n");
        state.append("제출된 카드: ").append(submittedCard.getTopCard()).append("\n");
        for (Map.Entry<String, List<Card>> entry : clientHands.entrySet()) {
            state.append(entry.getKey()).append(": ").append(entry.getValue().size()).append("장\n");
        }
        return state.toString();
    }

    public List<Card> getPlayerHand(String clientName) {
        return clientHands.get(clientName);
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public SubmittedCard getSubmittedCard() {
        return submittedCard;
    }

    public Deck getDeck() {
        return deck;
    }

    public Card getInitialCard() {
        return initialCard;
    }
}
