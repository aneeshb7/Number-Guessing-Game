package persistence;

import exceptions.NonPositiveAttemptsException;
import exceptions.NonPositiveMaximumException;
import model.Guess;
import model.GuessingGame;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.json.*;

// class name and comments taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
// Represents a reader that reads guessing game from JSON data stored in file
public class JsonReader {
    private java.lang.String source;

    // method taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    // EFFECTS: constructs reader to read from source file
    public JsonReader(java.lang.String source) {
        this.source = source;
    }

    // method template and name taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    // EFFECTS: reads game from file and returns it;
    //         throws an IOException if an error occurs reading data from file
    public GuessingGame read() throws IOException {
        java.lang.String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseGuessingGame(jsonObject);
    }

    private java.lang.String readFile(java.lang.String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<java.lang.String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    //method template taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    //EFFECTS: parses guessing game from JSON object and returns it
    private GuessingGame parseGuessingGame(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        GuessingGame gg = new GuessingGame(name);
        addAttemptsAndGuessesSoFar(gg, jsonObject);
        return gg;
    }

    // method template taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    // MODIFIES: gg
    // EFFECTS: parses attempts and guesses so far from JSON object and adds them to guessing game
    private void addAttemptsAndGuessesSoFar(GuessingGame gg, JSONObject jsonObject) {
        try {
            addJsonObjectAttemptsAndMax(gg, jsonObject);
        } catch (NonPositiveAttemptsException e) {
            System.out.println("Attempts must be positive.");
        } catch (NonPositiveMaximumException e) {
            System.out.println("Maximum must be positive.");
        }
        JSONArray jsonArray = jsonObject.getJSONArray("guesses");
        for (Object json : jsonArray) {
            JSONObject nextGuess = (JSONObject) json;
            addJsonObjectGuess(gg, nextGuess);
        }

    }

    // MODIFIES: gg
    // EFFECTS: parses guess from JSON object and adds to guesses so far in guessing game
    private void addJsonObjectGuess(GuessingGame gg, JSONObject jsonObject) {
        int guessValue = jsonObject.getInt("guess value");
        int answer = jsonObject.getInt("answer");
        Guess myGuess = new Guess(guessValue, gg.getMax());
        myGuess.setAnswer(answer);
        gg.addGuessToGame(myGuess);
    }

    // MODIFIES: gg
    // EFFECTS: parses attempts remaining and maximum from JSON object and attempts to add to game
    //          if attempts is not positive, throws NonPositiveAttemptsException,
    //          if maximum is not positive, throws NonPositiveMaximumException
    private void addJsonObjectAttemptsAndMax(GuessingGame gg, JSONObject jsonObj) throws NonPositiveAttemptsException,
            NonPositiveMaximumException  {
        int attempts = jsonObj.getInt("attempts remaining");
        if (attempts <= 0) {
            throw new NonPositiveAttemptsException("Attempts must be positive.");
        } else {
            gg.setAttempts(attempts);
        }
        int max = jsonObj.getInt("maximum");
        if (max <= 0) {
            throw new NonPositiveMaximumException("Maximum must be positive.");
        } else {
            gg.setMax(max);
        }
    }
}
