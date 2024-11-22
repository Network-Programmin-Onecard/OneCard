import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OneCardGame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private StartPanel startPanel;
    private GamePanel gamePanel;
    private Client client;

    public OneCardGame() {
        setTitle("One Card Game");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        client = new Client(this::updateUserListInGamePanel);
        startPanel = new StartPanel(new StartButtonListener());
        cardPanel.add(startPanel, "Start");

        add(cardPanel);
        setVisible(true);
    }

    public void updateUserListInGamePanel(String userList) {
        if (gamePanel != null) {
            gamePanel.updateUserList(userList);
        }
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String userName = startPanel.getUserName();
            String port = startPanel.getPort();
            String ip = startPanel.getIp();

            if (userName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "User Name을 입력해주세요.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 서버에 연결 시도
            boolean connected = client.connect(ip, Integer.parseInt(port), userName);
            System.out.println("Connected status: " + connected);
            if (connected) {
                String userList = client.getUserList();
                gamePanel = new GamePanel(userName, userList);
                cardPanel.add(gamePanel, "Game");
                cardLayout.show(cardPanel, "Game");
            
                // 게임 화면 생성 후 즉시 사용자 목록 업데이트
                gamePanel.updateUserList(userList);
                System.out.println("GamePanel created and userList updated: " + userList);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OneCardGame::new);
    }
}