package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class StartPanel extends JPanel {

    private JTextField portField;
    private JTextField ipField;
    private JTextField userField;
    private JButton startButton;
    private JLabel titleLabel;
    private JLabel portLabel;
    private JLabel ipLabel;
    private JLabel userLabel;
    private Image backgroundImage;

    public StartPanel(ActionListener startAction) {
        // 배경 이미지 로드
        backgroundImage = new ImageIcon("resources/background.png").getImage();

        // 레이아웃 설정 (null Layout)
        setLayout(null);

        // 제목 라벨
        titleLabel = new JLabel("원카드 게임", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLACK);
        add(titleLabel);

        // 포트 번호 입력 필드
        portLabel = new JLabel("Port Number:");
        add(portLabel);
        portField = new JTextField("30000", 15);
        add(portField);

        // IP 주소 입력 필드
        ipLabel = new JLabel("IP Address:");
        add(ipLabel);
        ipField = new JTextField("127.0.0.1", 15);
        add(ipField);

        // 사용자 이름 입력 필드
        userLabel = new JLabel("User Name:");
        add(userLabel);
        userField = new JTextField(15);
        add(userField);

        // Game Start 버튼
        startButton = new JButton("GAME START");
        startButton.setBackground(Color.ORANGE);
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        startButton.addActionListener(startAction);
        add(startButton);

        // 초기 위치 및 크기 설정
        resizeComponents();

        // 창 크기 변경 리스너 추가
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });
    }

    // 창 크기에 맞춰 컴포넌트 위치 및 크기 재조정
    private void resizeComponents() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
    
        // 상대적인 위치 및 크기 계산
        int centerX = panelWidth / 2;
        int baseY = (int) (panelHeight * 0.8); // 화면 높이의 70% 지점 기준
    
        // 제목 라벨 위치
        int titleWidth = 300;
        int titleHeight = 50;
        titleLabel.setBounds(centerX - titleWidth / 2, 50, titleWidth, titleHeight);
    
        // 입력 필드 위치
        int labelWidth = 100;
        int fieldWidth = 150;
        int fieldHeight = 25;
        int spacing = 40;
    
        // 입력 필드와 버튼 위치 조정
        portLabel.setBounds(centerX - labelWidth - 10, baseY - (2 * spacing), labelWidth, fieldHeight);
        portField.setBounds(centerX, baseY - (2 * spacing), fieldWidth, fieldHeight);
    
        ipLabel.setBounds(centerX - labelWidth - 10, baseY - spacing, labelWidth, fieldHeight);
        ipField.setBounds(centerX, baseY - spacing, fieldWidth, fieldHeight);
    
        userLabel.setBounds(centerX - labelWidth - 10, baseY, labelWidth, fieldHeight);
        userField.setBounds(centerX, baseY, fieldWidth, fieldHeight);
    
        // Game Start 버튼 위치
        int buttonWidth = 200;
        int buttonHeight = 50;
        startButton.setBounds(centerX - buttonWidth / 2, baseY + spacing, buttonWidth, buttonHeight);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int imageWidth = backgroundImage.getWidth(this);
        int imageHeight = backgroundImage.getHeight(this);

        // 이미지 비율 유지하며 그리기
        double panelAspect = (double) panelWidth / panelHeight;
        double imageAspect = (double) imageWidth / imageHeight;

        int drawWidth;
        int drawHeight;

        if (panelAspect > imageAspect) {
            drawHeight = panelHeight;
            drawWidth = (int) (imageAspect * drawHeight);
        } else {
            drawWidth = panelWidth;
            drawHeight = (int) (drawWidth / imageAspect);
        }

        int x = (panelWidth - drawWidth) / 2;
        int y = (panelHeight - drawHeight) / 2;
        g.drawImage(backgroundImage, x, y, drawWidth, drawHeight, this);
    }

    public String getUserName() {
        return userField.getText();
    }

    public String getPort() {
        return portField.getText();
    }

    public String getIp() {
        return ipField.getText();
    }
}