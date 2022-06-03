package ui;

import exceptions.NonPositiveAttemptsException;
import exceptions.NonPositiveMaximumException;
import model.Guess;
import model.GuessingGame;
import model.PreviousGuesses;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;

// Guessing Game graphical user interface application
public class GuessingGameGUI implements ActionListener {
    private static JFrame gameFrame;
    private static JPanel gamePanel;
    private static JButton startButton;
    private static JButton commandButton;
    private static JButton rangeButton;
    private static JButton attemptsButton;
    private static JButton nameButton;
    private static JButton playButton;
    private static JButton prevGuessButton;
    private static JTextField loadOrStartField;
    private static JTextField nameField;
    private static JTextField rangeField;
    private static JTextField attemptsField;
    private static JTextField answerField;
    private static JLabel startLabel;
    private static JLabel attemptsLabel;
    private static JLabel answerLabel;
    private static JLabel guessPropertyLabel;
    private static JLabel winLabel;
    private static JLabel loseLabel;
    private JsonReader jsonReader;
    private JsonWriter jsonWriter;
    private Guess playerGuess;
    private PreviousGuesses guesses;
    private GuessingGame game;
    private static final String JSON_STORE = "./data/game.json";
    private int range;
    private int initialAttempts;


    // MODIFIES: this
    // EFFECTS: creates new guessing game gui
    public GuessingGameGUI()  {
        initGUI();
        initGame();
    }

    // MODIFIES: this
    // EFFECTS: evaluates which button is being pressed and performs appropriate actions
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("start button")) {
            startGame();
        } else if (e.getActionCommand().equals("load or new game button")) {
            loadOrStartNewGame();
        } else if (e.getActionCommand().equals("new game name")) {
            game.setName(nameField.getText());
            specifyRange();
        } else if (e.getActionCommand().equals("largest possible positive integer")) {
            range = Integer.parseInt(rangeField.getText());
            userSetMax();
            specifyAttempts();
        } else if (e.getActionCommand().equals("desired number of attempts")) {
            initialAttempts = Integer.parseInt(attemptsField.getText());
            userSetAttempts();
            startGuessing();
        } else if (e.getActionCommand().equals("answer or restart or save or quit")) {
            processGameInput();
        } else  {
            tellUserGuessProperty();
        }
    }

    // MODIFIES: this, game
    // EFFECTS: if attempts > 0, sets game attempts to given value, else catches NonPositiveAttemptsException
    //          and exits game
    private void userSetAttempts() {
        try {
            game.setAttempts(initialAttempts);
        } catch (NonPositiveAttemptsException e) {
            System.out.println("Attempts must be positive.");
            System.exit(0);
        }
    }

    // MODIFIES: this, game
    // EFFECTS: if range + 1 > 0, sets game max to given value, else catches IndexOutOfBoundsException and exits game
    private void userSetMax() {
        try {
            game.setMax(range + 1);
        } catch (NonPositiveMaximumException e) {
            System.out.println("Maximum must be positive.");
            System.exit(0);
        }
    }

    // taken from LabelChanger project on phase 3 edX page 66
    // gameFrame centering code taken from
    // https://stackoverflow.com/questions/2442599/how-to-set-jframe-to-appear-centered-regardless-of-monitor-resolution
    // MODIFIES: this
    // EFFECTS: creates new gameFrame, startButton, and label instances and initializes
    private void initGUI() {
        gameFrame = new JFrame("Number Guessing Game");
        startButton = new JButton("Start game");
        startLabel = new JLabel();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = dim.width / 2 - gameFrame.getSize().width / 2;
        int centerY = dim.height / 2 - gameFrame.getSize().height / 2;
        gameFrame.setLocation(centerX, centerY);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setVisible(true);
        gameFrame.setResizable(true);
        gameFrame.setLayout(new FlowLayout());
        gameFrame.add(startLabel);
        gameFrame.add(startButton);
        startButton.setBounds(10,80,125,125);
        startButton.setActionCommand("start button");
        startButton.addActionListener(this);
        gameFrame.pack();
    }

    // MODIFIES: game, playerGuess
    // EFFECTS: initializes jsonWriter, jsonReader, playerGuess, and game
    private void initGame() {
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        game = new GuessingGame("My game");
        playerGuess = new Guess(-1,1);
    }

    // MODIFIES: this
    // EFFECTS:  displays welcome messages to user
    private void startGame() {
        clearGameFrame();
        startLabel.setText("Welcome to the number guessing game!");
        gameFrame.add(new JLabel("Enter l to load a previous game, or n to start a new game."));
        loadOrStartField = new JTextField(5);
        gameFrame.add(loadOrStartField);
        makeButton(commandButton, "load or new game button");
        guesses = new PreviousGuesses();
        gameFrame.pack();
    }

    // REQUIRES: user input is either l or n
    // MODIFIES: this
    // EFFECTS:  if user input is l, loads a previous game
    //           else if user input is n, starts a new game
    private void loadOrStartNewGame() {
        if (loadOrStartField.getText().equals("l")) {
            loadGame();
        } else if (loadOrStartField.getText().equals("n")) {
            newGame();
        }
    }

    // REQUIRES: user input is "s", "r", "q", or non-negative integer
    // MODIFIES: this
    // EFFECTS: reads the user input into answerField,
    //          if user inputs s, then saves and quits game,
    //          else if user inputs "r", then restarts game,
    //          else if user inputs "q", then quits game,
    //          else passes user integer input as argument to keepGuessing method
    private void processGameInput() {
        if (answerField.getText().equals("s")) {
            saveAndQuitGame();
        } else if (answerField.getText().equals("r")) {
            new GuessingGameGUI();
        } else if (answerField.getText().equals("q")) {
            quitGame();
        } else {
            int answer = Integer.parseInt(answerField.getText());
            keepGuessing(answer);
        }
    }

    // MODIFIES: this
    // EFFECTS: quits the game
    private void quitGame() {
        System.exit(0);
    }

    // MODIFIES: this, jsonWriter
    // EFFECTS: saves the game to file and quits
    private void saveAndQuitGame() {
        clearGameFrame();
        try {
            jsonWriter.open();
            jsonWriter.write(game);
            jsonWriter.close();
            gameFrame.add(new JLabel("Saved " + game.getName() + " to " + JSON_STORE + "."));
            gameFrame.pack();
        } catch (FileNotFoundException e) {
            gameFrame.add(new JLabel("Unable to write to file: " + JSON_STORE));
        }
    }

    // MODIFIES: this
    // EFFECTS:  tries to load previous saved game from file, if none available, catches IOException
    private void loadGame() {
        try {
            game = jsonReader.read();
            playerGuess.setAnswer(game.getGuessesSoFar().getGuessInPosition(0).getAnswer());
            clearGameFrame();
            gameFrame.add(new JLabel("Loaded " + game.getName() + " from " + JSON_STORE));
            gameFrame.add(new JLabel("You have " + game.getAttempts() + " attempts left."));
            gameFrame.add(new JLabel("The maximum answer is " + (game.getMax() - 1) + "."));
            answerField = new JTextField(5);
            attemptsLabel = new JLabel();
            gameFrame.add(answerField);
            makeButton(playButton, "answer or restart or save or quit");
            gameFrame.add(attemptsLabel);
            addLoadedGuesses();
            gameFrame.pack();
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
            gameFrame.pack();
        }
    }

    // MODIFIES: this, game, guesses
    // EFFECTS: upon loading previous file, adds all previous guesses to game and to guesses
    private void addLoadedGuesses() {
        int size = game.getGuessesSoFar().length();
        for (int i = 0; i < size; i++) {
            addGuessToPanel(game.getGuessesSoFar().getGuessInPosition(i));
            try {
                guesses.addGuess(game.getGuessesSoFar().getGuessInPosition(i));
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Index out of bounds.");
            }
        }
    }

    // MODIFIES: this, game
    // EFFECTS:  clears frame and lets player give their game file a new name
    private void newGame() {
        clearGameFrame();
        gameFrame.add(new JLabel("The computer will generate a random number and it is your job to guess it."));
        gameFrame.add(new JLabel("First, give your game file a creative name."));
        nameField = new JTextField(5);
        gameFrame.add(nameField);
        makeButton(nameButton, "new game name");
        gameFrame.pack();
    }

    // MODIFIES: this
    // EFFECTS: creates answer text field and prompts user to start guessing
    private void startGuessing() {
        clearGameFrame();
        gameFrame.add(new JLabel("Start guessing. Good luck!"));
        gameFrame.add(new JLabel("Enter r to restart. Enter q to quit. Enter s to save and quit."));
        gameFrame.add(new JLabel("Click on your most recent guess to see if it is higher or lower."));
        answerField = new JTextField(5);
        gameFrame.add(answerField);
        makeButton(playButton, "answer or restart or save or quit");
        attemptsLabel = new JLabel();
        gamePanel = new JPanel();
        gameFrame.add(attemptsLabel);
        gameFrame.pack();
    }

    // win game image taken from http://pixelartmaker.com/art/bb9cca5bf784b35
    // MODIFIES: this
    // EFFECTS: tells the player they have won, prints all guesses, and gives option to restart or quit
    private void winGame() {
        clearGameFrame();
        winLabel = new JLabel("Correct! The answer was " + playerGuess.getAnswer() + " .");
        gameFrame.add(winLabel);
        gameFrame.add(new JLabel("Enter r to restart or q to quit."));
        gameFrame.add(answerField);
        makeButton(playButton, "answer or restart or save or quit");
        ImageIcon winImage = new ImageIcon("./data/projectWinGameImage.png");
        JLabel winImageLabel = new JLabel(winImage);
        gameFrame.add(winImageLabel);
        printGuesses();
        gameFrame.pack();
    }

    // lose game image taken from https://www.deviantart.com/kawaiishi/art/Game-Over-You-lost-the-game-209771073
    // MODIFIES: this
    // EFFECTS: tells the player they have lost and gives option to restart or quit
    private void loseGame() {
        clearGameFrame();
        loseLabel = new JLabel("You are out of attempts. The answer was " + playerGuess.getAnswer() + " .");
        gameFrame.add(loseLabel);
        gameFrame.add(new JLabel("Enter r to restart or q to quit."));
        gameFrame.add(answerField);
        makeButton(playButton, "answer or restart or save or quit");
        ImageIcon loseImage = new ImageIcon("./data/projectLoseGameImage.jpg");
        JLabel loseImageLabel = new JLabel(loseImage);
        gameFrame.add(loseImageLabel);
        printGuesses();
        gameFrame.pack();
    }

    // REQUIRES: -2147483648 <= answer <= 2147483647
    // MODIFIES: this, game, playerGuess
    // EFFECTS: if player guess value is higher than the answer, tells the player,
    //          if player guess value is lower than the answer, tells the player,
    //          and adds to previous guesses
    //          else if player guess value is equal to the answer, player wins game
    private void keepGuessing(int answer) {
        playerGuess.setGuessValue(answer);
        Guess newGuess = new Guess(answer, game.getMax());
        game.addGuessToGame(newGuess);
        addNewGuess(newGuess);
        setPlayerGuessAnswer(newGuess);
        if (playerGuess.isHigher()) {
            game.deductAttempts();
            attemptsLabel.setText("You have " + game.getAttempts() + " attempts left.");
            checkAttemptsZero();
            if (!checkAttemptsZero()) {
                addGuessToPanel(newGuess);
            }
        } else if (playerGuess.isLower()) {
            game.deductAttempts();
            attemptsLabel.setText("You have " + game.getAttempts() + " attempts left.");
            checkAttemptsZero();
            if (!checkAttemptsZero()) {
                addGuessToPanel(newGuess);
            }
        } else if (playerGuess.isEqual()) {
            winGame();
        }
        gameFrame.pack();
    }

    // MODIFIES: guesses
    // EFFECTS: tries to add newGuess to guesses, else catches NonPositiveAnswerException
    private void addNewGuess(Guess newGuess) {
        guesses.addGuess(newGuess);
    }

    // MODIFIES: playerGuess
    // EFFECTS: sets player guess answer to newGuess asnwer
    private void setPlayerGuessAnswer(Guess newGuess) {
        playerGuess.setAnswer(newGuess.getAnswer());
    }

    // MODIFIES: this
    // EFFECTS: evaluates if player guess is higher, lower, or equal to the answer and gives corresponding feedback,
    //          then adds players guess to game panel
    private void gameLoop(Guess newGuess) {
        if (playerGuess.isHigher()) {
            game.deductAttempts();
            attemptsLabel.setText("You have " + game.getAttempts() + " attempts left.");
            checkAttemptsZero();
            if (!checkAttemptsZero()) {
                addGuessToPanel(newGuess);
            }
        } else if (playerGuess.isLower()) {
            game.deductAttempts();
            attemptsLabel.setText("You have " + game.getAttempts() + " attempts left.");
            checkAttemptsZero();
            if (!checkAttemptsZero()) {
                addGuessToPanel(newGuess);
            }
        } else if (playerGuess.isEqual()) {
            winGame();
        }
    }

    // MODIFIES: this
    // EFFECTS: if game attempts are equal to zero and playerGuess is not equal to the answer,
    //          tells player they have lost the game and returns true,
    //          else returns false
    private boolean checkAttemptsZero() {
        if (game.getAttempts() == 0 && !playerGuess.isEqual()) {
            loseGame();
            return true;
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: empty the game frame
    private void clearGameFrame() {
        gameFrame.getContentPane().removeAll();
        gameFrame.repaint();
    }

    // MODIFIES: playerGuess, game
    // EFFECTS: player inputs the max number that can be generated
    private void specifyRange() {
        clearGameFrame();
        gameFrame.add(new JLabel("Please enter the largest positive integer that the answer can be."));
        rangeField = new JTextField(5);
        gameFrame.add(rangeField);
        makeButton(rangeButton, "largest possible positive integer");
        gameFrame.pack();
    }

    // MODIFIES: game
    // EFFECTS: player inputs the number of desired attempts to guess number
    private void specifyAttempts() {
        clearGameFrame();
        gameFrame.add(new JLabel("Please enter the number of attempts you would like to guess the number."));
        attemptsField = new JTextField(5);
        gameFrame.add(attemptsField);
        makeButton(attemptsButton, "desired number of attempts");
        gameFrame.pack();
    }

    // MODIFIES: this
    // EFFECTS: makes gamePanel object and adds guess value to game panel as a button
    private void addGuessToPanel(Guess guess) {
        gamePanel = new JPanel();
        prevGuessButton = new JButton(Integer.toString(guess.getGuessValue()));
        prevGuessButton.addActionListener(this);
        prevGuessButton.setActionCommand("guess property");
        gamePanel.add(prevGuessButton);
        gameFrame.add(gamePanel);
        gameFrame.pack();
    }

    // MODIFIES: this
    // EFFECTS: tells user whether the guess is higher or lower than the answer
    private void tellUserGuessProperty() {
        int guessVal = Integer.parseInt(prevGuessButton.getText());
        Guess prevGuess = new Guess(guessVal, game.getMax());
        game.addGuessToGame(prevGuess);
        if (prevGuess.isHigher()) {
            gamePanel.add(new JLabel(guessVal + " is higher than the answer."));
        } else if (prevGuess.isLower()) {
            gamePanel.add(new JLabel(guessVal + " is lower than the answer."));
        }
        gameFrame.pack();
    }

    // REQUIRES: string is non empty
    // MODIFIES: JButton fields
    // EFFECTS:  creates new JButton instance, adds action listener, sets action command to given string
    //           then adds button to game frame
    private void makeButton(JButton button, String str) {
        button = new JButton("Enter");
        button.addActionListener(this);
        button.setActionCommand(str);
        gameFrame.add(button);
    }


    // MODIFIES: this
    // EFFECTS: if the player has made >= 1 guess, prints their values onto the GUI,
    //          else, prints "No guesses made."
    private void printGuesses() {
        int size = guesses.length();
        if (size > 0) {
            gameFrame.add(new JLabel("Your guesses were:"));
            for (int i = 0; i < size; i++) {
                gameFrame.add(new JLabel(Integer.toString(guesses.getGuessValInPosition(i))));
            }
        } else {
            gameFrame.add(new JLabel("No guesses made"));
        }
    }

}
