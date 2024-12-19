import javax.swing.*;
import java.awt.*;

public class ResultPanel extends JPanel {

    public ResultPanel(String winnerClient) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // JLayeredPane 생성
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1200, 800));

        // 배경 GIF 설정 (최하위 레이어)
        JLabel backgroundLabel = new JLabel(new ImageIcon("resources/result.gif"));
        backgroundLabel.setBounds(0, 0, 1200, 800); // 배경 이미지 크기 설정
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        // 승리 메시지 설정 (중간 레이어)
        JLabel winnerLabel = new JLabel("우승자: " + winnerClient, SwingConstants.CENTER);
        winnerLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 32));
        winnerLabel.setForeground(Color.BLACK);
        winnerLabel.setBounds(0, 500, 1200, 50); // 위치 및 크기 설정
        layeredPane.add(winnerLabel, JLayeredPane.PALETTE_LAYER);

        // 게임 종료 버튼 추가 (중간 레이어)
        JButton exitButton = new JButton("exit");
        exitButton.setBackground(Color.ORANGE);
        exitButton.setForeground(Color.WHITE);
        exitButton.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
        exitButton.setBounds(500, 650, 200, 50); // 위치 및 크기 설정
        exitButton.addActionListener(e -> {
            System.out.println("<<게임 종료 버튼 클릭됨>>");
            System.exit(0); // 프로그램 종료
        });
        layeredPane.add(exitButton, JLayeredPane.PALETTE_LAYER);

        add(layeredPane, BorderLayout.CENTER);
    }
}