import javax.swing.*;
import java.awt.*;

public class VictoryWindow extends JFrame {

    JPanel mainPanel = new JPanel(new BorderLayout());
    JLabel winLabel = new JLabel();
    JButton submit = new JButton("reset game");
    JPanel buttonPanel = new JPanel(new BorderLayout());

    public VictoryWindow(){
        getContentPane().add(mainPanel);
        winLabel.setText("");
        winLabel.setFont(new Font("sunserif", Font.BOLD, 16));
        mainPanel.add(winLabel, BorderLayout.NORTH);
        buttonPanel.add(submit, BorderLayout.EAST);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.setBackground(Color.decode("#ffd39b"));
        mainPanel.setBackground(Color.decode("#ffd39b"));

        setVisible(false);
        setTitle("checkmate!");
        setSize(300,100);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    public JButton getSubmit() {
        return submit;
    }
    public JLabel getWinLabel() {
        return winLabel;
    }
}
