import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final List<ClientHandler> clients = new ArrayList<>();
    private static final int PORT = 30000;
    private static final int MAX_CLIENTS = 4;

    // 연결된 사용자 목록 반환
    public static String getUserList() {
        StringBuilder userList = new StringBuilder("현재 접속한 사용자: ");
        synchronized (clients) {
            for (ClientHandler client : clients) {
                userList.append(client.getUserName()).append(", ");
            }
        }
        return userList.toString();
    }

    // 모든 클라이언트에게 사용자 목록 전송 (모든 클라이언트에게 공통 정보 전송할 때 유용)
    public static void broadcastUserList() {
        String userList = getUserList();
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(userList);
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String userName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // 사용자 이름 수신
                userName = in.readLine();
                System.out.println(userName + "님이 접속하였습니다.");

                // 새로운 클라이언트에게 즉시 사용자 목록 전송
                out.println(getUserList());

                // 모든 클라이언트에게 업데이트된 사용자 목록 전송
                broadcastUserList();

            } catch (IOException e) {
                System.out.println(userName + "님이 연결을 종료하였습니다.");
            } finally {
                // 연결 종료 시 클라이언트를 리스트에서 제거
                removeClient(this);
                broadcastUserList();
            }
        }

        public String getUserName() {
            return userName;
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }

    private static void removeClient(ClientHandler clientHandler) {
        synchronized (clients) {
            clients.remove(clientHandler);
        }
        System.out.println("현재 접속한 사용자 목록: " + getUserList());
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("서버가 시작되었습니다. 포트 번호: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                synchronized (clients) {
                    // 현재 접속 인원 체크
                    if (clients.size() >= MAX_CLIENTS) {
                        System.out.println("최대 접속 인원 초과: 새로운 클라이언트의 접속을 거부합니다.");
                        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                            out.println("서버에 접속할 수 없습니다. 최대 4명의 클라이언트만 접속 가능합니다.");
                            // 메시지 전송 후 잠시 대기 (클라이언트가 메시지를 읽을 수 있도록)
                            Thread.sleep(500);
                        } catch (InterruptedException ignored) {}
                        clientSocket.close();
                        continue;
                    }

                    // 새 클라이언트 접속 허용 -> 게임에 대한 정보 처리도 이와 같이 처리해야할 것으로 예상
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}