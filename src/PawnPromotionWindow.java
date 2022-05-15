import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PawnPromotionWindow extends JFrame {

    String[] players = new String[]{"r", "q", "h", "b"};
    String color;
    JPanel mainPanel = new JPanel(new BorderLayout());
    JPanel topPanel = new JPanel();
    JPanel bottomPanel = new JPanel(new GridLayout(1,4));
    JLabel info = new JLabel("Choose a Player");
    private String selectedPlayer;
    private Method method;

    public PawnPromotionWindow(String color){
        this.color = color;
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        topPanel.add(info);
        info.setFont(new Font("sunserif", Font.BOLD, 16));
        topPanel.setBackground(Color.decode("#ffd39b"));
        mainPanel.setBackground(Color.decode("#ffd39b"));
        for (int i = 0; i < 4; i++){
            JButton button = new JButton();
            button.setBackground(Color.decode("#ffd39b"));
            button.setIcon(new ImageIcon("files/" + color + players[i] + ".png"));
            button.setName(Integer.toString(i));
            button.addActionListener(new Listener());
            bottomPanel.add(button);
        }
        setSize(465,150);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setTitle("Pawn Promotion");
        setResizable(false);
        setVisible(true);
    }
    class Listener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            selectedPlayer = color + players[Integer.parseInt(button.getName())];
            method.fillerMethod();
        }
    }
    public void setMethod(Method method) {
        this.method = method;
    }
    public String getSelectedPlayer() {
        return selectedPlayer;
    }
}
