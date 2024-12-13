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
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            clientName = in.readLine(); // 클라이언트 이름 수신
            game.addPlayer(clientName); // 게임에 플레이어 추가

            server.broadcastGameState();

            String input;
            while ((input = in.readLine()) != null) {
                if (input.startsWith("PLAY_CARD")) {
                    handlePlayCard(input);
                }
            }
        } catch (IOException e) {
            System.out.println(clientName + " 연결 종료");
        } finally {
            server.removeClient(this); // 클라이언트 제거
            game.removePlayer(clientName); // 게임에서도 제거
        }
    }

    private void handlePlayCard(String input) {
        String cardInfo = input.substring(10); // "PLAY_CARD " 이후 카드 정보 추출
        try {
            Card card = parseCard(cardInfo);
            boolean gameOver = game.playTurn(clientName, card);
            if (gameOver) {
                server.broadcastGameState();
                server.broadcastMessage(clientName + "가 승리했습니다!");
            } else {
                server.broadcastGameState();
            }
        } catch (IllegalStateException e) {
            sendMessage("ERROR: " + e.getMessage());
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

    private Card parseCard(String cardInfo) {
        String[] parts = cardInfo.split(" ");
        return new Card(parts[0], parts[1], "");
    }
}