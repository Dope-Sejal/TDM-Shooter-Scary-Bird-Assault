import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class TDMGame extends JPanel implements ActionListener, KeyListener {

    final int WIDTH = 1200, HEIGHT = 700;

    int playerX, playerY, playerHealth, ammo;
    boolean up, down, left, right, reloading;
    boolean gameOver;
    int kills, matchTime, frameCount;

    int highScore = 0; // Track high score
    boolean newHighScore = false; // Flag to show message
    int highScoreDisplayFrames = 0; // Timer for message

    Random rand = new Random();
    String currentMap = "Spring";

    class Enemy {
        int x, y;
        int health = 3;
        int speed;
        boolean attacking = false;

        Enemy(int x, int y) {
            this.x = x;
            this.y = y;
            this.speed = 2 + rand.nextInt(2);
        }

        void move() {
            if (!attacking && rand.nextInt(200) == 0) attacking = true;

            if (attacking) {
                int dirX = playerX < x ? -1 : 1;
                int dirY = playerY < y ? -1 : 1;
                x += dirX * speed;
                y += dirY * speed;

                if (up || down || left || right) attacking = false;

                if (rand.nextInt(150) == 0) attacking = false;
            } else {
                x += (rand.nextBoolean() ? 1 : -1) * speed;
                y += (rand.nextBoolean() ? 1 : -1) * speed;
            }

            if (x < 0) x = 0; if (x > WIDTH - 50) x = WIDTH - 50;
            if (y < 50) y = 50; if (y > HEIGHT - 50) y = HEIGHT - 50;
        }
    }

    ArrayList<Enemy> enemies = new ArrayList<>();
    ArrayList<Rectangle> bullets = new ArrayList<>();

    javax.swing.Timer gameTimer;

    public TDMGame() {
        chooseMap();

        JFrame frame = new JFrame("Pro TDM Shooter – Scary Bird Attack");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.addKeyListener(this);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        startNewGame();

        gameTimer = new javax.swing.Timer(16, this); // ~60 FPS
        gameTimer.start();
    }

    private void chooseMap() {
        String[] options = {"Spring", "Summer", "Autumn", "Winter"};
        currentMap = (String) JOptionPane.showInputDialog(
                null,
                "Choose a map to play:",
                "Map Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        if (currentMap == null) currentMap = "Spring";
    }

    void startNewGame() {
        playerX = 100;
        playerY = 420;
        playerHealth = 100;
        ammo = 30;
        kills = 0;
        matchTime = 120;
        frameCount = 0;
        gameOver = false;
        reloading = false;
        newHighScore = false;
        highScoreDisplayFrames = 0;

        bullets.clear();
        enemies.clear();

        int attempts = 0;
        while (enemies.size() < 5 && attempts < 50) {
            int ex = 600 + rand.nextInt(WIDTH - 700);
            int ey = 100 + rand.nextInt(400);
            enemies.add(new Enemy(ex, ey));
            attempts++;
        }
    }

    void drawBackground(Graphics2D g) {
        switch (currentMap) {
            case "Spring":
                g.setColor(new Color(135, 206, 235));
                g.fillRect(0, 0, WIDTH, HEIGHT);
                g.setColor(new Color(60, 180, 75));
                g.fillRect(0, HEIGHT - 150, WIDTH, 150);
                g.setColor(Color.PINK);
                for (int i = 50; i < WIDTH; i += 100) g.fillOval(i, HEIGHT - 130, 10, 10);
                break;
            case "Summer":
                g.setColor(new Color(70, 200, 255));
                g.fillRect(0, 0, WIDTH, HEIGHT);
                g.setColor(new Color(40, 150, 40));
                g.fillRect(0, HEIGHT - 150, WIDTH, 150);
                g.setColor(Color.YELLOW);
                g.fillOval(WIDTH - 150, 50, 100, 100);
                break;
            case "Autumn":
                g.setColor(new Color(255, 165, 100));
                g.fillRect(0, 0, WIDTH, HEIGHT);
                g.setColor(new Color(200, 120, 40));
                g.fillRect(0, HEIGHT - 150, WIDTH, 150);
                g.setColor(Color.RED);
                for (int i = 0; i < 10; i++) g.fillOval(rand.nextInt(WIDTH), HEIGHT - 150 + rand.nextInt(50), 10, 10);
                break;
            case "Winter":
                g.setColor(new Color(200, 220, 255));
                g.fillRect(0, 0, WIDTH, HEIGHT);
                g.setColor(new Color(240, 240, 255));
                g.fillRect(0, HEIGHT - 150, WIDTH, 150);
                g.setColor(Color.WHITE);
                for (int i = 0; i < 50; i++) g.fillOval(rand.nextInt(WIDTH), rand.nextInt(HEIGHT), 5, 5);
                break;
        }

        // Fountain
        g.setColor(new Color(100, 100, 255));
        g.fillOval(WIDTH/2 - 50, HEIGHT - 120, 100, 50);
        g.setColor(Color.GRAY);
        g.fillRect(WIDTH/2 - 55, HEIGHT - 100, 110, 20);

        // Trees
        g.setColor(new Color(139, 69, 19));
        g.fillRect(100, HEIGHT - 250, 30, 100);
        g.setColor(Color.GREEN);
        g.fillOval(80, HEIGHT - 280, 70, 50);
        g.fillOval(90, HEIGHT - 300, 50, 50);

        g.setColor(new Color(139, 69, 19));
        g.fillRect(900, HEIGHT - 250, 30, 100);
        g.setColor(Color.GREEN);
        g.fillOval(880, HEIGHT - 280, 70, 50);
        g.fillOval(890, HEIGHT - 300, 50, 50);
    }

    void drawHuman(Graphics2D g, int x, int y) {
        g.setColor(new Color(255, 220, 180));
        g.fillOval(x + 15, y - 40, 30, 30);
        g.setColor(new Color(50, 50, 200));
        g.fillRect(x + 20, y - 10, 20, 50);
        g.setColor(new Color(50, 30, 20));
        g.fillRect(x + 20, y + 40, 10, 30);
        g.fillRect(x + 30, y + 40, 10, 30);
        g.setColor(new Color(80, 40, 20));
        g.fillRect(x + 35, y + 5, 50, 5);
        g.fillRect(x + 30, y, 10, 15);
        g.setColor(Color.BLACK);
        g.drawLine(x + 35, y + 5, x + 85, y + 5);
    }

    void drawScaryBird(Graphics2D g, int x, int y, boolean facingLeft) {
        int bodyWidth = 50, bodyHeight = 30;
        if (facingLeft) {
            g.setColor(new Color(20, 20, 20));
            g.fillOval(x, y - 30, bodyWidth, bodyHeight);
            g.setColor(new Color(40, 40, 40));
            g.fillOval(x - 15, y - 50, 20, 20);
            g.setColor(new Color(200, 100, 20));
            int[] bx = {x - 15, x - 30, x - 15};
            int[] by = {y - 45, y - 40, y - 35};
            g.fillPolygon(bx, by, 3);
            g.setColor(Color.RED);
            g.fillOval(x - 20, y - 48, 5, 5);
        } else {
            g.setColor(new Color(20, 20, 20));
            g.fillOval(x, y - 30, bodyWidth, bodyHeight);
            g.setColor(new Color(40, 40, 40));
            g.fillOval(x + 35, y - 50, 20, 20);
            g.setColor(new Color(200, 100, 20));
            int[] bx = {x + 55, x + 70, x + 55};
            int[] by = {y - 45, y - 40, y - 35};
            g.fillPolygon(bx, by, 3);
            g.setColor(Color.RED);
            g.fillOval(x + 45, y - 48, 5, 5);
        }
        g.setColor(new Color(30, 30, 30));
        int[] wx = {x + 10, x + 25, x + 5};
        int[] wy = {y - 20, y - 40, y - 35};
        g.fillPolygon(wx, wy, 3);
        g.setColor(new Color(150, 75, 0));
        g.fillRect(x + 10, y, 5, 15);
        g.fillRect(x + 35, y, 5, 15);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        drawBackground(g2);

        if (gameOver) {
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, WIDTH, HEIGHT);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            g2.drawString("GAME OVER", WIDTH / 2 - 120, HEIGHT / 2);
            g2.drawString("Press R to Restart", WIDTH / 2 - 180, HEIGHT / 2 + 60);
            return;
        }

        drawHuman(g2, playerX, playerY);

        for (Enemy e : enemies) {
            boolean facingLeft = e.x > playerX && e.attacking;
            drawScaryBird(g2, e.x, e.y, facingLeft);
        }

        g2.setColor(Color.BLACK);
        for (Rectangle b : bullets) g2.fill(b);

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(10, 10, 240, 110, 20, 20);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("HP: " + playerHealth, 20, 35);
        g2.drawString("Ammo: " + (reloading ? "Reloading" : ammo), 20, 60);
        g2.drawString("Kills: " + kills, 20, 85);
        g2.drawString("Time: " + matchTime, 20, 110);

        // Display new high score message
        if (newHighScore && highScoreDisplayFrames < 180) { // show ~3 seconds
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            g2.setColor(Color.YELLOW);
            g2.drawString("HURRAY!! NEW HIGH SCORE!!", WIDTH/2 - 250, 50);
            highScoreDisplayFrames++;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;

        frameCount++;
        if (frameCount % 60 == 0 && matchTime > 0) matchTime--;
        if (matchTime == 0) gameOver = true;

        if (up) playerY -= 5; if (down) playerY += 5;
        if (left) playerX -= 5; if (right) playerX += 5;

        bullets.removeIf(b -> b.x > WIDTH);
        for (Rectangle b : bullets) {
            b.x += 14;
            for (Enemy en : enemies) {
                if (b.intersects(new Rectangle(en.x, en.y - 40, 60, 90))) {
                    en.health--;
                    b.x = WIDTH;
                    if (en.health <= 0) {
                        kills++;
                        if (kills > highScore) { // New high score logic
                            highScore = kills;
                            newHighScore = true;
                            highScoreDisplayFrames = 0;
                        }
                        en.health = 3;
                        en.x = 600 + rand.nextInt(WIDTH - 700);
                        en.y = 100 + rand.nextInt(400);
                    }
                }
            }
        }

        for (Enemy en : enemies) {
            en.move();
            Rectangle birdRect = new Rectangle(en.x, en.y - 30, 50, 50);
            Rectangle playerRect = new Rectangle(playerX, playerY - 50, 50, 80);
            if (birdRect.intersects(playerRect)) {
                playerHealth -= 2;
                if (playerHealth <= 0) gameOver = true;
            }
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver && e.getKeyCode() == KeyEvent.VK_R) {
            chooseMap();
            startNewGame();
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> up = true;
            case KeyEvent.VK_S -> down = true;
            case KeyEvent.VK_A -> left = true;
            case KeyEvent.VK_D -> right = true;
            case KeyEvent.VK_SPACE -> {
                if (ammo > 0 && !reloading) {
                    bullets.add(new Rectangle(playerX + 80, playerY + 20, 16, 4));
                    ammo--;
                }
            }
            case KeyEvent.VK_R -> reload();
        }
    }

    void reload() {
        if (!reloading) {
            reloading = true;
            javax.swing.Timer t = new javax.swing.Timer(1500, e -> {
                ammo = 30;
                reloading = false;
            });
            t.setRepeats(false);
            t.start();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) up = false;
        if (e.getKeyCode() == KeyEvent.VK_S) down = false;
        if (e.getKeyCode() == KeyEvent.VK_A) left = false;
        if (e.getKeyCode() == KeyEvent.VK_D) right = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new TDMGame();
    }
}
