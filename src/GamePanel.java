package src;
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private JTextArea userListArea;

    public GamePanel(String userName, String userList) {
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome, " + userName + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));

        userListArea = new JTextArea(userList);
        userListArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(userListArea);

        add(welcomeLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateUserList(String userList) {
        userListArea.setText(userList);
    }
}
