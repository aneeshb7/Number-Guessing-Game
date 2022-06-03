package model;

import java.util.ArrayList;

// Represents a list of previous guesses the player has made
public class PreviousGuesses {
    public ArrayList<Guess> prevGuesses;

    // makes an empty list of previous guesses
    public PreviousGuesses() {
        prevGuesses = new ArrayList<>();
    }

    // REQUIRES: guess value is within range
    // MODIFIES: this
    // EFFECTS: if added guess is first guess in list, then add to this
    //          otherwise set answer of added guess to answer of previous guess and add to this
    public void addGuess(Guess g) {
        if (prevGuesses.size() == 0) {
            prevGuesses.add(g);
        } else {
            int size = prevGuesses.size();
            Guess pg = this.getGuessInPosition(size - 1);
            int prevAnswer = pg.getAnswer();
            g.setAnswer(prevAnswer);
            prevGuesses.add(g);
        }
    }

    // EFFECTS: returns number of previous guesses
    public int length() {
        return prevGuesses.size();
    }

    // REQUIRES: i <= length - 1
    // EFFECTS: returns guess value at position i in list of previous guesses
    public int getGuessValInPosition(int i) {
        return getGuessInPosition(i).getGuessValue();
    }

    // REQUIRES: i <= length - 1
    // EFFECTS: returns guess object at position i in list of previous guesses
    public Guess getGuessInPosition(int i) {
        Guess g = prevGuesses.get(i);
        return g;
    }
}
