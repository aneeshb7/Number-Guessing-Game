package model;

import exceptions.NonPositiveAttemptsException;
import exceptions.NonPositiveMaximumException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

// Represents a guessing game with name, number of attempts, max answer, and all previous guesses
public class GuessingGame implements Writable {
    private String name;
    private int attempts;
    private int max;
    private PreviousGuesses guessesSoFar;

    // makes a new game with an empty list of previous guesses
    //                       and 1 attempt
    //                       and 1 as max number
    //                       and name
    public GuessingGame(String name) {
        this.attempts = 1;
        this.max = 1;
        this.guessesSoFar = new PreviousGuesses();
        this.name = name;
    }

    public int getAttempts() {
        return this.attempts;
    }

    // MODIFIES: this
    // EFFECTS: if a <= 0, throws new NonPositiveAttemptsException
    //          else sets attempts to a
    public void setAttempts(int a) throws NonPositiveAttemptsException {
        if (a <= 0) {
            throw new NonPositiveAttemptsException("Number of attempts must be positive.");
        } else {
            this.attempts = a;
        }
    }

    public PreviousGuesses getGuessesSoFar() {
        return this.guessesSoFar;
    }

    public void setGuessesSoFar(PreviousGuesses pg) {
        this.guessesSoFar = pg;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String s) {
        this.name = s;
    }

    public int getMax() {
        return this.max;
    }

    // EFFECTS: if m is not positive throws new NonPositiveMaximumException,
    //          else sets max to m
    public void setMax(int m) throws NonPositiveMaximumException {
        if (m <= 0) {
            throw new NonPositiveMaximumException("Maximum must be a positive number.");
        } else {
            this.max = m;
        }
    }

    // MODIFIES: this
    // EFFECTS: subtracts 1 from attempts
    public void deductAttempts() {
        attempts = attempts - 1;
    }

    // MODIFIES: this
    // EFFECTS: adds guess to previous guesses
    public void addGuessToGame(Guess guess) {
        guessesSoFar.addGuess(guess);
    }

    // method template taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("attempts remaining", attempts);
        json.put("maximum", max);
        json.put("guesses", guessesToJson());
        return json;
    }

    // method template taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    // EFFECTS: returns guesses in this game as a JSON array
    private JSONArray guessesToJson() {
        JSONArray jsonArray = new JSONArray();
        int size = this.getGuessesSoFar().length();
        for (int i = 0; i < size; i++) {
            jsonArray.put(this.getGuessesSoFar().getGuessInPosition(i).toJson());
        }
        return jsonArray;
    }
}

