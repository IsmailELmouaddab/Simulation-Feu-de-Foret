import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class SimulationFeuGraphique extends JFrame implements ActionListener {
    

    private JPanel feuPanel;
    private JButton startButton;
    private Timer timer;
    private int height;
    private int width;
    private int delay = 1000;
    private JButton[][] cellButtons;
    private boolean[][] feuGrid;
    private double p; 
   /* private JPanel cellule;*/

    public SimulationFeuGraphique(String configFile) throws FileNotFoundException{
        
        setTitle("Simulation de feu de foret");
        Scanner scanner = new Scanner(new File(configFile));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("width=")) {
                width = Integer.parseInt(line.substring(6));
            } else if (line.startsWith("height=")) {
                height = Integer.parseInt(line.substring(7));
            } else if (line.startsWith("propagation_probability=")) {
                p = Double.parseDouble(line.substring(24));
            }
        }
        scanner.close();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        feuPanel = new JPanel();
        feuPanel.setLayout(new GridLayout(height, width));
        getContentPane().add(feuPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        startButton = new JButton("Start");
        startButton.addActionListener(this);
        buttonPanel.add(startButton);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        timer = new Timer(delay, this);

        initGrid();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initGrid() {
        feuGrid = new boolean[height][width];
        cellButtons = new JButton[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                cellButtons[i][j] = new JButton();
                cellButtons[i][j].setBackground(Color.GREEN);
                cellButtons[i][j].addActionListener(this);
                feuPanel.add(cellButtons[i][j]);
                feuGrid[i][j] = false;
            }
        }
    }

 private void startSimulation() {
    startButton.setEnabled(false);
    Thread simulationThread = new Thread(() -> {
        while (anyBurning()) {
            run();
            try {
                Thread.sleep(1000); // Attente d'une seconde
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        startButton.setEnabled(true);
    });
    simulationThread.start();
}





private void updateGrid() {
    boolean[][] newGrid = new boolean[height][width];
    for (int j = 0; j < width; j++) {
        for (int i = 0; i < height; i++) {
            if (feuGrid[i][j]) {
                // la case est en feu
                /*newGrid[i][j] = false; */// le feu s'eteint dans cette case
                if (i > 0 && !feuGrid[i-1][j] && Math.random() < p) {
                    newGrid[i-1][j] = true; // propagation vers le haut
                }
                if (i < height-1 && !feuGrid[i+1][j] && Math.random() < p) {
                    newGrid[i+1][j] = true; // propagation vers le bas
                }
                if (j > 0 && !feuGrid[i][j-1] && Math.random() < p) {
                    newGrid[i][j-1] = true; // propagation vers la gauche
                }
                if (j >0 && !feuGrid[i][j+1] && Math.random() < p) {
                    newGrid[i][j+1] = true; // propagation vers la droite
                }
                cellButtons[i][j].setBackground(Color.GRAY); // la case devient grise
            } else {
                // la case n'est pas en feu
                newGrid[i][j] = false;
            }
        }
    }
    feuGrid = newGrid;
}




@Override
public void actionPerformed(ActionEvent e) {
    if (e.getSource() == startButton) {
        startSimulation();
    } else {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (e.getSource() == cellButtons[i][j]) {
                    feuGrid[i][j] = true;
                    cellButtons[i][j].setBackground(Color.RED);
                }
            }
        }
    }
}

public void run() {
    int time = 0;
    while (anyBurning()) {
        System.out.println("Time: " + time);
        updateGrid();
        updateColor();
        time++;
        try {
            Thread.sleep(1000); // Pause de 1 secondes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

 private void updateColor(){
     for(int i=0;i<height;i++){
         for(int j=0; j<width;j++){
             if(feuGrid[i][j]){
                 cellButtons[i][j].setBackground(Color.RED);
             }
         }
     }
 }
 
 private boolean anyBurning() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (feuGrid[i][j]) {
                    return true;
                }
            }
        }
        return false;
    }

public static void main(String[] fichier) throws FileNotFoundException {
    
    new SimulationFeuGraphique(fichier[0]);

}}
