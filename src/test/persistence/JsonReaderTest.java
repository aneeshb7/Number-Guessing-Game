package persistence;
// class name taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

import exceptions.NonPositiveAttemptsException;
import model.Guess;
import model.GuessingGame;
import model.PreviousGuesses;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

// Unit tests for the JsonReader class
public class JsonReaderTest {

    // method taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    @Test
    public void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            GuessingGame gg = reader.read();
            fail("IOException expected.");
        } catch (IOException e) {
            //expected
        }
    }

    // method taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    @Test
    public void testReaderGameNotStarted() {
        JsonReader reader = new JsonReader("./data/testReaderGameNotStarted.json");
        try {
            GuessingGame gg = reader.read();
            assertEquals(0, gg.getGuessesSoFar().length());
            assertEquals("empty game", gg.getName());
            assertEquals(1, gg.getAttempts());
            assertEquals(5, gg.getMax());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    // method template taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    @Test
    public void testReaderGameStarted() {
        JsonReader reader = new JsonReader("./data/testReaderGameStarted.json");
        try {
            GuessingGame gg = reader.read();
            assertEquals(3, gg.getGuessesSoFar().length());
            assertEquals(4, gg.getAttempts());
            assertEquals(50, gg.getMax());
            PreviousGuesses testGuesses = gg.getGuessesSoFar();
            int testGuessVal = testGuesses.getGuessValInPosition(0);
            Guess testGuess = testGuesses.getGuessInPosition(0);
            assertEquals(10, testGuessVal);
            assertEquals(50, testGuess.getAnswer());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    public void testExceptionHandling() {
        JsonReader failReader1 = new JsonReader("./data/testReaderAttemptsExceptionThrown.json");
        try {
            GuessingGame failGame1 = failReader1.read();
            assertEquals(1, failGame1.getAttempts());
            assertEquals(1, failGame1.getMax());
        } catch (IOException e) {
            fail("Couldn't read from file.");
        }
        JsonReader failReader2 = new JsonReader("./data/testReaderMaximumExceptionThrown.json");
        try {
            GuessingGame failGame2 = failReader2.read();
            assertEquals(5, failGame2.getAttempts());
            assertEquals(1, failGame2.getMax());
        } catch (IOException e) {
            fail("Couldn't read from file.");
        }
    }
}
