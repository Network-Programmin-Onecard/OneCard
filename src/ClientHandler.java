import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Server server;
    private final Game game;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;

    public ClientHandler(Socket socket, Server server, Game game) {
        this.socket = socket;
        this.server = server;
        this.game = game;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 클라이언트 입력 스트림
            out = new PrintWriter(socket.getOutputStream(), true); // 클라이언트 출력 스트림
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendEmojiToClient(String emojiPath, String clientName) {
        sendMessage("EMOJI|" + emojiPath + "|" + clientName); // "EMOJI|경로" 형식으로 클라이언트로 전송
    }

    public void sendSubmittedCardToClient(Card playerSubmittedCard, String clientName) {
        sendMessage("SUBMITTED_CARD|" + playerSubmittedCard.getRank() + "|" + playerSubmittedCard.getSuit() + "|"
                + clientName); //

    }

    public void sendCardDeckToClient(Card CardDeck, String clientName) {
        System.out.println("sendCardDeckToClient 정상 실행됨");
        System.out.println("DRAW_CARD|" + CardDeck.getRank() + "|" + CardDeck.getSuit() + "|" + clientName);
        sendMessage("DRAW_CARD|" + CardDeck.getRank() + "|" + CardDeck.getSuit() + "|" + clientName);
    }

    @Override
    public void run() {
        try {
            // 클라이언트의 이름을 수신
            clientName = in.readLine();
            System.out.println("클라이언트 연결: " + clientName);

            // 클라이언트로부터 메시지를 계속 수신
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("클라이언트 메시지: " + message);

                // 메시지에 따라 처리
                if (message.startsWith("EMOJI|")) {
                    // 이모티콘 처리
                    String[] parts = message.split("\\|");
                    String emojiPath = parts[1];
                    String clientName = parts[2];
                    server.broadcastEmoji(emojiPath, clientName); // 다른 클라이언트로 이모티콘 브로드캐스트
                } else if (message.startsWith("SUBMITTED_CARD|")) {
                    handleCardSumission(message);
                } else if (message.startsWith("DRAW_CARD|")) {
                    System.out.println(message + " 메시지 정상적으로 클라이언트 핸들러에 도착");
                    String playerName = message.split("\\|")[1]; // 플레이어 이름 추출
                    handleDrawCard(playerName);
                } else if (message.startsWith("TOP_SUBMITTED_CARD")) {
                    Card topCard = server.getTopSubmittedCard();
                    if (topCard != null) {
                        sendMessage("TOP_SUBMITTED_CARD|" + topCard.getRank() + "|" + topCard.getSuit());
                    } else {
                        sendMessage("ERROR|No top card available");
                    }
                } else if (message.equalsIgnoreCase("EXIT")) {
                    System.out.println(clientName + "가 연결을 종료했습니다.");
                    break; // 종료 요청 처리
                } else {
                    System.out.println("알 수 없는 메시지: " + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResources();
            server.removeClient(this); // 서버에서 클라이언트 제거
            System.out.println("클라이언트 " + clientName + " 연결 종료.");
        }
    }

    private void closeResources() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            System.out.println("리소스 정리 중 오류 발생: " + e.getMessage());
        }
    }

    private void handleCardSumission(String message) {
        try {
            String[] parts = message.split("\\|");
            String rank = parts[1];
            String suit = parts[2];
            String clientName = parts[3];
            Card card = new Card(rank, suit);
            Card topCard = server.getTopSubmittedCard();

            if (server.isPlayerTurn(clientName)) {

                if (card.getRank().equals(topCard.getRank()) || card.getSuit().equals(topCard.getSuit())) {
                    sendSubmittedCardToClient(card, clientName);
                    boolean isGameOver = server.handleCardSubmission(clientName, card);
                    if (isGameOver) {
                        server.broadcastMessage("GAME_WINNER|" + "Player " + clientName);
                    } else {
                        server.broadcastGameState();
                    }
                } else {
                    sendMessage("ERROR|카드의 Rank나 Suit값이 틀림");
                }

            } else {
                sendMessage("ERROR|현재 순서가 아님");
            }

        } catch (Exception e) {
            sendMessage("ERROR: 카드 제출 중 문제가 발생했습니다." + e.getMessage());
        }
    }

    private void handleDrawCard(String playerName) {
        synchronized (game) {
            if (server.isPlayerTurn(clientName)) {

                System.out.println("카드 덱의 잔여 카드 개수 draw 전전: " + game.getRemainingDeckSize());
                Card drawnCard = game.drawCardFromDeck(); // Deck에서 카드 한 장 추출
                System.out.println("카드 덱의 잔여 카드 개수 draw 후: " + game.getRemainingDeckSize());
                if (drawnCard != null) {
                    game.addCardToPlayerHand(playerName, drawnCard); // 플레이어 손패에 추가
                    server.broadcastGameState(); // 모든 클라이언트에 업데이트된 게임 상태 브로드캐스트
                    System.out.println("handleDrawCard에서 사용된 카드: " + drawnCard.getRank() + "-" + drawnCard.getSuit());
                    // server.handleCardDeck(playerName, drawnCard);
                    server.handleCardDeck(playerName);
                } else {
                    System.out.println("ERROR: Deck에 남은 카드가 없습니다.");
                }
            } else {
                sendMessage("ERROR|현재 순서가 아님");
            }
        }
    }

    public String serializeHand() {
        return game.serializeHand(clientName); // 게임에서 손패 직렬화
    }

    public String getClientName() {
        return clientName;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

}