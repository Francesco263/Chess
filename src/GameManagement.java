import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameManagement{

    Game chess;

    public GameManagement(){
        chess = new Game();
        chess.updateBoard();
        chess.getVictoryWindow().getSubmit().addActionListener(new VictoryListener());
    }
    class VictoryListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            chess.deactivateButtons();
            chess.getVictoryWindow().setVisible(false);
            chess.dispose();
            chess = new Game();
            chess.updateBoard();
            chess.getVictoryWindow().getSubmit().addActionListener(new VictoryListener());
        }
    }
}
