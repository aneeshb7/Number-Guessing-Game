package tests;

import model.PreviousGuesses;
import model.Guess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

// Unit tests for the PreviousGuesses class
public class PreviousGuessTests {

    private int max;
    private PreviousGuesses myGuesses;
    private Guess myFirstGuess;
    private Guess mySecondGuess;
    private Guess myThirdGuess;

    @BeforeEach
    public void setup() {
        max = 1000;
        myGuesses = new PreviousGuesses();
        myFirstGuess = new Guess(max/2, max);
        mySecondGuess = new Guess(max/3, max);
        myThirdGuess = new Guess(max, max);
    }

    @Test
    public void testAddGuess() {
        myGuesses.addGuess(myFirstGuess);
        assertEquals(1, myGuesses.length());
        myGuesses.addGuess(mySecondGuess);
        assertEquals(2, myGuesses.length());
    }

    @Test
    public void testSameAnswer() {
        myGuesses.addGuess(myFirstGuess);
        myGuesses.addGuess(mySecondGuess);
        assertEquals(myFirstGuess.getAnswer(), mySecondGuess.getAnswer());
        myGuesses.addGuess(myThirdGuess);
        assertEquals(myFirstGuess.getAnswer(), myThirdGuess.getAnswer());
    }

    @Test
    public void testGetGuessInPosition() {
        myGuesses.addGuess(myFirstGuess);
        myGuesses.addGuess(mySecondGuess);
        myGuesses.addGuess(myThirdGuess);
        assertEquals(max/2, myGuesses.getGuessValInPosition(0));
        assertEquals(max/3, myGuesses.getGuessValInPosition(1));
        assertEquals(max, myGuesses.getGuessValInPosition(2));
        try {
            myGuesses.getGuessInPosition(4);
            fail("Exception should have been thrown.");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            myGuesses.getGuessValInPosition(7);
            fail("Exception should have been thrown.");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

}
