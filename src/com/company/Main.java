package com.company;

import java.util.Random;
import java.util.Scanner;

class Ship {
    private int length;
    private int hits;

    public Ship(int length) {
        this.length = length;
        this.hits = 0;
    }

    public int getLength() {
        return length;
    }

    public void hit() {
        hits++;
    }

    public boolean isSunk() {
        return hits >= length;
    }
}

class Board {
    private char[][] grid;
    private int size;

    public Board(int size) {
        this.size = size;
        this.grid = new char[size][size];
        initializeGrid();
    }

    private void initializeGrid() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                grid[row][col] = '~';  // '~' represents an empty spot on the board
            }
        }
    }

    public void display(boolean showShips) {
        System.out.print("   ");
        for (int col = 0; col < size; col++) {
            System.out.print((col + 1) + " ");
        }
        System.out.println();

        for (int row = 0; row < size; row++) {
            System.out.print((row + 1) + " ");
            for (int col = 0; col < size; col++) {
                char spot = grid[row][col];
                if (spot == 'H') {
                    System.out.print("H ");  // 'H' represents a hit spot
                } else if (spot == 'M') {
                    System.out.print("M ");  // 'M' represents a missed spot
                } else if (showShips && spot == 'S') {
                    System.out.print("S ");  // 'S' represents a ship spot
                } else {
                    System.out.print("~ ");  // '~' represents an empty spot
                }
            }
            System.out.println();
        }
    }

    public boolean isHit(int x, int y) {
        return grid[x][y] == 'S';  // 'S' represents a ship spot
    }

    public void markHit(int x, int y) {
        grid[x][y] = 'H';
    }

    public void markMiss(int x, int y) {
        grid[x][y] = 'M';
    }

    public boolean placeShip(Ship ship, int x, int y, boolean isHorizontal) {
        if (isHorizontal) {
            if (x + ship.getLength() > size) {
                return false;
            }

            for (int i = 0; i < ship.getLength(); i++) {
                if (grid[x+i][y] == 'S') {
                    return false;
                }
            }

            for (int i = 0; i < ship.getLength(); i++) {
                grid[x+i][y] = 'S';
            }
        } else {
            if (y + ship.getLength() > size) {
                return false;
            }

            for (int i = 0; i < ship.getLength(); i++) {
                if (grid[x][y+i] == 'S') {
                    return false;
                }
            }

            for (int i = 0; i < ship.getLength(); i++) {
                grid[x][y+i] = 'S';
            }
        }

        return true;
    }

    public boolean allShipsSunk() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (grid[row][col] == 'S') {
                    return false;
                }
            }
        }
        return true;
    }
    public int getSize() {
        return size;
    }
}

class Player {
    private String name;
    private Board board;
    private Board opponentBoard;
    private int score;
    private Random random;

    public Player(String name, int boardSize) {
        this.name = name;
        this.board = new Board(boardSize);
        this.opponentBoard = new Board(boardSize);
        this.score = 0;
        this.random = new Random();
    }

    public String getName() {
        return name;
    }

    public Board getBoard() {
        return board;
    }

    public void increaseScore() {
        score++;
    }

    public int getScore() {
        return score;
    }



    public boolean guess(Board opponentBoard, int x, int y) {
        if (opponentBoard.isHit(x, y)) {
            System.out.println("It's a hit!");
            board.markHit(x, y);
            opponentBoard.markHit(x, y);
            increaseScore();
            return true;
        } else {
            System.out.println("It's a miss!");
            board.markMiss(x, y);
            opponentBoard.markMiss(x, y);
            return false;
        }
    }

    public void generateComputerShips() {
        int numShips = 5;  // Number of ships for the computer

        for (int i = 0; i < numShips; i++) {
            int shipSize = i + 1;  // Ship sizes increase with each iteration

            boolean isHorizontal = random.nextBoolean();  // Randomly decide ship orientation

            int x, y;

            if (isHorizontal) {
                x = random.nextInt(board.getSize() - shipSize + 1);
                y = random.nextInt(board.getSize());
            } else {
                x = random.nextInt(board.getSize());
                y = random.nextInt(board.getSize() - shipSize + 1);
            }

            Ship ship = new Ship(shipSize);
            boolean placed = board.placeShip(ship, x, y, isHorizontal);

            while (!placed) {
                if (isHorizontal) {
                    x = random.nextInt(board.getSize() - shipSize + 1);
                    y = random.nextInt(board.getSize());
                } else {
                    x = random.nextInt(board.getSize());
                    y = random.nextInt(board.getSize() - shipSize + 1);
                }

                placed = board.placeShip(ship, x, y, isHorizontal);
            }
        }
    }

    public void displayOwnBoard() {
        System.out.println(name + "'s board:");
        board.display(true);
    }

    public void displayOpponentBoard() {
        System.out.println("Opponent's hidden board:");
        board.display(false);
    }
}

class Game {
    private Player player;
    private Player computer;
    private Scanner scanner;
    private Random random;

    public Game(String playerName, int boardSize) {
        this.player = new Player(playerName, boardSize);
        this.computer = new Player("Computer", boardSize);
        this.scanner = new Scanner(System.in);
        this.random = new Random();
    }

    private void printSeparator() {
        System.out.println("--------------------------------------------");
    }

    private void printWinner(Player player) {
        System.out.println("Congratulations, " + player.getName() + "! You win the game.");
    }

    private void placeShips(Player player) {
        System.out.println(player.getName() + ", place your ships on the board.");

        for (int shipNumber = 1; shipNumber <= 5; shipNumber++) {
            System.out.println("Placing ship " + shipNumber + " of length " + shipNumber + ".");
            player.displayOwnBoard();

            int size = player.getBoard().getSize();
            boolean validPlacement = false;
            int x = -1;
            int y = -1;
            boolean isHorizontal = false;

            while (!validPlacement) {
                System.out.print("Enter the x-coordinate (1-" + size + "): ");
                x = scanner.nextInt() - 1;
                System.out.print("Enter the y-coordinate (1-" + size + "): ");
                y = scanner.nextInt() - 1;

                if (x < 0 || x >= size || y < 0 || y >= size) {
                    System.out.println("Invalid coordinates. Try again.");
                    continue;
                }

                if (player.getBoard().isHit(x, y)) {
                    System.out.println("Invalid ship placement. Try again.");
                    continue;
                }

                boolean validOrientation = false;
                while (!validOrientation) {
                    System.out.print("Place horizontally? (true/false): ");
                    String orientationInput = scanner.next();

                    if (orientationInput.equalsIgnoreCase("true")) {
                        isHorizontal = true;
                        validOrientation = true;
                    } else if (orientationInput.equalsIgnoreCase("false")) {
                        isHorizontal = false;
                        validOrientation = true;
                    } else {
                        System.out.println("Invalid input. Please enter 'true' or 'false'.");
                    }
                }

                Ship ship = new Ship(shipNumber);
                if (player.getBoard().placeShip(ship, x, y, isHorizontal)) {
                    validPlacement = true;
                } else {
                    System.out.println("Invalid ship placement. Try again.");
                }
            }

            player.displayOwnBoard();
            printSeparator();
        }
    }

    private void playTurn(Player currentPlayer, Player opponentPlayer) {
        System.out.println(currentPlayer.getName() + "'s turn:");
        opponentPlayer.displayOpponentBoard();

        if (currentPlayer == player) {
            System.out.print("Enter the x-coordinate (1-" + opponentPlayer.getBoard().getSize() + "): ");
            int x = scanner.nextInt() - 1;
            System.out.print("Enter the y-coordinate (1-" + opponentPlayer.getBoard().getSize() + "): ");
            int y = scanner.nextInt() - 1;

            boolean isHit = currentPlayer.guess(opponentPlayer.getBoard(), x, y);
            currentPlayer.displayOpponentBoard();
            printSeparator();

            if (isHit && opponentPlayer.getScore() == 5) {
                printWinner(currentPlayer);
                System.exit(0);
            }
        } else {
            int x = random.nextInt(opponentPlayer.getBoard().getSize());
            int y = random.nextInt(opponentPlayer.getBoard().getSize());

            while (opponentPlayer.getBoard().isHit(x, y)) {
                x = random.nextInt(opponentPlayer.getBoard().getSize());
                y = random.nextInt(opponentPlayer.getBoard().getSize());
            }

            boolean isHit = currentPlayer.guess(opponentPlayer.getBoard(), x, y);
            currentPlayer.displayOpponentBoard();
            printSeparator();

            if (isHit && opponentPlayer.getScore() == 5) {
                printWinner(currentPlayer);
                System.exit(0);
            }
        }
    }

    public void start() {
        System.out.println("Welcome to Battleship " + player.getName());

        placeShips(player);
        player.generateComputerShips();

        while (true) {
            playTurn(player, computer);
            playTurn(computer, player);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String playerName = scanner.nextLine();

        Game game = new Game(playerName, 10);
        game.start();
    }
}

