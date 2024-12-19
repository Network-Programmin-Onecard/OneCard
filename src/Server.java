import java.io.*;
import java.net.*;
import java.util.*;

enum Course {
    SEQUENCE, REVERSE;
}

public class Server {
    private static final int PORT = 30000;
    private static final int MAX_CLIENTS = 4;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final Game game;
    String submittedCard;
    private int clientNumber = 0; // 현재 클라이언트의 순서
    private Course course = Course.SEQUENCE;

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
                    new Thread(clientHandler).start();
                    clients.add(clientHandler);
                    System.out.println("클라이언트 추가됨. 게임 시작 전전: " + clients.size()); // 로그 추가

                    // 모든 클라이언트가 연결되었을 때 게임 시작
                    if (clients.size() == MAX_CLIENTS) {
                        startNewGame(); // 게임 시작
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
    }

    private void startNewGame() {
        // 게임 상태를 초기화하고 시작
        resetGame(); // 게임 상태 초기화
        System.out.println("4명의 클라이언트가 접속했습니다. 새로운 게임을 시작합니다.");

        new Thread(() -> {
            try {
                Thread.sleep(100); // 0.1초 대기
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
            initializeGameState();
        }).start();
    }

    private void resetGame() {
        synchronized (game) {
            game.reset(); // 게임 상태 리셋
            clientNumber = 0;
            ClientHandler.resetTopCard();
            System.out.println("게임 상태가 초기화되었습니다.");
        }
    }

    private void initializeGameState() {
        synchronized (game) {
            List<Card> remainingDeck = game.getRemainingDeck(); // 남은 카드 가져오기
            if (!remainingDeck.isEmpty()) {
                submittedCard = game.getSubmittedCard().getTopCard().toString();
                String cardDeckTop = remainingDeck.size() > 0 ? remainingDeck.get(0).toString() : "Empty";
                String remainingCardsMessage = "REMAINING_CARDS:" + submittedCard + "," + cardDeckTop;
                System.out.println(remainingCardsMessage); // 디버깅 출력
                for (ClientHandler client : clients) {
                    client.sendMessage(remainingCardsMessage); // REMAINING_CARDS 메시지 전송
                }
            }
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        synchronized (clients) {
            clients.remove(clientHandler);
            System.out.println("클라이언트가 나갔습니다. 남은 클라이언트 수: " + clients.size());

            // 모든 클라이언트가 나간 경우 게임 상태 초기화
            if (clients.isEmpty()) {
                System.out.println("모든 클라이언트가 연결을 종료했습니다. 게임을 초기화합니다.");
                resetGame(); // 게임 상태 초기화
            }
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

    public void broadcastWinnter(ClientHandler winner) {
        String winnerMessage = "GAME_WINNER|" + winner.getClientName();
        System.out.println(winnerMessage);

        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(winnerMessage);
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

    public synchronized boolean handleCardSubmission(String name, Card card) {
        try {
            for (ClientHandler client : clients) {
                client.sendSubmittedCardToClient(card, name, null);
            }
            List<Card> remainingDeck = game.getRemainingDeck();
            submittedCard = game.updateSubmittedCard(card).toString();
            String cardDeckTop = remainingDeck.size() > 0 ? remainingDeck.get(0).toString() : "Empty";
            String remainingCardsMessage = "REMAINING_CARDS:" + submittedCard + "," + cardDeckTop;
            System.out.println("Broadcasting Remaining Cards: " + remainingCardsMessage); // 디버깅 출력
            for (ClientHandler client : clients) {
                client.sendMessage(remainingCardsMessage); // REMAINING_CARDS 메시지 전송
            }
            return game.playTurn(name, card);
        } catch (IllegalStateException e) {
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
    }

    public synchronized void handleCardDeck(String name) {
        Card card = game.getFirstCardDeck();
        try {
            for (ClientHandler client : clients) {
                client.sendCardDeckToClient(card, name);
            }
        } catch (IllegalStateException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    public synchronized Card getTopSubmittedCard() {
        System.out.println("서버에서 클라이언트로 전달할 제출된 카드의 맨 위 카드 : " + game.getTopSubmittedCard());
        return game.getTopSubmittedCard(); // Game 클래스의 getTopSubmittedCard 호출
    }

    public boolean isPlayerTurn(String playerName) {
        if (clients.get(clientNumber).getClientName().equals(playerName)) {
            System.out.println("현재 순서인 플레이어 이름: " + clients.get(clientNumber).getClientName() + " 번호: " + clientNumber);
            System.out.println("*************** 현재 clientNumber : " + clientNumber + " ***********************");
            return true;
        } else {
            System.out.println("현재 플레이어의 턴이 아닙니다.");
            return false;
        }
    }

    public void NextTurn(){
        if (course == Course.SEQUENCE) {
            if (clientNumber == 3) {
                clientNumber = 0;
            } else {
                clientNumber++;
            }
        } else if (course == Course.REVERSE) {
            if (clientNumber == 0) {
                clientNumber = 3;
            } else {
                clientNumber--;
            }
        }
    }

    public void AceAbility() {

    }

    public void KingAbility() {
        if (course == Course.SEQUENCE) {
            if (clientNumber == 0) {
                clientNumber = 3;
            } else {
                clientNumber--;
            }
        } else if (course == Course.REVERSE) {
            if (clientNumber == 3) {
                clientNumber = 0;
            } else {
                clientNumber++;
            }
        }
    }

    public void QueenAbility() {
        if (course == Course.SEQUENCE) {
            course = Course.REVERSE;
            if (clientNumber == 0) {
                clientNumber = 2;
            } else if(clientNumber == 1){
                clientNumber = 3;
            } else {
                clientNumber -= 2;
            }
        } else {
            course = Course.SEQUENCE;
            if (clientNumber == 3) {
                clientNumber = 1;
            } else if(clientNumber == 2){
                clientNumber = 0;
            } else {
                clientNumber += 2;
            }
        }
    }

    public void JackAbility() {
        if (course == Course.SEQUENCE) {
            clientNumber ++;
        } else if (course == Course.REVERSE) {
            clientNumber --;
        }
    }

    public void SevenAbility() {

    }

    public synchronized void isDeckhaveOneCard() { // ******************* 수정필요 ********************
        if (game.getDeckSize() == 1) {
            game.getDeck().replenishFromSubmittedCards(game.getSubmittedCard());
        }
    }
}