import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 30000;
    private static final int MAX_CLIENTS = 4;

    private final List<ClientHandler> clients = new ArrayList<>();
    private final Game game;

    public Server() {
        game = new Game(); // Game 객체 생성
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
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();
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

    private void broadcastGameState() {
        StringBuilder state = new StringBuilder();
        state.append("GAME_STATE: 현재 턴: ").append(game.getCurrentPlayer()).append("\n");
        state.append("제출된 카드: ").append(game.getSubmittedCard().getTopCard()).append("\n");
    
        for (String player : game.getPlayers()) {
            state.append(player).append(": ").append(game.getPlayerHand(player).size()).append("장\n");
        }
    
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(state.toString());
                client.sendHandUpdate(game.getPlayerHand(client.clientName)); // 각 클라이언트 손패 전송
            }
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // 클라이언트 이름 수신 및 게임에 추가
                clientName = in.readLine();
                synchronized (game) {
                    game.startGame(List.of(clientName)); // Game에 추가
                }

                broadcastGameState(); // 상태 브로드캐스트

                // 클라이언트 요청 처리 루프
                String input;
                while ((input = in.readLine()) != null) {
                    if (input.startsWith("PLAY_CARD")) {
                        handlePlayCard(input);
                    }
                }
            } catch (IOException e) {
                System.out.println(clientName + " 연결 종료");
            } finally {
                removeClient(this);
            }
        }

        private void handlePlayCard(String input) {
            String cardInfo = input.substring(10); // "PLAY_CARD " 이후 카드 정보 추출
            try {
                synchronized (game) {
                    Card card = parseCard(cardInfo);
                    boolean gameOver = game.playTurn(clientName, card);
                    if (gameOver) {
                        broadcastMessage(clientName + "가 승리했습니다!");
                    } else {
                        broadcastGameState(); // 상태 업데이트
                    }
                }
            } catch (IllegalStateException e) {
                sendMessage("ERROR: " + e.getMessage()); // 클라이언트에 에러 메시지 전달
            }
        }

        private String serializeHand(List<Card> hand) {
            StringBuilder sb = new StringBuilder();
            for (Card card : hand) {
                sb.append(card.getRank()).append(" ").append(card.getSuit()).append(",");
            }
            return sb.toString();
        }

        private Card parseCard(String cardInfo) {
            String[] parts = cardInfo.split(" ");
            return new Card(parts[0], parts[1], ""); // 이미지 경로는 불필요
        }

        private void removeClient(ClientHandler clientHandler) {
            synchronized (clients) {
                clients.remove(clientHandler);
            }
            synchronized (game) {
                game.getPlayers().remove(clientName); // 게임에서도 제거
            }
            broadcastGameState();
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        private void broadcastMessage(String message) {
            synchronized (clients) {
                for (ClientHandler client : clients) {
                    client.sendMessage(message);
                }
            }
        }

        public void sendHandUpdate(List<Card> hand) {
            StringBuilder handMessage = new StringBuilder("HAND_UPDATE:");
            for (Card card : hand) {
                handMessage.append(card.getRank()).append(" ").append(card.getSuit()).append(",");
            }
            sendMessage(handMessage.toString());
        }
    }
}
