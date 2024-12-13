import java.awt.*;
import javax.swing.*;
import java.util.List;

public class OneCardGameGUI extends JPanel {
    private JTextArea playerListArea;
    private JPanel handPanel;
    private Client client; // Client 참조 추가
    private JPanel topLeftPanel, topRightPanel, bottomLeftPanel, bottomRightPanel;
    private JPanel centralPanel; // 중앙 패널 참조 추가
    private JLayeredPane layeredPane;

    public OneCardGameGUI(Client client) {
        this.client = client;
        setLayout(null); // null 레이아웃 설정

        // JLayeredPane 생성
        layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1200, 800);

        // 각 코너 패널 추가
        topLeftPanel = createPanel(Color.RED, 0, 0, 600, 400);
        layeredPane.add(topLeftPanel, JLayeredPane.DEFAULT_LAYER);

        topRightPanel = createPanel(Color.GREEN, 600, 0, 600, 400);
        layeredPane.add(topRightPanel, JLayeredPane.DEFAULT_LAYER);

        bottomLeftPanel = createPanel(Color.BLUE, 0, 400, 600, 400);
        layeredPane.add(bottomLeftPanel, JLayeredPane.DEFAULT_LAYER);

        bottomRightPanel = createPanel(Color.YELLOW, 600, 400, 600, 400);
        layeredPane.add(bottomRightPanel, JLayeredPane.DEFAULT_LAYER);

        createCentralPanel();
        layeredPane.add(centralPanel, JLayeredPane.PALETTE_LAYER); // 항상 최상단에 위치

        // 레이아웃에 추가
        add(layeredPane);
    }

    private JPanel createPanel(Color color, int x, int y, int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(null); // 자유 배치
        panel.setBounds(x, y, width, height);
        panel.setBackground(color);
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
        int nameX = 0, nameY = 0;
        int yOffset = 0; // y 위치 오프셋

        switch (position) {
            case 0 -> { // Top Left Panel
                targetPanel = topLeftPanel;
                yOffset = -80; // 상단 패널: y 위치를 더 위로
                nameX = 200;
                nameY = 280;
            }
            case 1 -> { // Top Right Panel
                targetPanel = topRightPanel;
                yOffset = -80; // 상단 패널: y 위치를 더 위로
                nameX = 400;
                nameY = 280;
            }
            case 2 -> { // Bottom Left Panel
                targetPanel = bottomLeftPanel;
                yOffset = 50; // 하단 패널: y 위치를 더 아래로
                nameX = 200;
                nameY = 60;
            }
            case 3 -> { // Bottom Right Panel
                targetPanel = bottomRightPanel;
                yOffset = 50; // 하단 패널: y 위치를 더 아래로
                nameX = 400;
                nameY = 60;
            }
            default -> throw new IllegalArgumentException("Invalid position: " + position);
        }

        targetPanel.removeAll(); // 기존 내용 제거

        // 카드 버튼 추가 (가로 정렬 적용)
        int cardWidth = 80;
        int cardHeight = 120;
        int startX = 20; // 카드의 x 시작 위치
        int startY = targetPanel.getHeight() / 2 - cardHeight / 2 + yOffset; // y 위치 조정
        int overlap = 30; // 가로 간격 (겹침 효과)
        for (int i = 0; i < hand.size(); i++) {
            JButton cardButton = new JButton(hand.get(i).toString());
            cardButton.setBounds(startX + (overlap * i), startY, cardWidth, cardHeight); // x 위치만 겹침 효과 적용
            cardButton.setBackground(Color.WHITE); // 카드 버튼의 배경색
            cardButton.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // 카드 테두리
            targetPanel.add(cardButton);
        }
        // **닉네임** 위치: 지정된 자표 사용
        JLabel nameLabel = new JLabel(playerName, SwingConstants.CENTER);
        nameLabel.setBounds(nameX - 50, nameY, 100, 20); // 닉네임의 너비 100px로 중앙 정렬
        targetPanel.add(nameLabel);

        // **이모티콘** 위치: 이름 끝 바로 아래
        JButton emojiButton = new JButton("이모티콘");
        emojiButton.setBounds(nameX - 60, nameY + 30, 120, 30); // 고정된 y 위치 계산
        targetPanel.add(emojiButton);

        targetPanel.revalidate();
        targetPanel.repaint();
    }

    public void updateRemainingCards(String submittedCard, String cardDeckTop) {
        Component[] components = centralPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton button) {
                if ("Submitted Card".equals(button.getText())) {
                    button.setText(submittedCard); // Submitted Card 업데이트
                } else if ("Card Deck".equals(button.getText())) {
                    button.setText(cardDeckTop); // Card Deck 업데이트
                }
            }
        }
        centralPanel.revalidate();
        centralPanel.repaint();
    }

    private void createCentralPanel() {
        centralPanel = new JPanel();
        centralPanel.setLayout(null); // 자유 배치
        centralPanel.setBounds(400, 280, 400, 240);
        centralPanel.setBackground(Color.LIGHT_GRAY);

        // Submitted Card 버튼 추가
        JButton submittedCardButton = new JButton("Submitted Card");
        submittedCardButton.setBounds(50, 45, 100, 150); // 위치 및 크기 조정
        centralPanel.add(submittedCardButton);

        // Card Deck 버튼 추가
        JButton cardDeckButton = new JButton("Card Deck");
        cardDeckButton.setBounds(250, 45, 100, 150); // 위치 및 크기 조정
        centralPanel.add(cardDeckButton);
    }

    public void clearPlayerPanels() {
        topLeftPanel.removeAll();
        topRightPanel.removeAll();
        bottomLeftPanel.removeAll();
        bottomRightPanel.removeAll();
    }
}