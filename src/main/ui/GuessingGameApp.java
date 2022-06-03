package ui;

import exceptions.NonPositiveAttemptsException;
import exceptions.NonPositiveMaximumException;
import model.Guess;
import model.GuessingGame;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;


// Guessing Game application 
public class GuessingGameApp {
    private static final String JSON_STORE = "./data/game.json";
    private Guess playerGuess;
    private Scanner input;
    private GuessingGame game;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private String command;

    // EFFECTS: runs the guessing game application
    public GuessingGameApp() {
        playGame();
    }

    // method body template and specifications for playGame method borrowed from TellerApp
    // https://github.students.cs.ubc.ca/CPSC210/TellerApp
    // MODIFIES: this
    // EFFECTS: processes user input
    private void playGame() {
        initialize();
        startGame();
        gameLoop();
    }


    // EFFECTS: displays welcome messages and instructions to the user
    private void startGame() {
        System.out.println("Welcome to the number guessing game!");
        System.out.println("Press l to load a previous game, press n to start a new game.");
        processCommand(input.nextLine());
        System.out.println("\nThe computer will generate a random number and it is your job to guess it.");
        System.out.println("\nFirst, give your game a creative name!");
        String name = input.nextLine();
        game.setName(name);
        specifyRangeAndAttempts();
    }

    // MODIFIES: this
    // EFFECTS: while player guess is not equal, if input is an integer, the guess is evaluated and feedback is given
    //          if input is "q", quits the game
    //          if input is "s", saves and quits the game
    //          otherwise processes integer input and gives feedback
    //          if player guess is equal, user wins the game
    private void gameLoop() {
        while (!playerGuess.isEqual()) {
            command = input.nextLine();

            if (command.equals("q")) {
                quitGame();
            } else if (game.getAttempts() == 0) {
                keepGuessing(command);
                outOfAttempts();
                command = input.next();
                processCommand(command);
            } else {
                processCommand(command);
                keepGuessing(command);
            }
        }
        if (playerGuess.isEqual()) {
            winGame();
            command = input.next();
            processCommand(command);
        }
    }

    // REQUIRES: max number and number of attempts must be > 0, input at this point must be an integer
    // MODIFIES: game, playerGuess
    // EFFECTS: specifies the maximum integer the computer can choose from for the answer
    private void specifyRangeAndAttempts() {
        System.out.println("\nEnter the largest positive integer the computer can generate as an answer.");
        try {
            playerGuess = new Guess(-1, 1);
            int max = Integer.parseInt(input.nextLine()) + 1;
            playerGuess.generateAnswer(max);
            try {
                game.setMax(max);
            } catch (NonPositiveMaximumException e) {
                System.out.println("Maximum must be positive.");
            }
            System.out.println("Enter the number of attempts you would like to have to guess the number.");
            try {
                game.setAttempts(Integer.parseInt(input.nextLine()) - 1);
            } catch (NonPositiveAttemptsException e) {
                System.out.println("Attempts must be positive.");
            }
            System.out.println("\nStart guessing. Good luck!");
            System.out.println("\tr -> restart at any point while playing");
            System.out.println("\ts -> save and quit at any point while playing");
            System.out.println("\tq -> quit at any point while playing");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Answer must be positive.");
        }
    }

    // method follows template of init() method in TellerApp https://github.students.cs.ubc.ca/CPSC210/TellerApp
    // MODIFIES: this
    // EFFECTS:  initializes input, jsonWriter, jsonReader, playerGuess, and game
    private void initialize() {
        input = new Scanner(System.in);
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        game = new GuessingGame("My game");
        try {
            playerGuess = new Guess(-1,1);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Answer must be positive.");
        }
    }

    // REQUIRES: c is "r", "q", "s", or an integer
    // MODIFIES: this, playerGuess
    // EFFECTS: if player guess value is higher than the answer, tells the player,
    //          if player guess value is lower than the answer, tells the player,
    //          and adds to previous guesses
    private void keepGuessing(String c) {
        int guessVal = Integer.parseInt(c);
        playerGuess.setGuessValue(guessVal);
        try {
            Guess newGuess = new Guess(guessVal, game.getMax());
            game.addGuessToGame(newGuess);
            playerGuess.setAnswer(newGuess.getAnswer());
            if (playerGuess.isHigher()) {
                System.out.println(playerGuess.getGuessValue() + " is higher than the answer.");
            } else if (playerGuess.isLower()) {
                System.out.println(playerGuess.getGuessValue() + " is lower than the answer.");
            }
            System.out.println("\tYou have " + game.getAttempts() + " attempts left.");
            game.deductAttempts();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Answer must be positive.");
        }
    }

    // MODIFIES: this
    // EFFECTS: tells the player they won and gives the option to restart
    private void winGame() {
        System.out.println("\nCorrect! The answer was " + playerGuess.getAnswer() + ".");
        printGuesses();
        System.out.println("\tr -> restart");
        System.out.println("\tq -> quit");
    }

    // MODIFIES: this
    // EFFECTS: restarts the game
    private void restartGame() {
        System.out.println("\nThe answer was " + playerGuess.getAnswer() + ". Game has been restarted.");
        System.out.println("\n");
        playGame();
    }


    // MODIFIES: this
    // EFFECTS: ends the game and tells the player the answer
    private void quitGame() {
        System.out.println("\nThe answer was " + playerGuess.getAnswer() + ". Thanks for playing!");
        System.exit(0);
    }

    // template for this method taken from TellerApp https://github.students.cs.ubc.ca/CPSC210/TellerApp
    // REQUIRES: command is "r" or "q", cannot be called until player has specified range and attempts
    // MODIFIES: this
    // EFFECTS: processes user command
    private void processCommand(String command) {
        if (command.equals("r")) {
            restartGame();
        } else if (command.equals("q")) {
            quitGame();
        } else if (command.equals("s")) {
            saveAndQuitGame();
        } else if (command.equals("l")) {
            loadGame();
        }
    }

    // MODIFIES: this
    // EFFECTS: if the player has made >= 1 guess, prints their values,
    //          else, prints "No guesses made."
    private void printGuesses() {
        int size = game.getGuessesSoFar().length();
        if (size > 0) {
            System.out.println("\nYour guesses were:");
            for (int i = 0; i < size; i++) {
                System.out.println(game.getGuessesSoFar().getGuessValInPosition(i));
            }
        } else {
            System.out.println("\nNo guesses made.");
        }
    }

    // MODIFIES: this
    // EFFECTS: tells the player they are out of attempts, prints their guesses and if
    //          the player guessed right on the last try, wins game, else,
    //          tells them the answer and gives them options to restart or quit
    private void outOfAttempts() {
        System.out.println("\nYou are out of attempts.");
        if (playerGuess.isEqual()) {
            winGame();
        } else {
            System.out.println("\nYou have lost the game.");
            printGuesses();
            System.out.println("\nThe answer was " + playerGuess.getAnswer() + ".");
            System.out.println("\tr -> restart");
            System.out.println("\tq -> quit");
        }
    }

    // method template taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    // EFFECTS: saves the game to file and quits
    private void saveAndQuitGame() {
        try {
            jsonWriter.open();
            jsonWriter.write(game);
            jsonWriter.close();
            System.out.println("Saved " + game.getName() + " to " + JSON_STORE + ".");
            System.exit(0);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    // method template taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    // MODIFIES: this
    // EFFECTS: loads game from file
    private void loadGame() {
        try {
            game = jsonReader.read();
            if (game.getGuessesSoFar().length() > 0) {
                playerGuess.setAnswer(game.getGuessesSoFar().getGuessInPosition(0).getAnswer());
            }
            System.out.println("Loaded " + game.getName() + " from " + JSON_STORE);
            printGuesses();
            System.out.println("You have " + (game.getAttempts() + 1) + " attempts left.");
            System.out.println("The maximum answer is " + (game.getMax() - 1) + ".");
            gameLoop();
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }
}