package tests;

import exceptions.NonPositiveAttemptsException;
import exceptions.NonPositiveMaximumException;
import model.Guess;
import model.GuessingGame;
import model.PreviousGuesses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

// Unit tests for the GuessingGame class
public class GuessingGameTests {
    private GuessingGame testGame;
    private Guess testGuess;

    @BeforeEach
    public void setup() {
        testGame = new GuessingGame("Test game");
        testGuess = new Guess(5,10);
    }

    @Test
    public void testSetAndDeductAttempts() {
        assertEquals("Test game", testGame.getName());
        try {
            testGame.setAttempts(1);
        } catch (NonPositiveAttemptsException e) {
            fail("Exception should not have been thrown.");
        }
        testGame.deductAttempts();
        assertEquals(0, testGame.getAttempts());
        try {
            testGame.setAttempts(0);
            fail("Exception should have been thrown.");
        } catch (NonPositiveAttemptsException e) {
            // expected
        }
        try {
            testGame.setAttempts(-5);
            fail("Exception should have been thrown.");
        } catch (NonPositiveAttemptsException e) {
            // expected
        }
    }

    @Test
    public void testAddGuessToGame() {
        Guess testGuess = new Guess(5,10);
        testGame.addGuessToGame(testGuess);
        assertEquals(1, testGame.getGuessesSoFar().length());
        assertEquals(testGuess, testGame.getGuessesSoFar().getGuessInPosition(0));
    }

    @Test
    public void testSetName() {
        testGame.setName("Setter");
        assertEquals("Setter", testGame.getName());
    }

    @Test
    public void testSetGuessesSoFar() {
        PreviousGuesses testGuesses = new PreviousGuesses();
        testGuesses.addGuess(testGuess);
        testGame.setGuessesSoFar(testGuesses);
        assertEquals(testGuesses, testGame.getGuessesSoFar());
    }

    @Test
    public void testSetMax() {
         try {
             testGame.setMax(100);
             assertEquals(100, testGame.getMax());
         } catch (NonPositiveMaximumException e) {
             fail("Exception should not have been thrown.");
         }
         try {
             testGame.setMax(0);
             fail("Exception should have been thrown.");
         } catch (NonPositiveMaximumException e) {
             // expected
         }
        try {
            testGame.setMax(-1);
            fail("Exception should have been thrown.");
        } catch (NonPositiveMaximumException e) {
            // expected
        }
    }
}
