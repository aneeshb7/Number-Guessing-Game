package tests;

import model.Guess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

// Unit  for the Guess class
public class GuessTests {

    private int max;
    private int guessValue;
    private Guess myGuess;

    @BeforeEach
    public void setup() {
        max = 1000;
        guessValue = max / 2;
        myGuess = new Guess(guessValue, max);
    }

    @Test
    public void testConstructor() {
        assertEquals(guessValue, myGuess.getGuessValue());
        assertTrue(myGuess.getAnswer() >= 0 && myGuess.getAnswer() <= max);
    }

    @Test
    public void testHigherLowerXOREqual() {
        assertTrue(myGuess.isEqual() ^ myGuess.isHigher() ^ myGuess.isLower());
    }

    @Test
    public void testSetters() {
        myGuess.setAnswer(max/3);
        assertEquals(max/3, myGuess.getAnswer());
        myGuess.setGuessValue(max/3);
        assertEquals(max/3, myGuess.getGuessValue());
    }

    @Test
    public void testGenerateAnswer() {
        myGuess.generateAnswer(max);
        assertTrue(myGuess.getAnswer() >= 0 && myGuess.getAnswer() <= max);
    }

    @Test
    public void testIsEqual() {
        myGuess.setGuessValue(myGuess.getAnswer());
        assertTrue(myGuess.isEqual());
        myGuess.setGuessValue(myGuess.getAnswer() + 1);
        assertFalse(myGuess.isEqual());
    }

}