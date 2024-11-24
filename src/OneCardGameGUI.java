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
    private JLabel[] playerNameLabels;
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
        playerNameLabels = new JLabel[4];
        for (int i = 0; i < playerNameLabels.length; i++) {
            playerNameLabels[i] = new JLabel("Player " + (i + 1));
            playerNameLabels[i].setBounds(50 + i * 250, 10, 200, 30); // 적절한 위치 설정
            add(playerNameLabels[i]);
        }

        deckSizeLabel = new JLabel("남은 카드: " + game.getDeck().size());
        deckSizeLabel.setBounds(1100, 10, 150, 30);
        add(deckSizeLabel);

        addCentralUI(); // UI 초기화
        initializeGame(); // 게임 로직 초기화
        // addPlayerCards();
        // addListeners();
    }

    private void initializeGame() {

    }

    public void updatePlayerNames(String[] names) {
        for (int i = 0; i < playerNameLabels.length; i++) {
            if (i < names.length) {
                playerNameLabels[i].setText(names[i]); // 사용자 이름 설정
            } else {
                playerNameLabels[i].setText(""); // 남은 라벨은 빈 문자열로 설정
            }
        }
        repaint(); // UI 갱신
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
}
