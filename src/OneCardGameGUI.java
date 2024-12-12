import java.awt.*;
import javax.swing.*;
import java.util.List;

public class OneCardGameGUI extends JPanel {
    private JLabel gameStateLabel;
    private JTextArea playerListArea;
    private JPanel handPanel;
    private Client client; // Client 참조 추가
    private JPanel topLeftPanel, topRightPanel, bottomLeftPanel, bottomRightPanel;
    private OneCardGameGUI gui;


    public OneCardGameGUI(Client client) {
        this.client = client;
        setLayout(new GridLayout(2, 2)); // 2x2 그리드로 설정
        topLeftPanel = new JPanel(new BorderLayout());
        topRightPanel = new JPanel(new BorderLayout());
        bottomLeftPanel = new JPanel(new BorderLayout());
        bottomRightPanel = new JPanel(new BorderLayout());

        add(topLeftPanel); // 좌상
        add(topRightPanel); // 우상
        add(bottomLeftPanel); // 좌하
        add(bottomRightPanel); // 우하
    }

    public void updateHand(List<Card> hand) {
        SwingUtilities.invokeLater(() -> {
            handPanel.removeAll(); // 기존 카드 버튼 제거
            for (Card card : hand) {
                JButton cardButton = new JButton(card.toString());
                cardButton.addActionListener(e -> {
                    // 서버로 제출 요청 전송
                    // Client client = getClientInstance(); // 클라이언트 인스턴스 가져오기
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

    public void updatePlayerList(String[] players) {
        SwingUtilities.invokeLater(() -> {
            playerListArea.setText(""); // 기존 내용 초기화
            for (String player : players) {
                playerListArea.append(player + "\n"); // 새로운 내용 추가
            }

        });
    }

    public void updatePlayerPanel(int position, String playerName, List<Card> hand) {
        JPanel targetPanel;
        switch (position) {
            case 0 -> targetPanel = topLeftPanel;
            case 1 -> targetPanel = topRightPanel;
            case 2 -> targetPanel = bottomLeftPanel;
            case 3 -> targetPanel = bottomRightPanel;
            default -> throw new IllegalArgumentException("Invalid position: " + position);
        }
    
        targetPanel.removeAll();
    
        JLabel nameLabel = new JLabel(playerName, SwingConstants.CENTER);
        targetPanel.setLayout(new BorderLayout());

        targetPanel.add(nameLabel, BorderLayout.NORTH);
    
        JPanel cardPanel = new JPanel(new FlowLayout());
        for (Card card : hand) {
            JButton cardButton = new JButton(card.toString());
            cardPanel.add(cardButton);
        }
        targetPanel.add(cardPanel, BorderLayout.CENTER);
    
        targetPanel.revalidate();
        targetPanel.repaint();
    }

    public void clearPlayerPanels() {
        topLeftPanel.removeAll();
        topRightPanel.removeAll();
        bottomLeftPanel.removeAll();
        bottomRightPanel.removeAll();
    }

    private JPanel createCardPanel(List<Card> hand) {
        JPanel cardPanel = new JPanel(new FlowLayout());
        for (Card card : hand) {
            JButton cardButton = new JButton(card.toString());
            cardPanel.add(cardButton);
        }
        return cardPanel;
    }
}