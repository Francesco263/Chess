import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

public class Game extends JFrame {
    private String[] board = new String[]{
            "-r","-h","-b","-q","-k","-b","-h","-r",
            "-p","-p","-p","-p","-p","-p","-p","-p",
            " "," "," "," "," "," "," "," ",
            " "," "," "," "," "," "," "," ",
            " "," "," "," "," "," "," "," ",
            " "," "," "," "," "," "," "," ",
            "_p","_p","_p","_p","_p","_p","_p","_p",
            "_r","_h","_b","_q","_k","_b","_h","_r"
    };

    private int[] borderRight = new int[]{0,8,16,24,32,40,48,56};
    private int[] borderLeft = new int[]{7,15,23,31,39,47,55, 63};
    private Vector<String> deadPlayers = new Vector<>();
    private JPanel deadPlayerBoard = new JPanel(new GridLayout(2,1));
    private JPanel deadPLayerBoardWhite = new JPanel(new GridLayout(0,5));
    private JPanel deadPLayerBoardBlack = new JPanel(new GridLayout(0,5));
    private JPanel panelClock = new JPanel(new GridLayout(2,1));
    private JPanel panelClockWhite = new JPanel();
    private JPanel panelClockBlack = new JPanel();
    private int[] temp = new int[]{99};
    private int buttonCount = 0;
    private int cntr = 1;
    private int player = 0;
    private int index1;
    private int index2;
    private ArrayList<JButton> buttons = new ArrayList<>(64);
    private JPanel mainPanel = new JPanel(new GridLayout(8,8));
    private JPanel basePanel = new JPanel(new BorderLayout());
    private boolean castling1 = true;
    private boolean castling2 = true;
    private boolean castling = false;
    private boolean castlingLeftClear = false;
    private boolean castlingRightClear = false;
    private boolean enPassant = false;
    private int enPassantPosition;
    private boolean performEnPassant = false;
    private boolean pawnPromotion = false;

    public Game(){
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(basePanel, BorderLayout.CENTER);
        getContentPane().add(panelClock, BorderLayout.WEST);
        basePanel.add(mainPanel, BorderLayout.WEST);
        basePanel.add(deadPlayerBoard, BorderLayout.CENTER);
        deadPlayerBoard.add(deadPLayerBoardWhite);
        deadPlayerBoard.add(deadPLayerBoardBlack);
        deadPLayerBoardBlack.setBorder((BorderFactory.createLineBorder(Color.GRAY)));
        deadPLayerBoardWhite.setBorder((BorderFactory.createLineBorder(Color.GRAY)));
        deadPLayerBoardBlack.setBackground(Color.decode("#ffe4c4"));
        deadPLayerBoardWhite.setBackground(Color.decode("#ffe4c4"));
        panelClock.add(panelClockWhite);
        panelClock.add(panelClockBlack);
        panelClockBlack.setBorder((BorderFactory.createLineBorder(Color.GRAY)));
        panelClockWhite.setBorder((BorderFactory.createLineBorder(Color.GRAY)));
        panelClockBlack.setBackground(Color.decode("#ffe4c4"));
        panelClockWhite.setBackground(Color.decode("#ffe4c4"));
        for (int i = 0; i<8; i++){
            if (i%2==0){
                createBoard(1);
            }
            else{
                createBoard(2);
            }
        }
        setTitle("Chess v1.41");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel.setPreferredSize(new Dimension(800,800));
        panelClock.setPreferredSize(new Dimension(400,800));
        setSize(1600, 800);
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
            buttons.get(i).setDisabledIcon(new ImageIcon("files/" + board[i] + ".png"));
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

                    checkReadyForCastling(index1, index2);
                    checkReadyForEnPassant(index1, index2);
                    checkPawnPromotion(index1, index2);

                    performPlay(index1,index2);

                    if (castling){
                        castling(index1, index2);
                        castling = false;
                    }
                    if (pawnPromotion){
                        performPawnPromotion(index1, index2);
                        pawnPromotion = false;
                    }

                    cntr++;
                    buttons.get(index2).setEnabled(false);
                }
                else{
                    cntr--;
                    player--;
                    castling = false;
                    performEnPassant = false;
                    updateBoard();
                }
            }
            else{
                index1 = Integer.parseInt(button.getName());
                if (board[index1].contains("_")){
                    Vector<Integer> validMoves = validation(index1,-1, "_");
                    validMoves = validateValidMoves(validMoves, -1, "_");
                    markFields(validMoves, index1);
                }
                else {
                    Vector<Integer> validMoves = validation(index1,1, "-");
                    validMoves = validateValidMoves(validMoves, 1, "-");
                    markFields(validMoves, index1);
                }
                cntr++;
                player++;
            }
        }
    }
    public void performPlay(int index1, int index2){
        if (performEnPassant){
            if ((index1 > index2) && index2 == enPassantPosition-8){
                addToKillBoard(board[index2+8]);
                board[index2+8] = " ";
            }
            else if (index2 == enPassantPosition+8){
                addToKillBoard(board[index2-8]);
                board[index2-8] = " ";
            }
            performEnPassant = false;
        }
        addToKillBoard(board[index2]);
        board[index2] = board[index1];
        board[index1] = " ";
        updateBoard();
    }
    public void checkReadyForCastling(int index1, int index2){
        if (board[index1].contains("_k") || board[index1].contains("_r")){
            castling1 = false;
        }
        else if (board[index1].contains("-k") || board[index1].contains("-r")){
            castling2 = false;
        }
    }
    public void checkReadyForEnPassant(int index1, int index2){
        if(enPassant && (board[index1].contains("_") && !board[enPassantPosition].contains("_")) || ((board[index1].contains("-") && !board[enPassantPosition].contains("-")))){
            enPassant = false;
        }
        if (board[index1].contains("p") || board[index1].contains("P")){
            if ((board[index1].contains("_") && index1-16 == index2) || (board[index1].contains("-") && index1+16 == index2)){
                enPassantPosition = index2;
                enPassant = true;
            }
        }
    }
    public void checkPawnPromotion(int index1, int index2){
        if (pawnPromotion){
            pawnPromotion = false;
        }
        else{
            if ((board[index1].contains("_p") && index2 >= 0 && index2 <= 7) || (board[index1].contains("-p") && index2 >= 56 && index2 <= 63)){
                pawnPromotion = true;
            }
        }
    }
    public void castling(int index1, int index2){
        if (index2 > index1 && castlingRightClear){
            board[index2-1] = board[index2+1];
            board[index2+1] = " ";
        }
        else if(castlingLeftClear){
            board[index2+1] = board[index2-2];
            board[index2-2] = " ";
        }
        updateBoard();
    }
    public void markFields(Vector<Integer> validMoves, int index1){
        deactivateButtons();
        buttons.get(index1).setEnabled(true);
        for (int i = 0; i < validMoves.size(); i++){
            if (board[index1+validMoves.get(i)] != null){
                buttons.get(index1+validMoves.get(i)).setEnabled(true);
                if (board[index1+validMoves.get(i)].equals(" ")){
                    buttons.get(index1+validMoves.get(i)).setIcon(new ImageIcon("files/valid.png"));
                    if ((validMoves.get(i) == 2 || validMoves.get(i) == -2) && castling){
                        buttons.get(index1+validMoves.get(i)).setBorder(BorderFactory.createLineBorder(Color.GREEN));
                    }
                }
                else{
                    buttons.get(index1+validMoves.get(i)).setBorder(BorderFactory.createLineBorder(Color.RED));
                }
            }
        }
    }
    public Vector<Integer> validation(int index1, int operator, String color){
        Vector<Integer> validMoves = new Vector<>();
        switch (board[index1]) {
            case "_p":
            case "-p":
                pawn(validMoves, operator, color);
                break;
            case "_r":
            case "-r":
                prepareRook(validMoves, color, false, false);
                break;
            case "_b":
            case "-b":
                prepareBishop(validMoves, color, false, false);
                break;
            case "_q":
            case "-q":
                prepareRook(validMoves, color, false, false);
                prepareBishop(validMoves, color, false, false);
                break;
            case "_k":
            case "-k":
                prepareBishop(validMoves, color, true, false);
                prepareRook(validMoves, color, true, false);
                castling(index1, operator, color, validMoves);
                break;
            case "_h":
            case "-h":
                prepareHorse(validMoves, color, true, true);
                break;
        }
        return validMoves;
    }
    public void prepareHorse(Vector<Integer> validMoves, String color, boolean king, boolean horse){
        int operator = 1;
        calculateMove(validMoves, operator, color, 17, borderRight, king, horse);
        calculateMove(validMoves, operator, color, 10, borderRight, king, horse);
        calculateMove(validMoves, operator, color, 15, borderLeft, king, horse);
        calculateMove(validMoves, operator, color, 6, borderLeft, king, horse);
        calculateMove(validMoves, -1, color, 17, borderLeft, king, horse);
        calculateMove(validMoves, -1, color, 10, borderLeft, king, horse);
        calculateMove(validMoves, -1, color, 15, borderRight, king, horse);
        calculateMove(validMoves, -1, color, 6, borderRight, king, horse);
    }
    public void prepareRook(Vector<Integer> validMoves, String color, boolean king, boolean horse){
        int operator = 1;
        calculateMove(validMoves, operator, color, 1, borderRight, king, horse);
        calculateMove(validMoves, operator, color, 8, temp, king, horse);
        calculateMove(validMoves, -1, color, 1, borderLeft, king, horse);
        calculateMove(validMoves, -1, color, 8, temp, king, horse);
    }
    public void prepareBishop(Vector<Integer> validMoves, String color, boolean king, boolean horse){
        int operator = 1;
        calculateMove(validMoves, operator, color, 9, borderRight, king, horse);
        calculateMove(validMoves, operator, color, 7, borderLeft, king, horse);
        calculateMove(validMoves, -1, color, 9, borderLeft, king, horse);
        calculateMove(validMoves, -1, color, 7, borderRight, king, horse);
    }
    public void pawn(Vector<Integer> validMoves, int operator, String color){
        if (operator == -1){
            pawnMoves(validMoves, operator, color, 48, 55, borderLeft, borderRight);
        }
        else{
            pawnMoves(validMoves, operator, color, 8, 15, borderRight, borderLeft);
        }
        if (enPassant){
            if ((board[index1].contains("_") && !board[enPassantPosition].contains("_")) || ((board[index1].contains("-") && !board[enPassantPosition].contains("-")))){
                if (enPassantPosition+1 == index1 || enPassantPosition-1 == index1){
                    if (!((checkBorder(index1, borderLeft) && checkBorder(enPassantPosition, borderRight)) || (checkBorder(index1, borderRight) && checkBorder(enPassantPosition, borderLeft)))){
                        if (operator == -1 && enPassantPosition > index1){
                            validMoves.add(-7);
                            buttons.get(index1+1).setBorder(BorderFactory.createLineBorder(Color.GREEN));
                        }
                        else if (operator == -1){
                            validMoves.add(-9);
                            buttons.get(index1-1).setBorder(BorderFactory.createLineBorder(Color.GREEN));
                        }
                        if (operator == 1 && enPassantPosition < index1){
                            validMoves.add(7);
                            buttons.get(index1-1).setBorder(BorderFactory.createLineBorder(Color.GREEN));
                        }
                        else if (operator == 1){
                            validMoves.add(9);
                            buttons.get(index1+1).setBorder(BorderFactory.createLineBorder(Color.GREEN));
                        }
                        performEnPassant = true;
                    }
                }
            }
            else{
                enPassant = false;
            }
        }
    }
    public void pawnMoves(Vector<Integer> validMoves, int operator, String color, int num1, int num2, int[] arr1, int[] arr2){
        if (index1 >= num1 && index1 <= num2 && board[index1+(16*operator)].equals(" ") && board[index1+(16*operator)] != null  && board[index1+(8*operator)].equals(" ")){
            validMoves.add(16*operator);
        }
        if (!((operator == -1 && index1-9 < 0) || (operator == 1 && index1+9 > 63))){
            if (!board[index1+(9*operator)].equals(color) && !board[index1+9*operator].equals(" ")){
                calculateMove(validMoves, operator, color, 9, arr1, true, false);
            }
        }
        if (!board[index1+(7*operator)].equals(color) && !board[index1+(7*operator)].equals(" ")){
            calculateMove(validMoves, operator, color, 7, arr2, true, false);
        }
        if (board[index1+(8*operator)].equals(" ")){
            calculateMove(validMoves, operator, color, 8, temp, true, false);
        }
    }
    public void castling(int index1, int operator, String color, Vector<Integer> validMoves){
        if (color.equals("_") && castling1){
            calculateCastling(index1, operator, color, validMoves);
            castling = true;
        }
        else if (color.equals("-") && castling2){
            calculateCastling(index1, operator, color, validMoves);
            castling = true;
        }
    }
    public void calculateCastling(int index1, int operator, String color, Vector<Integer> validMoves){
        if (board[index1+1].contains(" ") && board[index1+2].contains(" ")){
            validMoves.add(2);
            castlingRightClear = true;
        }
        if (board[index1-1].contains(" ") && board[index1-2].contains(" ") && board[index1-3].contains(" ")){
            validMoves.add(-2);
            castlingLeftClear = true;
        }
    }
    public void calculateMove(Vector<Integer> validMoves, int operator, String color, int number, int[] array, boolean king, boolean horse){
        int validIndex = index1;
        int check = 0;
        int i = 1;
        if (!horse){
            if (((array[0] == 0 && operator == -1 && checkBorder(validIndex, array))||(array[0] == 7 && operator == 1 && checkBorder(validIndex, array))) && number != 8 && number != 7){
                return;
            }
        }
        else{
            if ((checkBorder(index1, borderRight) && (((operator == 1 && number == 6) || (operator == -1 && number == 10)))) || (checkBorder(index1, borderLeft) && (((operator == -1 && number == 6) || (operator == 1 && number == 10))))){
                return;
            }
        }
        while(validIndex+(number*operator) >= 0 && validIndex+(number*operator) <= 63 && !board[validIndex+(number*operator)].contains(color) && !checkBorder(validIndex + (number * operator), array)){
            if (board[validIndex+(number*operator)].contains(" ")){
                validMoves.add((i*number)*operator);
            }
            if (!board[validIndex+(number*operator)].contains(" ") && !board[validIndex+(number*operator)].contains(color)){
                validMoves.add((i*number)*operator);
                break;
            }
            i++;
            validIndex = validIndex + (number*operator);
            if (king){
                break;
            }
        }
    }
    public void performPawnPromotion(int index1, int index2){
        deactivateButtons();
        char color1 = board[index2].charAt(0);
        String color = Character.toString(color1);
        PawnPromotionWindow pawnPromotion = new PawnPromotionWindow(color);
        Method transmition = new Method() {
            @Override
            public void fillerMethod() {
                board[index2] = pawnPromotion.getSelectedPlayer();
                updateBoard();
                pawnPromotion.dispose();
            }
        };
        pawnPromotion.setMethod(transmition);
    }
    public boolean checkBorder(int validIndex, int[] array){
        for (int i = 0; i < array.length; i++){
            if (validIndex == array[i]){
                return true;
            }
        }
        return false;
    }
    public void deactivateButtons(){
        for(int i = 0; i < 64; i++){
            buttons.get(i).setEnabled(false);
        }
    }
    public void addToKillBoard(String deadPlayer){
        JLabel label = new JLabel();
        ImageIcon icon = new ImageIcon("files/" + deadPlayer + ".png");
        label.setIcon(icon);
        if (deadPlayer.contains("_")){
            deadPLayerBoardWhite.add(label);
        }
        else if (deadPlayer.contains("-")){
            deadPLayerBoardBlack.add(label);
        }
    }
    public Vector<Integer> validateValidMoves(Vector<Integer> validMoves, int operator, String color){
        /*
        Vector <Integer> testMoves = new Vector<>();
        int tempIndex = index1;
        String tempColor = color;

        if (color.contains("-")){
            color = "_";
        }
        else{
            color = "-";
        }
        operator = operator*-1;

        if (board[index1].contains("k")){
            for (int i = 0; i < 64; i++){
                if (board[i].contains(color)){
                    index1 = i;
                    testMoves = validation(index1, operator, color);
                    for (int f = 0; f < validMoves.size(); f++){
                        for (int h = 0; h < testMoves.size(); h++){
                            if (validMoves.get(f) + tempIndex == testMoves.get(h) + index1){
                                validMoves.remove(f);
                            }
                        }
                    }
                }
            }
        }

        index1 = tempIndex;
        color = tempColor;
        operator = operator*-1;

         */
        return validMoves;
    }
}

