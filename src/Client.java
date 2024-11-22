import java.io.*;
import java.net.*;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String userList;
    private Consumer<String> updateUserListCallback;

    public Client(Consumer<String> updateUserListCallback) {
        this.updateUserListCallback = updateUserListCallback;
    }

    public boolean connect(String ip, int port, String userName) {
        try {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
    
            // 사용자 이름 전송
            out.println(userName);
    
            // 서버의 첫 번째 메시지를 동기적으로 수신 (초기 사용자 목록)
            String message = in.readLine();
            if (message != null && message.contains("접속할 수 없습니다")) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, message, "Connection Error", JOptionPane.ERROR_MESSAGE);
                });
                disconnect();
                return false; // 연결 실패
            }
    
            // 초기 사용자 목록 설정
            userList = message;
            System.out.println("Initial userList received: " + userList);
    
            // 메시지 수신 스레드 시작
            new Thread(this::receiveMessages).start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                userList = message;
                System.out.println("Received userList: " + userList);

                // UI 스레드에서 업데이트
                SwingUtilities.invokeLater(() -> {
                    if (updateUserListCallback != null) {
                        updateUserListCallback.accept(userList);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserList() {
        return userList;
    }

    public void disconnect() {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}