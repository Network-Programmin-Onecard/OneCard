import java.io.*;
import java.net.*;
//import java.util.function.Consumer;
//import javax.swing.SwingUtilities;
import java.util.List;
import java.util.ArrayList;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String userList;
    //private Consumer<String> updateUserListCallback;
    private String name = "default";
    private List<Card> hand = new ArrayList<>();

    public Client() {
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
                disconnect();
                return false; // 연결 실패
            }
    
            // 초기 사용자 목록 설정
            userList = message;
            System.out.println("Initial userList received: " + userList);
    
            // 메시지 수신 스레드 시작 -> 게임도 하나의 스레드로 생각해서 이와 같이 설정하면 될 듯
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

                // // UI 스레드에서 업데이트
                // SwingUtilities.invokeLater(() -> {
                //     if (updateUserListCallback != null) {
                //         updateUserListCallback.accept(userList);
                //     }
                // });
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

    public void setName(String name) {
        this.name = name;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public String getName() {
        return name;
    }

    public void addCard(Card card) {
        hand.add(card);
    }


    public List<Card> getHand() {
        return hand;
    }
}