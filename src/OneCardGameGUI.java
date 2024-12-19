import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OneCardGameGUI extends JPanel {
    private JTextArea playerListArea;
    private Client client; // Client 참조 추가
    private JPanel topLeftPanel, topRightPanel, bottomLeftPanel, bottomRightPanel;
    private JPanel centralPanel; // 중앙 패널 참조 추가
    private JLayeredPane layeredPane;
    private Map<String, JPanel> clientPanels = new HashMap<>();
    private RoundedButton submittedCardButton;
    private RoundedButton cardDeckButton;

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

    public void updateHand(String playerName, List<Card> hand) {
        SwingUtilities.invokeLater(() -> {
            JPanel handPanel = clientPanels.get(playerName);
            if (handPanel == null) {
                System.out.println("핸드 패널을 찾을 수 없습니다: " + playerName);
                return;
            }

            // 카드 버튼만 삭제
            Component[] components = handPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JButton button && "cardButton".equals(button.getClientProperty("type"))) {
                    handPanel.remove(button); // 카드 버튼만 삭제
                }
            }

            // 새 카드 버튼 추가
            for (Card card : hand) {
                JButton cardButton = new JButton(card.toString());
                cardButton.putClientProperty("type", "cardButton");
                cardButton.putClientProperty("card", card); // 카드 정보 저장

                cardButton.addActionListener(e -> {
                    hand.remove(card); // 핸드에서 카드 제거
                    handPanel.remove(cardButton); // 해당 버튼 제거
                    client.playCard(card, hand, playerName, null); // 서버로 카드 제출 요청

                    // UI 갱신
                    handPanel.revalidate();
                    handPanel.repaint();
                });

                handPanel.add(cardButton);
            }

            System.out.println("UI 갱신 완료, 남은 손패: " + hand);
            handPanel.revalidate();
            handPanel.repaint();
        });
    }

    public void updateSubmittedCard(Card card) {
        SwingUtilities.invokeLater(() -> {
            if (submittedCardButton != null) {
                ImageIcon cardImage = loadCentralCardImage(card);
                if (cardImage != null) {
                    submittedCardButton.setIcon(cardImage);
                    System.out.println("Submitted Card 버튼 이미지 업데이트 성공");
                } else {
                    System.out.println("ERROR: 카드 이미지를 로드할 수 없습니다.");
                }
                centralPanel.revalidate();
                centralPanel.repaint();
            } else {
                System.out.println("ERROR: Submitted Card 버튼이 초기화되지 않았습니다.");
            }
        });
    }

    public void updateDeckCard(Card card) {
        SwingUtilities.invokeLater(() -> {
            if (cardDeckButton != null) {
                ImageIcon cardImage = loadCentralCardImage(card);
                if (cardImage != null) {
                    cardDeckButton.setIcon(cardImage);
                    System.out.println("card Deck버튼 이미지 업데이트 성공");
                } else {
                    System.out.println("ERROR: 카드 이미지를 로드할 수 없습니다.");
                }
                centralPanel.revalidate();
                centralPanel.repaint();
            } else {
                System.out.println("ERROR: card Deck 버튼이 초기화되지 않았습니다.");
            }
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
                nameY = 10;
            }
            case 3 -> { // Bottom Right Panel
                targetPanel = bottomRightPanel;
                yOffset = 50; // 하단 패널: y 위치를 더 아래로
                nameX = 400;
                nameY = 10;
            }
            default -> throw new IllegalArgumentException("Invalid position: " + position);
        }
        clientPanels.put(playerName, targetPanel);
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

            cardButton.addActionListener(e -> {
                // 현재 클라이언트의 패널에서 클릭한 경우에만 이벤트 발생
                if (client.getName().equals(playerName)) {
                    System.out.println("클릭한 카드 : " + card + " by client " + client.getName());
                    if ("7".equals(card.getRank())) {
                        // 숫자가 7인 경우 팝업 창 띄우기
                        String selectedSuit = showSuitSelectionDialog(card.getSuit());
                        Card newCard = new Card("7", selectedSuit);
                        client.playCard(card, hand, playerName, newCard);
                    } else {
                        // 일반 카드 처리
                        client.playCard(card, hand, playerName, null);
                    }
                } else {
                    System.out.println("다른 플레이어의 패널에서 카드를 제출할 수 없습니다.");
                }
            });

        }

        // **닉네임** 위치: 지정된 자표 사용
        JLabel nameLabel = new JLabel(playerName, SwingConstants.CENTER);
        nameLabel.setBounds(nameX - 50, nameY, 100, 20); // 닉네임의 너비 100px로 중앙 정렬
        targetPanel.add(nameLabel);

        // **이모티콘 버튼들** 위치: 가로로 정렬된 이모티콘 버튼 추가
        JPanel emojiPanel = new JPanel();
        emojiPanel.setLayout(new GridLayout(1, 4, 10, 0)); // FlowLayout으로 가로 정렬
        emojiPanel.setBounds(nameX - 150, nameY + 30, 300, 80); // 이모티콘 패널의 위치 및 크기 설정
        emojiPanel.setOpaque(false); // 배경 투명화

        // 이모티콘 이미지 버튼 생성
        String[] emojiPaths = {
                "resources/emoticon/good.png",
                "resources/emoticon/huck.png",
                "resources/emoticon/cry.png",
                "resources/emoticon/hurry.png"
        };

        for (String emojiPath : emojiPaths) {
            JButton emojiButton = new JButton();
            ImageIcon originalIcon = loadEmojiImage(emojiPath, 65, 65);
            originalIcon.setDescription(emojiPath); // 반드시 경로 설정
            emojiButton.setIcon(originalIcon);
            // 원본 이미지를 버튼에 저장
            emojiButton.putClientProperty("originalIcon", originalIcon);
            emojiButton.setBorderPainted(false); // 테두리 제거
            emojiButton.setFocusPainted(false); // 포커스 윤곽선 제거
            emojiButton.setContentAreaFilled(false); // 배경 투명화

            emojiButton.addActionListener(e -> {
                // 현재 클라이언트의 패널에서 클릭한 경우에만 이벤트 발생
                if (client.getName().equals(playerName)) {
                    System.out.println("이모티콘 클릭: " + emojiPath + " by client " + client.getName());
                    client.sendEmoji(emojiPath, client.getName()); // 서버에 이모티콘 전송
                } else {
                    System.out.println("다른 클라이언트 패널에서 클릭 이벤트 무시");
                }
            });

            emojiPanel.add(emojiButton);
        }

        targetPanel.add(emojiPanel);

        targetPanel.revalidate();
        targetPanel.repaint();
    }

    private String showSuitSelectionDialog(String suit) {
        // 선택지 버튼 생성
        String[] suits = { "Diamond", "Clover", "Heart", "Spade" };
        String selectedSuit = (String) JOptionPane.showInputDialog(
                null,
                "Choose a suit",
                "Select Suit",
                JOptionPane.PLAIN_MESSAGE,
                null,
                suits, // 선택지 배열
                suits[0] // 기본값
        );

        // 사용자가 취소하거나 선택하지 않은 경우 null 반환
        if (selectedSuit == null) {
            System.out.println("사용자가 선택을 취소했습니다.");
            return suit;
        }

        System.out.println("선택된 무늬: " + selectedSuit);
        return selectedSuit;
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

    private void createCentralPanel() {
        centralPanel = new JPanel();
        centralPanel.setLayout(null); // 자유 배치
        centralPanel.setBounds(400, 280, 400, 240);
        centralPanel.setBackground(new Color(144, 238, 144));

        // Submitted Card 버튼 추가
        submittedCardButton = new RoundedButton("Submitted Card");
        submittedCardButton.setBounds(50, 45, 100, 150); // 위치 및 크기 조정
        submittedCardButton.setRoundness(20, 20); // 둥근 모서리 설정
        centralPanel.add(submittedCardButton);

        // Card Deck 버튼 추가
        cardDeckButton = new RoundedButton("Card Deck");
        cardDeckButton.setBounds(250, 45, 100, 150); // 위치 및 크기 조정
        cardDeckButton.setRoundness(20, 20); // 둥근 모서리 설정
        cardDeckButton.addActionListener(e -> {
            System.out.println("Card Deck 클릭됨");
            client.requestCardFromDeck(client.getName()); // 서버에 카드 요청
        });
        centralPanel.add(cardDeckButton);
        centralPanel.revalidate();
        centralPanel.repaint();
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

    private ImageIcon loadCentralCardImage(Card card) {
        String rank = card.getRank();
        String suit = card.getSuit();
        String imagePath = "resources/" + suit.toLowerCase() + "/" + rank + ".png"; // 경로 생성
        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage().getScaledInstance(100, 150, Image.SCALE_SMOOTH); // 크기 조정
        return new ImageIcon(img);
    }

    // 새로운 메서드: 이모티콘 이미지 로드
    private ImageIcon loadEmojiImage(String imagePath, int width, int height) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH); // 크기 조정
        return new ImageIcon(img);
    }

    public void handleIncomingEmoji(String emojiPath, String clientName) {
        SwingUtilities.invokeLater(() -> showEmojiAnimation(emojiPath, clientName)); // UI에서 애니메이션 실행
    }

    public void showEmojiAnimation(String emojiPath, String clientName) {
        System.out.println("[showEmojiAnimation] 호출된 경로: " + emojiPath + ", 클라이언트: " + clientName);

        // 이모티콘 이미지를 로드
        ImageIcon emojiIcon = loadEmojiImage(emojiPath, 65, 65); // 원래 크기
        JLabel emojiLabel = new JLabel(emojiIcon);

        // 원본 크기 및 확대 크기 설정
        int originalWidth = emojiIcon.getIconWidth();
        int originalHeight = emojiIcon.getIconHeight();
        int expandedWidth = originalWidth + 100; // 확대 크기
        int expandedHeight = originalHeight + 100;

        // 이모티콘의 초기 위치를 중앙으로 설정 (클라이언트 패널 위치 기반)
        JPanel clientPanel = clientPanels.get(clientName.trim());
        if (clientPanel == null) {
            System.out.println("[showEmojiAnimation] 클라이언트 패널을 찾지 못했습니다: " + clientName);
            return;
        }
        Point clientPanelLocation = clientPanel.getLocationOnScreen();
        Point layeredPaneLocation = layeredPane.getLocationOnScreen();

        // 위치 계산 (JLayeredPane 좌표계 기준으로 변환)
        int emojiX = clientPanelLocation.x - layeredPaneLocation.x + clientPanel.getWidth() / 2 - originalWidth / 2;
        int emojiY = clientPanelLocation.y - layeredPaneLocation.y + clientPanel.getHeight() / 2 - originalHeight / 2;

        // 초기 위치와 크기 설정
        emojiLabel.setBounds(emojiX, emojiY, originalWidth, originalHeight);

        // JLayeredPane 최상단에 추가
        layeredPane.add(emojiLabel, JLayeredPane.DRAG_LAYER); // 최상단 레이어 사용
        layeredPane.revalidate();
        layeredPane.repaint();

        // 1초 후 확대
        Timer expandTimer = new Timer(0, e -> {
            emojiLabel.setBounds(
                    emojiX - 50, emojiY - 50, // 확대 위치 (중심 유지)
                    expandedWidth, expandedHeight // 확대 크기
            );
            emojiLabel.setIcon(resizeImage(emojiIcon, expandedWidth, expandedHeight));
            layeredPane.revalidate();
            layeredPane.repaint();
        });
        expandTimer.setRepeats(false);
        expandTimer.start();

        // 1초 후 원래 크기로 돌아가며 제거
        Timer removeTimer = new Timer(1000, e -> {
            layeredPane.remove(emojiLabel);
            layeredPane.revalidate();
            layeredPane.repaint();
        });
        removeTimer.setRepeats(false);
        removeTimer.start();
    }

    private ImageIcon resizeImage(ImageIcon icon, int maxWidth, int maxHeight) {
        Image img = icon.getImage();

        // 원본 크기
        int originalWidth = icon.getIconWidth();
        int originalHeight = icon.getIconHeight();

        // 비율 유지하면서 크기 계산
        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double scale = Math.min(widthRatio, heightRatio); // 비율이 작은 쪽 기준으로 크기 조정

        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);

        // 리사이즈
        Image scaledImg = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(scaledImg);
        resizedIcon.setDescription(icon.getDescription()); // 경로 정보 유지
        return resizedIcon;
    }

    public void endingGame(String winnerClient) {

        disconnectFromServer();

        // 부모 컨테이너 가져오기
        Container parent = this.getParent();
        if (parent == null) {
            System.out.println("ERROR: 부모 컨테이너를 찾을 수 없습니다.");
            return;
        }

        // 현재 패널을 부모에서 완전히 제거
        parent.removeAll();

        // 새로운 ResultPanel 추가
        ResultPanel resultPanel = new ResultPanel(winnerClient);
        parent.add(resultPanel);

        // 부모 컨테이너 갱신
        parent.revalidate();
        parent.repaint();
    }

    private void disconnectFromServer() {
        try {
            if (client != null && client.getSocket() != null) {
                System.out.println("서버와의 연결을 종료합니다...");

                // 서버에 종료 메시지 전송 (선택 사항)
                PrintWriter out = new PrintWriter(client.getSocket().getOutputStream(), true);
                out.println("EXIT"); // 서버에 클라이언트 종료 알림

                // 소켓과 스트림 닫기
                client.getSocket().close();
                System.out.println("서버와의 연결이 정상적으로 종료되었습니다.");
            }
        } catch (IOException e) {
            System.out.println("서버 연결 종료 중 오류 발생: " + e.getMessage());
        }
    }

}