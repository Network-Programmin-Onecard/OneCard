
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private JTextArea userListArea;

    public GamePanel(String userName, String userList) {
        setLayout(new BorderLayout());

        // 사용자 환영 메시지
        JLabel welcomeLabel = new JLabel("Welcome, " + userName + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));

        // 연결된 사용자 목록 표시
        userListArea = new JTextArea(userList);
        userListArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(userListArea);

        add(welcomeLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // 사용자 목록 업데이트 메서드
    public void updateUserList(String userList) {
        userListArea.setText(userList);
    }
}
