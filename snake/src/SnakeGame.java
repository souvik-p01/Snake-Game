import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x;
        int y;
        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    int boardWidth;
    int boardHeight;
    int tileSize = 25;
    //snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    //food
    Tile food;
    Random random;

    //game Logic
    Timer gameLoop;
    int velocityX;
    int velocityY;
    boolean gameOver = false;
    
    // Colors
    Color backgroundColor = new Color(40, 40, 40);
    Color gridColor = new Color(50, 50, 50);
    Color snakeHeadColor = new Color(50, 205, 50); // Bright green
    Color snakeBodyColor = new Color(34, 139, 34); // Dark green
    Color foodColor = new Color(220, 20, 60);      // Crimson
    
    // Animation
    long lastMoveTime;
    int animationSpeed = 100; // milliseconds
    int growthCounter = 0;
    
    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(backgroundColor);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();

        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 0;
        velocityY = 1;
        
        lastMoveTime = System.currentTimeMillis();
        gameLoop = new Timer(16, this); // ~60 FPS for smoother animation
        gameLoop.start();
    } 

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        draw(g2d);
    }

    public void draw(Graphics2D g) {
        // Draw checkered background
        for(int i = 0; i < boardWidth/tileSize; i++) {
            for(int j = 0; j < boardHeight/tileSize; j++) {
                if((i + j) % 2 == 0) {
                    g.setColor(new Color(35, 35, 35));
                } else {
                    g.setColor(new Color(45, 45, 45));
                }
                g.fillRect(i * tileSize, j * tileSize, tileSize, tileSize);
            }
        }

        // Grid lines (very subtle)
        g.setColor(gridColor);
        for(int i = 0; i <= boardWidth/tileSize; i++) {
            g.drawLine(i*tileSize, 0, i*tileSize, boardHeight);
        }
        for(int i = 0; i <= boardHeight/tileSize; i++) {
            g.drawLine(0, i*tileSize, boardWidth, i*tileSize);
        }

        // Food with glow effect
        drawFood(g);

        // Snake body with 3D effect
        for(int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            drawSnakePart(g, snakePart.x, snakePart.y, snakeBodyColor);
        }

        // Snake head with 3D effect
        drawSnakePart(g, snakeHead.x, snakeHead.y, snakeHeadColor);

        // Score display
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.white);
        String scoreText = "Score: " + snakeBody.size();
        
        // Score background
        FontMetrics metrics = g.getFontMetrics();
        int scoreWidth = metrics.stringWidth(scoreText) + 10;
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(10, 10, scoreWidth, 25, 10, 10);
        
        // Score text
        g.setColor(Color.white);
        g.drawString(scoreText, 15, 28);

        // Game over display
        if(gameOver) {
            String gameOverText = "Game Over";
            String finalScoreText = "Final Score: " + snakeBody.size();
            String restartText = "Press R to Restart";
            
            // Game over background
            int textWidth = Math.max(
                Math.max(metrics.stringWidth(gameOverText), metrics.stringWidth(finalScoreText)),
                metrics.stringWidth(restartText)
            ) + 200;
            
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRoundRect(boardWidth/2 - textWidth/2, boardHeight/2 - 60, textWidth, 120, 20, 20);
            g.setColor(new Color(220, 20, 60));
            g.drawRoundRect(boardWidth/2 - textWidth/2, boardHeight/2 - 60, textWidth, 120, 20, 20);
            
            // Game over text
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(new Color(220, 20, 60));
            
            int gameOverX = boardWidth/2 - metrics.stringWidth(gameOverText)/2;
            g.drawString(gameOverText, gameOverX, boardHeight/2 - 20); //game over text postition
            
            // Final score text
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(Color.white);
            int finalScoreX = boardWidth/2 - metrics.stringWidth(finalScoreText)/2;
            g.drawString(finalScoreText, finalScoreX, boardHeight/2 + 10);
            
            // Restart instructions
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.setColor(Color.lightGray);
            int restartX = boardWidth/2 - metrics.stringWidth(restartText)/2;
            g.drawString(restartText, restartX, boardHeight/2 + 40);
        }
    }
    
    // Draw snake part with 3D effect
    private void drawSnakePart(Graphics2D g, int x, int y, Color baseColor) {
        int px = x * tileSize;
        int py = y * tileSize;
        int size = tileSize - 1; // Slight gap between segments
        
        // Main square
        g.setColor(baseColor);
        g.fillRoundRect(px, py, size, size, 8, 8);
        
        // Highlight (top-left)
        g.setColor(brighter(baseColor, 0.5f));
        g.fillRoundRect(px, py, size, size/2, 8, 8);
        
        // Shadow (bottom-right)
        g.setColor(darker(baseColor, 0.5f));
        g.fillRoundRect(px + size/2, py + size/2, size/2, size/2, 8, 8);
        
        // Outline
        g.setColor(darker(baseColor, 0.2f));
        g.drawRoundRect(px, py, size, size, 8, 8);
    }
    
    // Draw food with animation
    private void drawFood(Graphics2D g) {
        int px = food.x * tileSize;
        int py = food.y * tileSize;
        int size = tileSize - 2;
        
        // Pulsating animation
        double pulse = Math.sin(System.currentTimeMillis() / 150.0) * 0.1 + 0.9;
        int pulseSize = (int)(size * pulse);
        int offset = (tileSize - pulseSize) / 2;
        
        // Glow effect
        for(int i = 0; i < 5; i++) {
            int alpha = 50 - i * 10;
            g.setColor(new Color(foodColor.getRed(), foodColor.getGreen(), foodColor.getBlue(), alpha));
            g.fillOval(px - i, py - i, size + i*2, size + i*2);
        }
        
        // Main apple
        g.setColor(foodColor);
        g.fillOval(px + offset, py + offset, pulseSize, pulseSize);
        
        // Highlight
        g.setColor(brighter(foodColor, 0.5f));
        g.fillOval(px + offset + pulseSize/4, py + offset + pulseSize/4, pulseSize/4, pulseSize/4);
        
        // Draw stem
        g.setColor(new Color(101, 67, 33));
        g.fillRect(px + tileSize/2 - 1, py + offset, 2, tileSize/6);
    }
    
    // Helper methods for colors
    private Color brighter(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() * (1 + factor)));
        int g = Math.min(255, (int)(color.getGreen() * (1 + factor)));
        int b = Math.min(255, (int)(color.getBlue() * (1 + factor)));
        return new Color(r, g, b);
    }
    
    private Color darker(Color color, float factor) {
        int r = Math.max(0, (int)(color.getRed() * (1 - factor)));
        int g = Math.max(0, (int)(color.getGreen() * (1 - factor)));
        int b = Math.max(0, (int)(color.getBlue() * (1 - factor)));
        return new Color(r, g, b);
    }

    public void placeFood() {
        // Make sure food doesn't spawn on the snake
        boolean onSnake;
        do {
            food.x = random.nextInt(boardWidth/tileSize);
            food.y = random.nextInt(boardHeight/tileSize);
            
            onSnake = collision(food, snakeHead);
            if (!onSnake) {
                for (Tile body : snakeBody) {
                    if (collision(food, body)) {
                        onSnake = true;
                        break;
                    }
                }
            }
        } while (onSnake);
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void move() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMoveTime < animationSpeed) {
            return; // Not time to move yet
        }
        lastMoveTime = currentTime;
        
        // Snake head position before moving (for body parts)
        int prevHeadX = snakeHead.x;
        int prevHeadY = snakeHead.y;
        
        // Move snake head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;
        
        // Eating food
        if(collision(snakeHead, food)) {
            // Add a growth counter instead of immediately adding body parts
            growthCounter += 1;
            
            // Speed up the game slightly as the snake grows
            animationSpeed = Math.max(50, 100 - snakeBody.size() / 2);
            
            placeFood();
        }
        
        // Process snake body movement
        if (!snakeBody.isEmpty()) {
            // Move the rest of the body
            int prevX = prevHeadX;
            int prevY = prevHeadY;
            
            for (Tile body : snakeBody) {
                int tempX = body.x;
                int tempY = body.y;
                
                body.x = prevX;
                body.y = prevY;
                
                prevX = tempX;
                prevY = tempY;
            }
        }
        
        // Add new body part if growth counter is positive
        if (growthCounter > 0) {
            growthCounter--;
            // Add new body part at the end of the snake
            if (snakeBody.isEmpty()) {
                snakeBody.add(new Tile(prevHeadX, prevHeadY));
            } else {
                Tile tail = snakeBody.get(snakeBody.size() - 1);
                snakeBody.add(new Tile(tail.x, tail.y));
            }
        }

        // Game over conditions
        checkCollisions();
    }
    
    private void checkCollisions() {
        // Check if head collides with body
        for(Tile body : snakeBody) {
            if(collision(snakeHead, body)) {
                gameOver = true;
                return;
            }
        }
        
        // Check if head collides with walls
        if(snakeHead.x < 0 || snakeHead.x >= boardWidth/tileSize || 
           snakeHead.y < 0 || snakeHead.y >= boardHeight/tileSize) {
            gameOver = true;
        }
    }
    
    public void restart() {
        // Reset game state
        snakeHead = new Tile(5, 5);
        snakeBody.clear();
        
        velocityX = 0;
        velocityY = 1;
        
        placeFood();
        gameOver = false;
        growthCounter = 0;
        animationSpeed = 100;
        
        // Restart game loop if it was stopped
        if (!gameLoop.isRunning()) {
            gameLoop.start();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Handle game restart
        if (gameOver && e.getKeyCode() == KeyEvent.VK_R) {
            restart();
            return;
        }
        
        // Don't allow direction changes if we're already moving
        // in a certain direction to prevent 180-degree turns
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                if (velocityY != 1) {
                    velocityX = 0;
                    velocityY = -1;
                }
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                if (velocityY != -1) {
                    velocityX = 0;
                    velocityY = 1;
                }
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                if (velocityX != 1) {
                    velocityX = -1;
                    velocityY = 0;
                }
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                if (velocityX != -1) {
                    velocityX = 1;
                    velocityY = 0;
                }
                break;
            case KeyEvent.VK_SPACE:
                // Pause/resume functionality could be added here
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used but required by KeyListener interface
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used but required by KeyListener interface
    }
}