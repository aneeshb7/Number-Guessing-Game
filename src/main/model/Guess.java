package model;

import org.json.JSONObject;
import persistence.Writable;

import java.util.Random;

// Represents a guess with a value, the final answer, and whether the guess is higher, lower, or equal
public class Guess implements Writable {
    private int guessValue;
    private int max;
    private int answer;
    private boolean isEqual;
    private boolean isHigher;
    private boolean isLower;
    private Random randInteger;


    // REQUIRES: num is <= max
    // EFFECTS: creates new Guess with given value, and
    //          random answer, and
    //          exactly one of isEqual, isHigher, and isLower set to true
    public Guess(int num, int max) {
        this.guessValue = num;
        this.answer = this.generateAnswer(max);
        this.isEqual = this.isEqual();
        this.isHigher = this.isHigher();
        this.isLower = this.isLower();
    }

    public int getGuessValue() {
        return this.guessValue;
    }

    public void setGuessValue(int i) {
        this.guessValue = i;
    }

    // MODIFIES: this
    // EFFECTS: returns true if guessValue is equal to rand, false otherwise
    public boolean isEqual() {
        return (this.guessValue == answer);
    }

    // MODIFIES: this
    // EFFECTS: returns true if guessValue is greater than rand, false otherwise
    public boolean isHigher() {
        return (this.guessValue > answer);
    }

    // MODIFIES: this
    // EFFECTS: returns true if guessValue is less than rand, false otherwise
    public boolean isLower() {
        return (this.guessValue < answer);
    }

    // REQUIRES: max > 0
    // EFFECTS: returns a random integer that is >= 0 and <= max
    public Integer generateAnswer(int max)  {
        randInteger = new Random();
        this.answer = randInteger.nextInt(max);
        return answer;
    }

    // EFFECTS: returns answer
    public Integer getAnswer() {
        return this.answer;
    }

    // REQUIRES: i > 0
    // MODIFIES: this
    // EFFECTS: sets answer of Guess object equal to i
    public void setAnswer(int i) {
        this.answer = i;
    }

    // method template taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    // EFFECTS: returns guess as a JSON object
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("guess value", guessValue);
        json.put("answer", answer);
        return json;
    }

}
