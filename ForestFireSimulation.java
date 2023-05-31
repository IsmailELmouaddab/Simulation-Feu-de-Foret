import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;

public class ForestFireSimulation {

    private int height; // hauteur de la grille
    private int width; // largeur de la grille
    private boolean[][] grid; // grille de la foret
    private double p; // probabilite de propagation du feu

    // Constructeur prenant en entree le nom d'un fichier de configuration
    public ForestFireSimulation(String configFile) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(configFile));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("height=")) {
                height = Integer.parseInt(line.substring(7));
            } else if (line.startsWith("width=")) {
                width = Integer.parseInt(line.substring(6));
            } else if (line.startsWith("initial_burning=")) {
                String[] parts = line.substring(16).split(";");
                grid = new boolean[height][width];
                for (String part : parts) {
                    String[] coordinates = part.split(",");
                    int row = Integer.parseInt(coordinates[0]);
                    int col = Integer.parseInt(coordinates[1]);
                    grid[row][col] = true;
                }
            } else if (line.startsWith("propagation_probability=")) {
                p = Double.parseDouble(line.substring(24));
            }
        }
        scanner.close();
    }

    // Methode pour executer la simulation
    public void run() {
        int time = 0;
        while (anyBurning()) {
            System.out.println("Time: " + time);
            printGrid();
            updateGrid();
            time++;
        }
    }

    // Methode pour verifier si au moins une case est en feu
    private boolean anyBurning() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (grid[row][col]) {
                    return true;
                }
            }
        }
        return false;
    }

    // Methode pour mettre a jour la grille en fonction de l'etat actuel
    private void updateGrid() {
        boolean[][] newGrid = new boolean[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (grid[row][col]) {
                    // la case est en feu
                    newGrid[row][col] = false; // le feu s'eteint dans cette case
                    if (row > 0 && Math.random() < p) {
                        newGrid[row-1][col] = true; // propagation vers le haut
                    }
                    if (row < height-1 && Math.random() < p) {
                        newGrid[row+1][col] = true; // propagation vers le bas
                    }
                    if (col > 0 && Math.random() < p) {
                        newGrid[row][col-1] = true; // propagation vers la gauche
                    }
                    if (col < width-1 && Math.random() < p) {
                        newGrid[row][col+1] = true; // propagation vers la droite
                    }
                } else {
                    // la case n'est pas en feu
                    newGrid[row][col] = false;
                }
            }
        }
        grid = newGrid;
    }

    // Methode pour afficher la grille actuelle
    private void printGrid() {
        for (int row = 0; row < height; row++) {
        for (int col = 0; col < width; col++) {
            if (grid[row][col]) {
                System.out.print("X"); // case en feu
            } else {
                System.out.print("."); // case non en feu
            }
        }
        System.out.println();
    }
    System.out.println();
}

// Methode main pour lancer la simulation a partir d'un fichier de configuration
public static void main(String[] args) throws FileNotFoundException {
    if (args.length < 1) {
        System.out.println("Usage: java ForestFireSimulation <config_file>");
        System.exit(1);
    }
    ForestFireSimulation simulation = new ForestFireSimulation(args[0]);
    simulation.run();
}}