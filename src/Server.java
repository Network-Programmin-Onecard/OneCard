import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 30000;
    private static final int MAX_CLIENTS = 4;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final Game game;

    public Server() {
        game = new Game();
    }

    public static void main(String[] args) {
        new Server().start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("서버가 시작되었습니다. 포트 번호: " + PORT);
    
            while (true) {
                Socket clientSocket = serverSocket.accept();
                synchronized (clients) {
                    if (clients.size() >= MAX_CLIENTS) {
                        rejectClient(clientSocket);
                        continue;
                    }
    
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this, game);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();
    
                    // 모든 클라이언트가 연결되었을 때 게임 시작
                    if (clients.size() == MAX_CLIENTS) {
                        new Thread(() -> {
                            try {
                                Thread.sleep(100); // 1초 대기
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                    
                            synchronized (game) {
                                List<String> playerNames = new ArrayList<>();
                                for (ClientHandler client : clients) {
                                    playerNames.add(client.getClientName());
                                }
                                game.startGame(playerNames); // 게임 초기화
                            }
                            broadcastGameState(); // 초기 게임 상태 전송
                        }).start();
                       }
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void rejectClient(Socket clientSocket) {
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            out.println("접속할 수 없습니다. 최대 접속 인원 초과.");
        } catch (IOException ignored) {
        }
    }

    public void broadcastGameState() {
        // GAME_STATE 메시지 생성 및 전송
        StringBuilder state = new StringBuilder("GAME_STATE:");
        synchronized (clients) {
            int index = 0;
            for (ClientHandler client : clients) {
                String clientName = client.getClientName();
                String serializedHand = client.serializeHand();
                if (serializedHand == null || serializedHand.isEmpty()) {
                    System.out.println("손패 데이터가 없습니다: " + clientName);
                    continue;
                }
                state.append(index).append(",")
                     .append(clientName).append(",")
                     .append(serializedHand).append(";");
                index++;
            }
        }
    
        String gameState = state.toString();
        System.out.println("Broadcasting Game State: " + gameState); // 디버깅 출력
        for (ClientHandler client : clients) {
            client.sendMessage(gameState); // GAME_STATE 메시지 전송
        }
    
        // REMAINING_CARDS 메시지 생성 및 전송
        synchronized (game) {
            List<Card> remainingDeck = game.getSuffledDeck(); // 남은 카드 가져오기
            if (!remainingDeck.isEmpty()) {
                String submittedCard = remainingDeck.get(0).toString();
                String cardDeckTop = remainingDeck.size() > 1 ? remainingDeck.get(1).toString() : "Empty";
                String remainingCardsMessage = "REMAINING_CARDS:" + submittedCard + "," + cardDeckTop;
                System.out.println("Broadcasting Remaining Cards: " + remainingCardsMessage); // 디버깅 출력
                for (ClientHandler client : clients) {
                    client.sendMessage(remainingCardsMessage); // REMAINING_CARDS 메시지 전송
                }
            }
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        synchronized (clients) {
            clients.remove(clientHandler);
        }
        broadcastGameState();
    }

    public void broadcastMessage(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(message); // 각 클라이언트에 메시지 전송
            }
        }
    }

    public void broadcastEmoji(String emojiPath, String clientName) {
        synchronized (clients) {
            System.out.println("Broadcasted Emoji: " + emojiPath + "|" + clientName);
            for (ClientHandler client : clients) {
                client.sendEmojiToClient(emojiPath, clientName);
            }
        }
    }

    public void broadcastSubmittedCard(Card card, String playerName) {
        synchronized (clients) {
            System.out.println("SUBMITTED_CARD|" + card.getRank() + "|" + card.getSuit() + "|" + playerName);
            for (ClientHandler client : clients) {
                client.sendSubmittedCardToClient(card, playerName);
            }
        }
    }
}