package brickBracker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePlay extends JPanel implements KeyListener, ActionListener {
    private boolean play = false; // is the game currently in play?
    private int score = 0;
    private int brickRowCount = 5;
    private int brickColCount = 9;
    private int totalBricks = brickColCount * brickRowCount; // current available bricks that can be hit

    private Timer timer;
    private int delay = 5;

    private int playerX = 310; // player's board, initial position, playerY always in the height of board

    private int ballposX = 120;
    private int ballposY = 350;
    private int ballXdir = -1;
    private int ballYdir = -1;

    //make the bricks
    private MapGenerator map;

    public GamePlay() {
        map = new MapGenerator(brickRowCount, brickColCount);
        addKeyListener(this); //??
        setFocusable(true); //??
        setFocusTraversalKeysEnabled(false); //??
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paint(Graphics g) {
        //background
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);

        map.draw((Graphics2D)g); //??

        //borders
        g.setColor(Color.yellow);
        //the bottom does not have a border
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        //scores
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("" + score, 590, 30); //top right corner

        //the paddle
        g.setColor(Color.green);
        g.fillRect(playerX, 550, 100, 8);

        //the ball
        g.setColor(Color.yellow);
        g.fillOval(ballposX, ballposY, 20, 20);

        //check for win
        if (totalBricks <= 0) {
            play = false;
            ballXdir = 0; //position will still update, but just will update by 0
            ballYdir = 0;
            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("You Won: ", 260, 300);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
        }

        //check for fail
        if (ballposY > 570) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString(("Game Over, Scores: "+ score), 190, 300);

            g.setFont(new Font("serif", Font.BOLD, 28));
            g.drawString(("Press Enter to restart: "), 230, 350);
        }

        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();
        if (play) {
            //detect the catching board
            if (new Rectangle(ballposX, ballposY, 20, 20).intersects((new Rectangle(playerX, 550, 100, 8)))) {
                ballYdir = -ballYdir;
            }

            //make bricks disappear
            A: for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80; //because of index
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);
                        Rectangle brickRect = rect;

                        if (ballRect.intersects((brickRect))) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 5;

                            if (ballposX + 19 <= brickRect.x || ballposX + 1 >= brickRect.x + brickRect.width) {
                                ballXdir = -ballXdir;
                            } else {
                                ballYdir = -ballYdir;
                            }

                            break A;
                        }
                    }
                }
            }

            ballposX += ballXdir;
            ballposY += ballYdir;
            System.out.println(ballXdir);
            if (ballposX < 0) {
                ballXdir = -ballXdir;
            }
            if (ballposY < 0) {
                ballYdir = -ballYdir;
            }
            if (ballposX > 670) {
                ballXdir = -ballXdir;
            }
            //why not check ballposY > 390??
        }
        repaint(); //call paint()
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //we will only use the arrow keys
        if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 600) {
                playerX = 600;
            } else {
                moveRight();
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX < 10) {
                playerX = 10;
            } else {
                moveLeft();
            }

        }
        if(e.getKeyCode() == KeyEvent.VK_ENTER) { //start or restart the game
            if (!play) {
                play = true;
                ballposX = 120;
                ballposY = 350;
                ballXdir = -1;
                ballYdir = -1;
                playerX = 310;
                score = 0;
                totalBricks = 21;
                map = new MapGenerator(brickRowCount, brickColCount);

                repaint();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void moveLeft() {
        play = true;
        playerX -= 20;
    }

    public void moveRight() {
        play = true;
        playerX += 20;
    }

}
