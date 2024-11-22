import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class OneCardGameGUI extends JPanel {
    private JButton submittedCardButton;
    private JButton cardDeckButton;
    private JButton oneCardButton;
    private Game game;
    private JLabel[] playerCardLabels;
    private JButton[][] playerCardButtons;
    private JLabel deckSizeLabel;

    public OneCardGameGUI() {
        setLayout(null);

        game = new Game();

        // 중앙 UI를 먼저 초기화
        submittedCardButton = new JButton(); // 초기화를 명시적으로 수행
        cardDeckButton = new JButton("카드 뭉치");
        oneCardButton = new JButton("원카드!");
        oneCardButton.setEnabled(false); // 기본적으로 비활성화

        deckSizeLabel = new JLabel("남은 카드: " + game.getDeck().size());
        deckSizeLabel.setBounds(1100, 10, 150, 30);
        add(deckSizeLabel);

        addCentralUI(); // UI 초기화
        initializeGame(); // 게임 로직 초기화
        addPlayerCards();
        addListeners();
    }

    private void initializeGame() {
        game.addPlayer("Player 1");
        game.addPlayer("Player 2");
        game.addPlayer("Player 3");
        // game.addPlayer("Player 4");
        game.startGame();

        // 제출된 첫 번째 카드 버튼 텍스트 업데이트
        submittedCardButton.setText("" + game.getInitialCard());
        refreshUI();
    }

    private void addCentralUI() {
        int rectWidth = 500;
        int rectHeight = 300;
        int rectX = (1200 - rectWidth) / 2;
        int rectY = (800 - rectHeight) / 2;

        submittedCardButton.setBounds(rectX + 40, rectY + 40, 150, 200);
        cardDeckButton.setBounds(rectX + rectWidth - 190, rectY + 40, 150, 200);
        oneCardButton.setBounds(rectX + rectWidth / 2 - 50, rectY + rectHeight - 60, 100, 50);

        this.add(submittedCardButton);
        this.add(cardDeckButton);
        this.add(oneCardButton);
    }

    private void addPlayerCards() {
        playerCardLabels = new JLabel[4];
        playerCardButtons = new JButton[4][10];
        int[][] coords = { { 50, 50 }, { 700, 50 }, { 50, 650 }, { 700, 650 } };

        for (int i = 0; i < 4; i++) {
            playerCardLabels[i] = new JLabel(game.getPlayers().get(i).getName());
            playerCardLabels[i].setBounds(coords[i][0], coords[i][1] - 30, 200, 30);
            this.add(playerCardLabels[i]);

            for (int j = 0; j < 10; j++) {
                playerCardButtons[i][j] = new JButton();
                playerCardButtons[i][j].setBounds(coords[i][0] + j * 90, coords[i][1], 80, 30);
                this.add(playerCardButtons[i][j]);
            }
            updatePlayerCardButtons(i);
        }
    }

    private void updatePlayerCardButtons(int playerIndex) {
        Player player = game.getPlayers().get(playerIndex);
        List<Card> hand = player.getHand();

        for (int j = 0; j < 10; j++) {
            JButton cardButton = playerCardButtons[playerIndex][j];

            // 카드가 있는 경우 버튼 활성화
            if (j < hand.size()) {
                Card card = hand.get(j);

                // 기존 리스너 제거
                for (ActionListener al : cardButton.getActionListeners()) {
                    cardButton.removeActionListener(al);
                }

                cardButton.setText(card.getRank() + " " + card.getSuit());
                cardButton.setVisible(true);
                cardButton.setEnabled(player == game.getCurrentPlayer()); // 현재 플레이어만 활성화
                cardButton.addActionListener(new CardPlayListener(player, card, cardButton));
            } else {
                // 카드가 없는 경우 버튼 비활성화
                cardButton.setVisible(false);
            }
        }
    }

    private void addListeners() {
        cardDeckButton.addActionListener(e -> {
            Player currentPlayer = game.getCurrentPlayer(); // 현재 턴의 플레이어 가져오기
            Card newCard = game.getDeck().dealCard(); // 덱에서 새 카드 가져오기
            if (newCard != null) {
                currentPlayer.addCard(newCard); // 현재 플레이어에게 새 카드 추가
                // JOptionPane.showMessageDialog(this, currentPlayer.getName() + "가 새 카드를 가져왔습니다: " + newCard);
        
                // 턴 전환
                game.nextTurn(); // 다음 플레이어로 전환
                refreshUI(); // UI 업데이트
            } else {
                JOptionPane.showMessageDialog(this, "덱에 카드가 없습니다!");
            }
        });
        

        oneCardButton.addActionListener(e -> {
            for (Player player : game.getPlayers()) {
                if (player != game.getCurrentPlayer() && player.getHand().size() == 1) {
                    Card penaltyCard = game.getDeck().dealCard();
                    if (penaltyCard != null) {
                        player.addCard(penaltyCard);
                        JOptionPane.showMessageDialog(this, player.getName() + "가 규칙 위반으로 패널티 카드를 받았습니다: " + penaltyCard);
                        refreshUI();
                    }
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "규칙 위반이 감지되지 않았습니다.");
        });
    }

    private class CardPlayListener implements ActionListener {
        private Player player;
        private Card card;
        private JButton cardButton;

        public CardPlayListener(Player player, Card card, JButton cardButton) {
            this.player = player;
            this.card = card;
            this.cardButton = cardButton;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (player != game.getCurrentPlayer()) {
                JOptionPane.showMessageDialog(OneCardGameGUI.this, "현재 당신의 차례가 아닙니다!");
                return;
            }

            if (game.getSubmittedCard().canSubmit(card)) {
                game.playTurn(card); // 카드 제출
                JOptionPane.showMessageDialog(OneCardGameGUI.this, player.getName() + "가 카드를 제출했습니다: " + card);
                refreshUI(); // UI 갱신
            } else {
                JOptionPane.showMessageDialog(OneCardGameGUI.this, "제출할 수 없는 카드입니다!");
            }
        }
    }

    private void refreshUI() {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < game.getPlayers().size(); i++) {
                updatePlayerCardButtons(i);
            }

            // 제출된 카드 업데이트
            Card topCard = game.getSubmittedCard().getTopCard();
            submittedCardButton.setText(topCard != null ? ""+topCard : "제출된 카드 없음");

            // 덱 크기 업데이트
            deckSizeLabel.setText("남은 카드: " + game.getDeck().size());
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int rectWidth = 500;
        int rectHeight = 300;
        int rectX = (1200 - rectWidth) / 2;
        int rectY = (800 - rectHeight) / 2;
        g.drawRect(rectX, rectY, rectWidth, rectHeight);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("One Card Game GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.add(new OneCardGameGUI());
        frame.setVisible(true);
    }
}
