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
        addPlayerLabels(playerNameLabels);

        // 카드 버튼 생성 / 나중에 수정할 부분. 플레이어 별로 카드가 생성되게 해야됨
        JButton[][] cardButtons = new JButton[4][16];
        for (int i = 0; i < cardButtons.length; i++) {
            for (int j = 0; j < cardButtons[i].length; j++) {
                cardButtons[i][j] = new JButton("Card " + (j + 1));
            }
        }

        // 카드 UI 생성
        handCardUI(cardButtons);

        deckSizeLabel = new JLabel("남은 카드: " + game.getDeck().size());
        deckSizeLabel.setBounds(1100, 10, 150, 30);
        add(deckSizeLabel);

        addCentralUI(); // UI 초기화
        initializeGame(); // 게임 로직 초기화
        // addPlayerCards();
        // addListeners();
    }

    private void addPlayerLabels(JLabel[] playerLabels) {
        for (int i = 0; i < playerLabels.length; i++) {
            playerLabels[i] = new JLabel("Player " + (i + 1));

            // 위치 설정
            switch (i) {
                case 0: // 왼쪽 위 모서리
                    playerLabels[i].setBounds(10, 10, 200, 30);
                    break;
                case 1: // 오른쪽 위 모서리
                    playerLabels[i].setBounds(1000, 10, 200, 30); // 프레임 크기에 맞춰 x 좌표 수정
                    break;
                case 2: // 오른쪽 아래 모서리
                    playerLabels[i].setBounds(1000, 700, 200, 30); // 프레임 크기에 맞춰 y 좌표 수정
                    break;
                case 3: // 왼쪽 아래 모서리
                    playerLabels[i].setBounds(10, 700, 200, 30);
                    break;
                default:
                    break;
            }
            add(playerLabels[i]);

        }

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

    private void handCardUI(JButton[][] cardButtons) {

        int overlapOffset = 25;
        int cardWidth = 100;
        int cardHeight = 150;

        for (int i = 0; i < cardButtons.length; i++) {
            for (int j = 0; j < cardButtons[i].length; j++) {

                JButton cardButton = cardButtons[i][j];
                switch (i) {
                    case 0: // 왼쪽 위 모서리
                        cardButtons[i][j].setBounds(50 + j * overlapOffset, 50, cardWidth, cardHeight);
                        break;
                    case 1: // 오른쪽 위 모서리
                        cardButtons[i][j].setBounds(700 + j * overlapOffset, 50, cardWidth, cardHeight); // 프레임 크기에 맞춰 x
                                                                                                         // 좌표 수정
                        break;
                    case 2: // 오른쪽 아래 모서리
                        cardButtons[i][j].setBounds(700 + j * overlapOffset, 550, cardWidth, cardHeight); // 프레임 크기에 맞춰
                                                                                                          // y 좌표 수정
                        break;
                    case 3: // 왼쪽 아래 모서리
                        cardButtons[i][j].setBounds(50 + j * overlapOffset, 550, cardWidth, cardHeight);
                        break;
                    default:
                        break;
                }
                // cardButton.setBounds(baseX + i * overlapOffset, baseY, cardWidth,
                // cardHeight);
                this.add(cardButton); // 카드 패널에 추가
            }
        }

    }

    private void addCentralUI() {
        int rectWidth = 500;
        int rectHeight = 300;
        int rectX = (1200 - rectWidth) / 2;
        int rectY = (800 - rectHeight) / 2;

        submittedCardButton.setBounds(rectX + 110, rectY + 40, 100, 150);
        cardDeckButton.setBounds(rectX + rectWidth - 220, rectY + 40, 100, 150);
        oneCardButton.setBounds(rectX + rectWidth / 2 - 50, rectY + rectHeight - 60, 100, 50);

        this.add(submittedCardButton);
        this.add(cardDeckButton);
        this.add(oneCardButton);
    }
}
