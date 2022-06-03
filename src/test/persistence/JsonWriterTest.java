package persistence;
// class name taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

import exceptions.NonPositiveAttemptsException;
import exceptions.NonPositiveMaximumException;
import model.Guess;
import model.GuessingGame;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

// Unit tests for the JsonWriter class
public class JsonWriterTest extends JsonTest {

    // method name and template taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    @Test
    void testWriterInvalidFile() {
        try {
            GuessingGame gg = new GuessingGame("My game");
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // expected
        }
    }

    // method template taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    @Test
    void testWriterEmptyWorkRoom() {
        try {
            GuessingGame gg = new GuessingGame("My game");
            JsonWriter writer = new JsonWriter("./data/testWriterGameNotStarted.json");
            writer.open();
            writer.write(gg);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGameNotStarted.json");
            gg = reader.read();
            assertEquals("My game", gg.getName());
            assertEquals(0, gg.getGuessesSoFar().length());
            assertEquals(1, gg.getAttempts());
            assertEquals(1, gg.getMax());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    // method template taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    @Test
    void testWriterGeneralGame() {
        try {
            GuessingGame gg = new GuessingGame("My game");
            gg.addGuessToGame(new Guess(15, 7));
            gg.addGuessToGame(new Guess(10, 5));
            assertEquals(gg.getGuessesSoFar().getGuessInPosition(0).getAnswer(),
                    gg.getGuessesSoFar().getGuessInPosition(1).getAnswer());
            try {
                gg.setAttempts(3);
            } catch (NonPositiveAttemptsException e) {
                System.out.println("Attempts must be positive.");
            }
            try {
                gg.setMax(500);
            } catch (NonPositiveMaximumException e) {
                System.out.println("Maximum must be positive.");
            }
            JsonWriter writer = new JsonWriter("./data/testWriterGeneralGame.json");
            writer.open();
            writer.write(gg);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralGame.json");
            gg = reader.read();
            assertEquals("My game", gg.getName());
            assertEquals(2, gg.getGuessesSoFar().length());
            assertEquals(3, gg.getAttempts());
            assertEquals(500, gg.getMax());
            checkGuess(15, gg.getGuessesSoFar().getGuessInPosition(0));
            checkGuess(10, gg.getGuessesSoFar().getGuessInPosition(1));
            assertEquals(gg.getGuessesSoFar().getGuessInPosition(0).getAnswer(),
                    gg.getGuessesSoFar().getGuessInPosition(1).getAnswer());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

}
