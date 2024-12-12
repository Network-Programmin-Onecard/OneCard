import java.awt.*;
import javax.swing.*;
import java.util.List;

public class OneCardGameGUI extends JPanel {
    private JLabel gameStateLabel;
    private JTextArea playerListArea;
    private JPanel handPanel;
    private Client client; // Client 참조 추가
    private JPanel topLeftPanel, topRightPanel, bottomLeftPanel, bottomRightPanel;
    private JPanel centralPanel; // 중앙 패널 참조 추가
    private JLayeredPane layeredPane;
    private OneCardGameGUI gui;


    public OneCardGameGUI(Client client) {
        this.client = client;
        setLayout(null); // null 레이아웃 설정

         // JLayeredPane 생성
         layeredPane = new JLayeredPane();
         layeredPane.setBounds(0, 0, 1200, 800);

        // 각 코너 패널 추가
        topLeftPanel = createPanel("Top Left", Color.RED, 0, 0, 600, 400);
        layeredPane.add(topLeftPanel, JLayeredPane.DEFAULT_LAYER);

        topRightPanel = createPanel("Top Right", Color.GREEN, 600, 0, 600, 400);
        layeredPane.add(topRightPanel, JLayeredPane.DEFAULT_LAYER);

        bottomLeftPanel = createPanel("Bottom Left", Color.BLUE, 0, 400, 600, 400);
        layeredPane.add(bottomLeftPanel, JLayeredPane.DEFAULT_LAYER);

        bottomRightPanel = createPanel("Bottom Right", Color.YELLOW, 600, 400, 600, 400);
        layeredPane.add(bottomRightPanel, JLayeredPane.DEFAULT_LAYER);

        createCentralPanel();
        layeredPane.add(centralPanel, JLayeredPane.PALETTE_LAYER); // 항상 최상단에 위치

        // 레이아웃에 추가
        add(layeredPane);
    }

    private JPanel createPanel(String text, Color color, int x, int y, int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBounds(x, y, width, height);
        panel.setBackground(color);

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);

        return panel;
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

    private void createCentralPanel() {
        // 중앙 패널 생성
        centralPanel = new JPanel();
        centralPanel.setLayout(null); // 자유 배치
        centralPanel.setBounds(350, 250, 500, 300); // 화면 중앙 위치 지정
        centralPanel.setBackground(Color.LIGHT_GRAY);

        // Submitted Card 버튼 추가
        JButton submittedCardButton = new JButton("Submitted Card");
        submittedCardButton.setBounds(50, 50, 150, 100);
        centralPanel.add(submittedCardButton);

        // Card Deck 버튼 추가
        JButton cardDeckButton = new JButton("Card Deck");
        cardDeckButton.setBounds(300, 50, 150, 100);
        centralPanel.add(cardDeckButton);
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