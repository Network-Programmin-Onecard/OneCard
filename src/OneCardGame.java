import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OneCardGame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private StartPanel startPanel;
    private OneCardGameGUI gamePanel;
    private Client client;

    public OneCardGame() {
        setTitle("One Card Game");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        startPanel = new StartPanel(new StartButtonListener());
        cardPanel.add(startPanel, "Start");

        add(cardPanel);
        setVisible(true);
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
    
            try {
                client = new Client();
                gamePanel = new OneCardGameGUI(client); // 게임 패널 생성
                client.setGUI(gamePanel); // GUI를 Client에 연결
    
                boolean connected = client.connect(ip, Integer.parseInt(port), userName, gamePanel);
    
                if (connected) {
                    cardPanel.add(gamePanel, "Game");
                    cardLayout.show(cardPanel, "Game");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "포트 번호는 숫자여야 합니다.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "오류가 발생했습니다.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OneCardGame::new);
    }
}