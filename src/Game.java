import java.util.ArrayList;
import java.util.List;

public class Game {
    private Deck deck;
    private SubmittedCard submittedCard;
    private List<Player> players;
    private int currentPlayerIndex; // 현재 플레이어 인덱스
    private Card initialCard;

    public Game() {
        deck = new Deck();
        submittedCard = new SubmittedCard();
        players = new ArrayList<>();
        currentPlayerIndex = 0; // 첫 번째 플레이어부터 시작
    }

    public void addPlayer(String name) {
        players.add(new Player(name));
    }

    public void startGame() {
        deck.shuffle();

        // 각 플레이어에게 5장의 카드를 나눠줌
        for (int i = 0; i < 5; i++) {
            for (Player player : players) {
                player.addCard(deck.dealCard());
            }
        }

        // 덱에서 첫 번째 카드를 제출된 카드로 설정
        initialCard = deck.dealCard();
        if (initialCard != null) {
            submittedCard.addCard(initialCard);
            System.out.println("제출된 첫 번째 카드: " + initialCard);
        }
    }

    public void playTurn(Card card) {
        Player currentPlayer = players.get(currentPlayerIndex);
        if (submittedCard.canSubmit(card)) {
            submittedCard.addCard(card); // 카드를 제출된 카드에 추가
            currentPlayer.getHand().remove(card); // 플레이어의 손에서 카드 제거
            deck.replenishFromSubmittedCards(submittedCard); // 덱 보충 확인

            // 턴 전환
            nextTurn();
        } else {
            System.out.println("제출할 수 없는 카드입니다!");
        }
    }

    // 턴 전환 메서드 추가
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public SubmittedCard getSubmittedCard() {
        return submittedCard;
    }

    public Deck getDeck() {
        return deck;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public Card getInitialCard() {
        return initialCard;
    }
}
