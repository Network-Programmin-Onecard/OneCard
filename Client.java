import java.io.*;
import java.net.*;
import java.util.function.Consumer;

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

            // 별도의 스레드에서 사용자 목록 수신
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
    
                // UI 업데이트 콜백 호출
                SwingUtilities.invokeLater(() -> updateUserListCallback.accept(userList));
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
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}