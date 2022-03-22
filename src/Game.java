import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

public class Game extends JFrame {
    String[] board = new String[]{
            "-r","-h","-b","-k","-q","-b","-h","-r",
            "-p","-p","-p","-p","-p","-p","-p","-p",
            " "," "," "," "," "," "," "," ",
            " "," "," "," "," "," "," "," ",
            " "," "," "," "," "," "," "," ",
            " "," "," "," "," "," "," "," ",
            "_p","_p","_p","_p","_p","_p","_p","_p",
            "_r","_h","_b","_q","_k","_b","_h","_r"
    };
    public int buttonCount = 0;
    public int cntr = 1;
    public int player = 0;
    public int index1;
    public int index2;
    ArrayList<JButton> buttons = new ArrayList<>(64);
    JPanel mainPanel = new JPanel(new GridLayout(8,8));
    public Game(){
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        for (int i = 0; i<8; i++){
            if (i%2==0){
                createBoard(1);
            }
            else{
                createBoard(2);
            }
        }
        setTitle("Chess v1.01");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800,800);
        setResizable(false);
    }
    public void createBoard(int set){
        for (int i = 0; i < 8; i++){
            JButton button = new JButton();
            button.setName(Integer.toString(buttonCount++));
            button.addActionListener(new Listener());
            if (i%2 == 0){
                if (set==1){
                    button.setBackground(Color.decode("#eddacb"));
                }
                else{
                    button.setBackground(Color.decode("#d8aa76"));
                }
            }
            else{
                if (set==1){
                    button.setBackground(Color.decode("#d8aa76"));
                }
                else{
                    button.setBackground(Color.decode("#eddacb"));
                }
            }
            mainPanel.add(button);
            buttons.add(button);
        }
    }
    public void updateBoard(){
        for (int i = 0; i < 64; i++){
            buttons.get(i).setIcon(new ImageIcon("files/" + board[i] + ".png"));
            buttons.get(i).setBorder(null);
            buttons.get(i).setEnabled(false);
            if (player%2==0){
                if (board[i].contains("_")){
                    buttons.get(i).setEnabled(true);
                }
            }
            else{
                if (board[i].contains("-")){
                    buttons.get(i).setEnabled(true);
                }
            }
        }
    }
    class Listener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            if (cntr%2 == 0){
                index2 = Integer.parseInt(button.getName());
                if (index1 != index2){
                    performPlay(index1,index2);
                    cntr++;
                    buttons.get(index2).setEnabled(false);
                }
                else{
                    cntr--;
                    player--;
                    updateBoard();
                }
            }
            else{
                index1 = Integer.parseInt(button.getName());
                if (board[index1].contains("_")){
                    markFields(validation(index1,-1, "_"), index1);
                }
                else{
                    markFields(validation(index1,1, "-"), index1);
                }
                cntr++;
                player++;
            }
        }
    }
    public void performPlay(int index1, int index2){
        board[index2] = board[index1];
        board[index1] = " ";
        updateBoard();
    }
    public Vector<Integer> validation(int index1, int operator, String color){
        Vector<Integer> validMoves = new Vector<>();
        if (board[index1].equals("_p") || board[index1].equals("-p")){
            if (operator == -1){
                if (index1 >= 48 && index1 <= 55 && board[index1-16].equals(" ") && board[index1-16] != null){
                    validMoves.add(-16);
                }
            }
            else{
                if (index1 >= 8 && index1 <= 15 && board[index1+16].equals(" ") && board[index1+16] != null){
                    validMoves.add(16);
                }
            }
            validMoves = pawn(validMoves,operator, color);
        }
        else if (board[index1].equals("_r") || board[index1].equals("-r")){
            validMoves = rook(validMoves, operator, color);
            validMoves = rook(validMoves, operator*-1, color);
        }
        else if (board[index1].equals("_b") || board[index1].equals("-b")){

        }
        return validMoves;
    }
    public void markFields(Vector<Integer> validMoves, int index1){
        for (int i = 0; i < 64; i++){
            buttons.get(i).setEnabled(false);
        }
        buttons.get(index1).setEnabled(true);
        for (int i = 0; i < validMoves.size(); i++){
            if (board[index1+validMoves.get(i)] != null){
                buttons.get(index1+validMoves.get(i)).setEnabled(true);
                if (board[index1+validMoves.get(i)].equals(" ")){
                    buttons.get(index1+validMoves.get(i)).setIcon(new ImageIcon("files/valid.png"));
                }
                else{
                    buttons.get(index1+validMoves.get(i)).setBorder(BorderFactory.createLineBorder(Color.RED));
                }
            }
        }
    }
    public Vector<Integer> pawn(Vector<Integer> validMoves, int operator, String color){
        if (board[index1+(8*operator)].equals(" ") && board[index1+(8*operator)] != null){
            validMoves.add(8*operator);
        }
        if ((index1+(9*operator)%8 != 0 && (index1+(6*operator))%8 != 0)){
            if (!board[index1+(7*operator)].equals(" ") && !board[index1+(7*operator)].contains(color) && board[index1+(7*operator)] != null){
                validMoves.add(7*operator);
            }
            if(!board[index1+(9*operator)].equals(" ") && !board[index1+(9*operator)].contains(color) && board[index1+(9*operator)] != null){
                validMoves.add(9*operator);
            }
        }
        return validMoves;
    }
    public Vector<Integer> rook(Vector<Integer> validMoves, int operator, String color){
        int[] borderRight = new int[]{0,8,16,24,32,40,48,56};
        int[] borderLeft = new int[]{7,15,23,31,39,47,55, 63};
        int validIndex = index1;
        int i = 1;
        while (validIndex+(8*operator) <= 63 && validIndex+(8*operator) >= 0 && board[validIndex+(8*operator)].contains(" ")){
            validMoves.add((8*operator)*i);
            validIndex = validIndex+(8*operator);
            i++;
        }
        if (index1+((8*operator)*i) >= 0 && index1+((8*operator)*i) <= 63 && !board[index1+((8*operator)*i)].contains(color)){
            validMoves.add((8*operator)*i);
        }
        if (operator == 1){
            validMoves = roookCheckBorders(validMoves, borderLeft, index1, 1, color);
        }
        else{
            validMoves = roookCheckBorders(validMoves, borderRight, index1, -1, color);
        }
        return validMoves;
    }
    public Vector<Integer> roookCheckBorders(Vector<Integer> validMoves, int[] array, int index1, int operator, String color){
        int validIndex = index1;
        int check = 0;
        int check2 = 0;
        int check3 = 0;
        int i = 1;
        while(validIndex+(1*operator) <= 63 && validIndex+(1*operator) >= 0 && !board[validIndex+(1*operator)].contains(color) && board[validIndex+(1*operator)].contains(" ")){
            for (int h = 0; h < array.length; h++){
                if (validIndex+(1*operator) == array[h]+(1*operator)){
                    check = 1;
                }
                if (index1+((i+1)*operator) == array[h]+(1*operator)){
                    check2 = 1;
                }
                if (index1+(1*operator) == array[h]+(1*operator)){
                    check3 = 1;
                }
            }
            if (check!= 1){
                validMoves.add(i*(operator));
                if (check2 != 1 && !board[index1+((i+1)*operator)].contains(color) && !board[index1+((i+1)*operator)].contains(" ")){
                    validMoves.add((i+1)*(operator));
                }
            }
            i++;
            validIndex = validIndex + (1*operator);
        }
        if(check3 != 1){
            if (index1+(1*operator) >= 0 && index1+(1*operator) <= 63){
                if (!board[index1+(1*operator)].contains(color) && (!board[index1+(1*operator)].contains(" "))){
                    validMoves.add(1*operator);
                }
            }
        }
        return validMoves;
    }
}
