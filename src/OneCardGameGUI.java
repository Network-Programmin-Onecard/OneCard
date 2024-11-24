import java.awt.*;
import javax.swing.*;
import java.util.List;

public class OneCardGameGUI extends JPanel {
    private JLabel gameStateLabel;
    private JTextArea playerListArea;
    private JPanel handPanel;
    private Client client; // Client 참조 추가

    
    public OneCardGameGUI(Client client) {
        this.client = client;
        // 기본 레이아웃이나 초기화 코드 추가
        setLayout(new BorderLayout());
        gameStateLabel = new JLabel("게임 상태: 초기화 중");
        gameStateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(gameStateLabel, BorderLayout.NORTH);
    
        playerListArea = new JTextArea();
        playerListArea.setEditable(false);
        add(new JScrollPane(playerListArea), BorderLayout.WEST);
    
        handPanel = new JPanel();
        handPanel.setLayout(new FlowLayout());
        add(handPanel, BorderLayout.SOUTH);
    
        // revalidate();
        // repaint();
    }

    public void updateHand(List<Card> hand) {
        SwingUtilities.invokeLater(() -> {
            handPanel.removeAll(); // 기존 카드 버튼 제거
            for (Card card : hand) {
                JButton cardButton = new JButton(card.toString());
                cardButton.addActionListener(e -> {
                    // 서버로 제출 요청 전송
                    //Client client = getClientInstance(); // 클라이언트 인스턴스 가져오기
                    if (client != null) {
                        client.playCard(card); // 서버로 제출 요청
                    }
                });
                handPanel.add(cardButton);
            }
            System.out.println("UI 갱신 완료, 남은 손패: " + hand); // 디버깅 로그 추가
            handPanel.revalidate();
            handPanel.repaint();
        });
    }
    

    public void updateGameState(String gameState) {
        SwingUtilities.invokeLater(() -> {
            gameStateLabel.setText("게임 상태: " + gameState);
        });
    }

    public void updatePlayerList(String[] players) {
        SwingUtilities.invokeLater(() -> {
            playerListArea.setText(""); // 기존 내용 초기화
            for (String player : players) {
                playerListArea.append(player + "\n"); // 새로운 내용 추가
            }
        });
    }
}
