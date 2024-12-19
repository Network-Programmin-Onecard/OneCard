import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String name; // 클라이언트 이름
    private List<Card> hand = new ArrayList<>(); // 플레이어의 손패
    private OneCardGameGUI gui; // GUI 업데이트를 위한 참조
    private Card cardDeckTop;

    public void setGUI(OneCardGameGUI gui) {
        this.gui = gui;
    }

    public String getName() {
        return this.name;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public boolean connect(String ip, int port, String userName, OneCardGameGUI gui) {
        try {
            socket = new Socket(ip, port);
            System.out.println("서버 연결 성공");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
    
            // 서버에 이름 전송
            out.println(userName);
            this.gui = gui; // GUI 참조를 설정
            
            String response = in.readLine();
            System.out.println("서버 응답 수신: " + response);
    
            if (response != null && response.startsWith("NAME_ERROR")) {
                // 이름 중복 처리
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null,
                            "입력한 이름이 이미 사용 중입니다. 다른 이름을 입력하세요.",
                            "이름 중복", JOptionPane.WARNING_MESSAGE);
                });
                return false;
            }
    
            this.name = userName;
            // 서버 응답 처리 스레드 시작
            new Thread(this::receiveMessages).start();
            return true;
    
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 서버로 메시지 전송
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    // 서버 메시지 수신 및 처리
    // Client.java
    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("receiveMessages 문자 내용 확인: " + message);
                if (message.startsWith("GAME_STATE:")) {
                    String gameState = message.substring(11); // 상태 데이터
                    updateGameState(gameState);
                } else if (message.startsWith("REMAINING_CARDS:")) {
                    String remainingCards = message.substring(16); // 남은 카드 데이터
                    updateRemainingCards(remainingCards); // 남은 카드 갱신
                } else if (message.startsWith("SUBMITTED_CARD|")) {
                    System.out.println("서버 메시지: " + message);
                    onServerMessageReceived(message);
                } else if (message.startsWith("EMOJI|")) {// 이모지 처리 메시지
                    System.out.println("서버 메시지: " + message);
                    onServerMessageReceived(message);
                } else if (message.startsWith("DRAW_CARD|")) {
                    System.out.println("서버 메시지: " + message);
                    onServerMessageReceived(message); // 게임 상태 파싱 후 업데이트
                } else if (message.startsWith("GAME_WINNER|")) {
                    System.out.println("서버 메시지: " + message);
                    onServerMessageReceived(message);   // 게임 승자 확인
                } else {
                    System.out.println("오류 메시지: " + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateRemainingCards(String remainingCards) {
        String[] parts = remainingCards.split(",");
        Card submittedCard;
        if (parts.length < 2) {
            submittedCard = parseCard(parts[0]); // 첫 번째 카드
            gui.updateRemainingCards(submittedCard, cardDeckTop); // GUI 갱신
        } else {
            // String 데이터를 Card 객체로 변환
            submittedCard = parseCard(parts[0]); // 첫 번째 카드
            cardDeckTop = parts.length > 1 ? parseCard(parts[1]) : null; // 두 번째 카드 (없으면 null)
            gui.updateRemainingCards(submittedCard, cardDeckTop); // GUI 갱신

        }
    }

    private Card parseCard(String cardString) {
        String[] parts = cardString.split("-");
        if (parts.length != 2) {
            return null; // 잘못된 형식 처리
        }
        return new Card(parts[0], parts[1]); // Rank와 Suit로 카드 객체 생성
    }

    public List<Card> parseHand(String handData) {
        List<Card> hand = new ArrayList<>();
        String[] cards = handData.split(",");
        for (String card : cards) {
            String[] parts = card.split(" ");
            if (parts.length == 2) {
                hand.add(new Card(parts[0], parts[1]));
            }
        }
        return hand;
    }

    // Client.java - updateHand
    public void updateHand(List<Card> newHand) {
        this.hand.clear(); // 기존 손패 비우기
        this.hand.addAll(newHand); // 새 손패 추가

        System.out.println("UI 갱신: " + hand); // 디버깅 로그 추가
        gui.updateHand(this.getName(), hand); // GUI 갱신
    }

    // 게임 상태 업데이트
    private void updateGameState(String gameState) {
        String[] players = gameState.split(";");
        SwingUtilities.invokeLater(() -> {
            gui.clearPlayerPanels();
            for (String playerData : players) {
                String[] parts = playerData.split(",");
                if (parts.length < 3) {
                    System.out.println("Invalid player data: " + playerData);
                    continue;
                }
                System.out.println("플레이어 별 데이터 : " + playerData);
                int position = Integer.parseInt(parts[0]);
                String playerName = parts[1];

                String CardString = String.join(",", Arrays.copyOfRange(parts, 2, parts.length));

                List<Card> hand = parseHand(CardString);
                gui.updatePlayerPanel(position, playerName, hand);
            }
        });
    }

    public void sendEmoji(String emojiPath, String clientName) {
        // PrintWriter를 사용하여 이모티콘 전송
        sendMessage("EMOJI|" + emojiPath + "|" + clientName);
        System.out.println("받은 이모티콘 경로: " + emojiPath);
    }

    public void sendSubmittedCard(Card card, String clientName) {
        sendMessage("SUBMITTED_CARD|" + card.getRank() + "|" + card.getSuit() + "|" + clientName);
        System.out.println("받은 카드: " + card);

    }

    public void requestCardFromDeck(String playerName) {
        sendMessage("DRAW_CARD|" + playerName);
        System.out.println("card_deck 가져간 플레이어: " + playerName);
    }

    public void onServerMessageReceived(String message) {
        System.out.println("서버로부터 메시지 받는거 클라이언트에서 확인: " + message);
        if (message.startsWith("EMOJI|")) {
            String[] parts = message.split("\\|");
            String emojiPath = parts[1];
            String clientName = parts[2];
            gui.handleIncomingEmoji(emojiPath, clientName); // UI에 애니메이션 반영
            System.out.println("수신한 이모티콘 경로: " + emojiPath + "|" + clientName);
        } else if (message.startsWith("SUBMITTED_CARD|")) {
            String[] parts = message.split("\\|");
            String rank = parts[1];
            String suit = parts[2];
            String newSuit = parts[3];
            Card card = new Card(rank, suit);
            hand.remove(card);
            System.out.println("손패에서 제거됨: " + card);
            SwingUtilities.invokeLater(() -> gui.updateHand(this.getName(), hand)); // UI 갱신
            if (newSuit.equals("NONE") ) {
                gui.updateSubmittedCard(card);
            }  
            else {
                Card newCard = new Card(rank, newSuit);
                gui.updateSubmittedCard(newCard);
            }
        } else if (message.startsWith("DRAW_CARD|")) {
            String[] parts = message.split("\\|");
            String rank = parts[1];
            String suit = parts[2];
            Card card = new Card(rank, suit);
            gui.updateDeckCard(card);
        } else if (message.startsWith("GAME_WINNER|")) {
            String[] parts = message.split("\\|");
            String winnerClient = parts[1];
            gui.endingGame(winnerClient);
        }
    }

    // 카드 제출 요청
    public void playCard(Card card, List<Card> hand, String playerName, Card newCard) {
        if (hand.contains(card)) {
            if (newCard == null) {
                System.out.println("제출 요청: " + card);
                sendMessage("SUBMITTED_CARD|" + card.getRank() + "|" + card.getSuit() + "|" + playerName + "|NONE");
            } else {
                System.out.println("제출 요청: " + newCard);
                sendMessage("SUBMITTED_CARD|" + card.getRank() + "|" + card.getSuit() + "|" + playerName + "|" + newCard.getSuit());
            }
        } else {
            System.out.println("손패에 없는 카드입니다! 제출 실패: " + card);
        }
    }

    public void setGamePanel(OneCardGameGUI gamePanel) {
        this.gui = gamePanel;
    }

    public void addCard(Card card) {
        hand.add(card);
        SwingUtilities.invokeLater(() -> {
            gui.updateHand(this.getName(), hand); // GUI 갱신
        });
    }

    public List<Card> getHand() {
        return hand;
    }

    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}