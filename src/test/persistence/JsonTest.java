package persistence;
// class name taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

import model.Guess;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Unit test to be used in the JsonWriter class
public class JsonTest {
    // method template taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    // checks that the given guess value is equal to the guess value of the given Guess
    protected void checkGuess(int guessValue, Guess guess) {
        assertEquals(guessValue, guess.getGuessValue());
    }
}
