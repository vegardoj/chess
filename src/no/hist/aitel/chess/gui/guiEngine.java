package no.hist.aitel.chess.gui;

/**
 *
 * @author Vegard
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import no.hist.aitel.chess.board.Board;
import static no.hist.aitel.chess.gui.guiConstants.*;

public class guiEngine extends JFrame implements MouseListener, MouseMotionListener {

    private boolean canDrag = true;
    private Chessboard boardGui = new Chessboard();
    private Board board = new Board();
    private getRect getRect = new getRect();
    private int[] x_coords = boardGui.getXcoords();
    private int[] y_coords = boardGui.getYcoords();
    private int dragFromX = zero;
    private int dragFromY = zero;
    private int movingPiece = -1;
    private int capturedPiece = -1;
    private int x_coordStartPos = -1;
    private int y_coordStartPos = -1;
    private int capturedWhitePieces = zero;
    private int capturedBlackPieces = zero;
    private int x;
    private int y;
    private int fromPos;
    private int toPos;
    private String player1 = null;
    private String player2 = null;
    private JLabel player1Label;
    private JLabel player2Label;
    private int capturedPos = -1;
    private JLabel stopWatchP1 = new JLabel("", JLabel.LEFT);
    private JLabel stopWatchP2 = new JLabel("", JLabel.LEFT);
    Font player = new Font("VERDANA", Font.BOLD, 15);
    Font timer = new Font("VERDANA", Font.ITALIC, 10);

    private int getCapturedPos() {
        return capturedPos;
    }

    private void setCapturedPos(int capturedPos) {
        this.capturedPos = capturedPos;
    }

    public guiEngine(String title) {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        players();

        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel northPanel = new JPanel(new BorderLayout());

        player1Label = new JLabel(player1, JLabel.LEFT);
        player2Label = new JLabel(player2, JLabel.LEFT);

        player1Label.setFont(player);
        //player1Label.setBorder(BorderFactory.createTitledBorder("Player White"));

        player2Label.setFont(player);
        //player2Label.setBorder(BorderFactory.createTitledBorder("Player Black"));

        stopWatchP1.setFont(timer);
        stopWatchP1.setBorder(BorderFactory.createEmptyBorder());

        stopWatchP2.setFont(timer);
        stopWatchP2.setBorder(BorderFactory.createEmptyBorder());

        southPanel.add(boardGui, BorderLayout.CENTER);
        southPanel.add(player1Label, BorderLayout.SOUTH);
        southPanel.add(stopWatchP1, BorderLayout.SOUTH);

        northPanel.add(player2Label, BorderLayout.NORTH);
        northPanel.add(stopWatchP2, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.CENTER);
        add(northPanel, BorderLayout.NORTH);

        Runnable runThis = new Runnable () {
            public void run () {
                int i = 0;
                while(true) {
                    try {
                        Thread.currentThread().sleep(1000);
                        stopWatchP1.setText("   " + i++ + " sek");
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        new Thread(runThis).start();

        Runnable runThis1 = new Runnable () {
            public void run () {
                int i = 0;
                while(true) {
                    try {
                        Thread.currentThread().sleep(1000);
                        stopWatchP2.setText("   " + i++ + " sek");
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };

        new Thread(runThis1).start();




        //add(new Buttons(), BorderLayout.SOUTH);
        //add(boardGui, BorderLayout.CENTER);
        pack();
    }

    public Chessboard getChessboard() {
        return boardGui;
    }

    public void setP1name(String newName) {
        player1Label.setText(newName);
    }

    public void setP2name(String newName) {
        player2Label.setText(newName);
    }

    private void players() {
        player1 = "Player 1";
        player2 = "Player 2";
    }

    public void undoMove() {
        //
    }

    public void newgame() {
        //
        this.repaint();
    }

    public void mousePressed(MouseEvent e) {
        x = e.getX();
        y = e.getY();

        try {
            for (int i = 0; i < 64; i++) {
                if (x - xIn > x_coords[i] && x - xIn < x_coords[i] + (width) && y - 60 > y_coords[i] && y - 60 < y_coords[i] + (height)) {
                    movingPiece = i;
                    canDrag = true;
                    dragFromX = x - x_coords[i];
                    dragFromY = y - y_coords[i];
                    break;
                } else {
                    movingPiece = -1;
                    canDrag = false;
                }
            }
            x_coordStartPos = x;
            y_coordStartPos = y;
        } catch (ArrayIndexOutOfBoundsException outOfBoundsException) {
            System.out.println(outOfBoundsException);
        }
    }

    public void mouseDragged(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        if (canDrag) {
            x_coords[movingPiece] = x - dragFromX;
            y_coords[movingPiece] = y - dragFromY;

            x_coords[movingPiece] = Math.max(x_coords[movingPiece], zero);
            x_coords[movingPiece] = Math.min(x_coords[movingPiece], getWidth() - 400);

            y_coords[movingPiece] = Math.max(y_coords[movingPiece], 50);
            y_coords[movingPiece] = Math.min(y_coords[movingPiece], getWidth() - 340);


        }

        System.out.println(x + " " + y);
        this.repaint();
    }

    /**
     * Resets the piece position if the move is invalid
     */
    private void resetPosition() {
        x_coords[movingPiece] = getRect.getRectCoordX(fromPos);
        y_coords[movingPiece] = getRect.getRectCoordY(fromPos);
    }

    /**
     * Changes the piece position
     */
    private void changePosition() {
        x_coords[movingPiece] = getRect.getRectCoordX(toPos);
        y_coords[movingPiece] = getRect.getRectCoordY(toPos);
    }

    public void mouseReleased(MouseEvent e) {
        if (canDrag) {
            int x_on_release = e.getX();
            int y_on_release = e.getY();
            fromPos = getRect.getRectNumber(x_coordStartPos, y_coordStartPos);
            toPos = getRect.getRectNumber(x_on_release, y_on_release);

            try {
                capturedPiece = board.getPiece(toPos).getId();
                System.out.println(capturedPiece);
                board.movePiece(fromPos, toPos);
                setCapturedPos(board.getPiece(toPos).getId());
                changePosition();
            } catch (IllegalArgumentException exception) { // IllegalArgumentException = alle våre exceptions
                System.out.println(exception.getMessage());
                resetPosition();
                setCapturedPos(-1);
            } catch (ArrayIndexOutOfBoundsException exception) {
                System.out.println(exception.getMessage());
                resetPosition();
                setCapturedPos(-1);
            }

            // Fin shø, andre betingelsen hindrer at brikken "capture"-er seg selv
            if (getCapturedPos() > -1 && board.getPiece(fromPos).getId() != getCapturedPos()) {
//            System.out.println("Captured pos: " + getCapturedPos());
//            System.out.println("toPos ID: " + board.getPiece(toPos).getId());
//            System.out.println("fromPos ID: " + board.getPiece(fromPos).getId());
                System.out.println(capturedPiece);
                if (capturedPiece == 61) {
                    capturedPiece = 5;
                    try {
                        x_coords[capturedPiece] = capturedBlackPieces * width;
                        y_coords[capturedPiece] = height * 9;
                        capturedBlackPieces++;
                        setCapturedPos(-1);
                    } catch (ArrayIndexOutOfBoundsException excep) {
                    }
                } else if (capturedPiece == 5) {
                    capturedPiece = 61;
                    try {
                        x_coords[capturedPiece] = capturedWhitePieces * width;
                        y_coords[capturedPiece] = zero;
                        capturedWhitePieces++;
                        setCapturedPos(-1);
                    } catch (ArrayIndexOutOfBoundsException excep) {
                    }
                } else if (capturedPiece == 58) {
                    capturedPiece = 2;
                    try {
                        x_coords[capturedPiece] = capturedBlackPieces * width;
                        y_coords[capturedPiece] = height * 9;
                        capturedBlackPieces++;
                        setCapturedPos(-1);
                    } catch (ArrayIndexOutOfBoundsException excep) {
                    }
                } else if (capturedPiece == 2) {
                    capturedPiece = 58;
                    try {
                        x_coords[capturedPiece] = capturedWhitePieces * width;
                        y_coords[capturedPiece] = zero;
                        capturedWhitePieces++;
                        setCapturedPos(-1);
                    } catch (ArrayIndexOutOfBoundsException excep) {
                    }
                } else if (capturedPiece >= 48) {
                    try {
                        x_coords[capturedPiece] = capturedBlackPieces * width;
                        y_coords[capturedPiece] = height * 9;
                        capturedBlackPieces++;
                        setCapturedPos(-1);
                    } catch (ArrayIndexOutOfBoundsException excep) {
                    }

                } else if (capturedPiece <= 15) {
                    try {
                        x_coords[capturedPiece] = capturedWhitePieces * width;
                        y_coords[capturedPiece] = zero;
                        capturedWhitePieces++;
                        setCapturedPos(-1);
                    } catch (ArrayIndexOutOfBoundsException excep) {
                    }
                }
            }
            this.repaint();
            System.out.println(board.toString());
        }
    }

    public void mouseExited(MouseEvent e) {
        canDrag = false;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public class Buttons extends JPanel {

        public Buttons() {
            Buttonlistener listener = new Buttonlistener();

            setLayout(new FlowLayout());

            JButton buttonOne = new JButton("New game");
            add(buttonOne);
            JButton buttonTwo = new JButton("Undo move");
            add(buttonTwo);
            JButton buttonThree = new JButton("Highscore");
            add(buttonThree);

            buttonOne.addActionListener(listener);
            buttonTwo.addActionListener(listener);
            buttonThree.addActionListener(listener);

        }
    }

    public class Chessboard extends JPanel {

        private JLayeredPane layeredPane;
        private drawBoard chessBoard;
        private drawPos startPos = new drawPos();
        private int[] x_coords = startPos.getXcoords();
        private int[] y_coords = startPos.getYcoords();
        private int xSize = 800;
        private int ySize = 600;

        public Chessboard() {
            Dimension boardSize = new Dimension(xSize, ySize);
            layeredPane = new JLayeredPane();
            getContentPane().add(layeredPane);
            layeredPane.setPreferredSize(boardSize);
            chessBoard = new drawBoard();
            chessBoard.setLayout(new GridLayout(8, 8));
            chessBoard.setPreferredSize(boardSize);
            startPos.initStartCoords();
            startPos.setBounds(5, 0, boardSize.width, boardSize.height);
            chessBoard.setBounds(5, 0, boardSize.width, boardSize.height);
            chessBoard.setOpaque(true);
            layeredPane.add(chessBoard, JLayeredPane.DEFAULT_LAYER);
            startPos.setOpaque(false);
            layeredPane.add(startPos, JLayeredPane.PALETTE_LAYER);
            add(layeredPane);

        }

        public drawBoard getDrawBoard() {
            return chessBoard;
        }

        public void setXsize(int size) {
            xSize = size;

        }

        public void setYsize(int size) {
            ySize = size;

        }

        public int[] getXcoords() {
            return x_coords;
        }

        public int[] getYcoords() {
            return y_coords;
        }
    }

    private class Buttonlistener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            String commando = event.getActionCommand();

            if (commando.equals("New game")) {
                if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, "Are you sure you want to create a new game?\nUnsaved progress will be lost.")) {
                    newgame();
                }
            } else if (commando.equals("Undo move")) {
                try {
                    undoMove();
                } catch (ArrayIndexOutOfBoundsException outOfBoundsException) {
                    System.out.println("test");
                }
            //JOptionPane.showMessageDialog(null, "Du angret et trekk");
            } else if (commando.equals("Highscore")) {
                JOptionPane.showMessageDialog(null, "Highscore:");
            }
        }
    }
}
