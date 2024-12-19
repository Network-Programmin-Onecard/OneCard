import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {
    private int arcWidth = 20; // 둥근 모서리 너비
    private int arcHeight = 20;

    public RoundedButton(String text) {
        super(text);
        setFocusPainted(false);      // 포커스 테두리 제거
        setBorderPainted(false);     // 테두리 제거
    }

    public void setRoundness(int arcWidth, int arcHeight) {
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 둥근 버튼 배경을 그리지 않음 (투명 처리)
        if (getModel().isPressed()) {
            g2.setColor(getBackground().darker());
        } else {
            g2.setColor(getBackground());
        }

        // 배경 그리기
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);

        // 텍스트 및 아이콘 렌더링
        if (getIcon() != null) {
            getIcon().paintIcon(this, g, (getWidth() - getIcon().getIconWidth()) / 2,
                    (getHeight() - getIcon().getIconHeight()) / 2);
        }

        if (getText() != null) {
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(getText());
            int textHeight = fm.getHeight();
            g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight / 4) / 2);
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 둥근 테두리 그리기
        g2.setColor(getForeground());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight);
    }
}