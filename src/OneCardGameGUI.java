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
        topLeftPanel = createPanel(new Color(255, 182, 193), 0, 0, 600, 400);
        layeredPane.add(topLeftPanel, JLayeredPane.DEFAULT_LAYER);
        topRightPanel = createPanel(new Color(230, 230, 250), 600, 0, 600, 400);
        layeredPane.add(topRightPanel, JLayeredPane.DEFAULT_LAYER);

        bottomLeftPanel = createPanel(new Color(173, 216, 230), 0, 400, 600, 400);
        layeredPane.add(bottomLeftPanel, JLayeredPane.DEFAULT_LAYER);

        bottomRightPanel = createPanel(new Color(255, 250, 205), 600, 400, 600, 400);
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
            Card card = hand.get(i);
            // 카드 버튼 생성
            RoundedButton cardButton = new RoundedButton("");
            cardButton.setIcon(loadCardImage(card));
            cardButton.setBounds(startX + (overlap * i), startY, cardWidth, cardHeight); // x 위치만 겹침 효과 적용
            // 버튼 스타일링
            cardButton.setBorderPainted(false); // 테두리 렌더링 비활성화
            cardButton.setFocusPainted(false); // 포커스 윤곽선 제거
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

    public void updateRemainingCards(Card submittedCard, Card cardDeckTop) {
        Component[] components = centralPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof RoundedButton button) {
                if ("Submitted Card".equals(button.getText())) {
                    button.setText(""); // 기존 텍스트 제거
                    if (submittedCard != null) {
                        ImageIcon cardImage = loadCardImage(submittedCard); // 카드 이미지 로드
                        button.setIcon(resizeImage(cardImage, button.getWidth(), button.getHeight())); // 크기 조정
                        // 버튼 스타일링
                        button.setBorderPainted(false); // 테두리 렌더링 비활성화
                        button.setContentAreaFilled(false); // 배경 투명화
                        button.setFocusPainted(false); // 포커스 윤곽선 제거
                    }
                } else if ("Card Deck".equals(button.getText())) {
                    button.setText(""); // 기존 텍스트 제거
                    if (cardDeckTop != null) {
                        ImageIcon cardImage = loadCardImage(cardDeckTop); // 카드 이미지 로드
                        button.setIcon(resizeImage(cardImage, button.getWidth(), button.getHeight())); // 크기 조정
                        // 버튼 스타일링
                        button.setBorderPainted(false); // 테두리 렌더링 비활성화
                        button.setContentAreaFilled(false); // 배경 투명화
                        button.setFocusPainted(false); // 포커스 윤곽선 제거
                    }
                }
            }
        }
        centralPanel.revalidate();
        centralPanel.repaint();
    }

    private ImageIcon resizeImage(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    private void createCentralPanel() {
        centralPanel = new JPanel();
        centralPanel.setLayout(null); // 자유 배치
        centralPanel.setBounds(400, 280, 400, 240);
        centralPanel.setBackground(new Color(144, 238, 144));

        // Submitted Card 버튼 추가
        RoundedButton submittedCardButton = new RoundedButton("Submitted Card");
        submittedCardButton.setBounds(50, 45, 100, 150); // 위치 및 크기 조정
        submittedCardButton.setRoundness(20, 20); // 둥근 모서리 설정
        centralPanel.add(submittedCardButton);

        // Card Deck 버튼 추가
        RoundedButton cardDeckButton = new RoundedButton("Card Deck");
        cardDeckButton.setBounds(250, 45, 100, 150); // 위치 및 크기 조정
        cardDeckButton.setRoundness(20, 20); // 둥근 모서리 설정
        centralPanel.add(cardDeckButton);
    }

    public void clearPlayerPanels() {
        topLeftPanel.removeAll();
        topRightPanel.removeAll();
        bottomLeftPanel.removeAll();
        bottomRightPanel.removeAll();
    }

    private ImageIcon loadCardImage(Card card) {
        String rank = card.getRank();
        String suit = card.getSuit();
        String imagePath = "resources/" + suit.toLowerCase() + "/" + rank + ".png"; // 경로 생성
        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH); // 크기 조정
        return new ImageIcon(img);
    }
}