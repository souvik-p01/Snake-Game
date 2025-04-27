import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) throws Exception {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create game window
        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Set game dimensions
        int boardWidth = 600;
        int boardHeight = 600;
        
        // Create game panel
        SnakeGame snakeGame = new SnakeGame(boardWidth, boardHeight);
        
        // Set up the title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(30, 30, 30));
        titlePanel.setPreferredSize(new Dimension(boardWidth, 30));
        JLabel titleLabel = new JLabel("SNAKE GAME");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        
        // Set up instructions panel
        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setBackground(new Color(30, 30, 30));
        instructionsPanel.setPreferredSize(new Dimension(boardWidth, 30));
        JLabel instructionsLabel = new JLabel("Use arrow keys or WASD to move. Press R to restart after game over.");
        instructionsLabel.setForeground(Color.LIGHT_GRAY);
        instructionsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        instructionsPanel.add(instructionsLabel);
        
        // Add components to frame
        frame.setLayout(new BorderLayout());
        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(snakeGame, BorderLayout.CENTER);
        frame.add(instructionsPanel, BorderLayout.SOUTH);
        
        // Set up the frame
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Give focus to the game panel
        snakeGame.requestFocus();
    }
}