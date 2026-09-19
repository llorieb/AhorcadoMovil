package com.llorieb.ahorcado.core;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Motor puro de Ahorcado. No depende de Android y puede probarse con JUnit/JVM.
 */
public final class GameEngine {

    public enum GuessResult {
        CORRECT,
        INCORRECT,
        WON,
        LOST,
        ALREADY_GUESSED,
        NOT_RUNNING
    }

    private final int maxMistakes;
    private final Set<Character> guessedLetters = new HashSet<>();
    private final List<String> acceptedAnswers = new ArrayList<>();

    private String secretWord = "";
    private char[] visibleCharacters = new char[0];
    private int mistakes;
    private boolean running;

    public GameEngine(int maxMistakes) {
        if (maxMistakes < 1) {
            throw new IllegalArgumentException("maxMistakes debe ser mayor que cero");
        }
        this.maxMistakes = maxMistakes;
    }

    public void start(String word, List<String> aliases) {
        if (word == null || word.trim().isEmpty()) {
            throw new IllegalArgumentException("La palabra no puede estar vacía");
        }

        secretWord = word.trim().toUpperCase(Locale.ROOT);
        visibleCharacters = new char[secretWord.length()];
        guessedLetters.clear();
        acceptedAnswers.clear();
        mistakes = 0;
        running = true;

        acceptedAnswers.add(secretWord);
        if (aliases != null) {
            for (String alias : aliases) {
                if (alias != null && !alias.trim().isEmpty()) {
                    acceptedAnswers.add(alias.trim());
                }
            }
        }

        for (int i = 0; i < secretWord.length(); i++) {
            char c = secretWord.charAt(i);
            visibleCharacters[i] = Character.isLetter(c) ? '_' : c;
        }
    }

    public GuessResult guessLetter(char letter) {
        if (!running) {
            return GuessResult.NOT_RUNNING;
        }

        char normalized = normalizeLetter(letter);
        if (!guessedLetters.add(normalized)) {
            return GuessResult.ALREADY_GUESSED;
        }

        boolean found = false;
        for (int i = 0; i < secretWord.length(); i++) {
            char secret = secretWord.charAt(i);
            if (Character.isLetter(secret) && normalizeLetter(secret) == normalized) {
                visibleCharacters[i] = secret;
                found = true;
            }
        }

        if (found) {
            if (isSolved()) {
                running = false;
                return GuessResult.WON;
            }
            return GuessResult.CORRECT;
        }

        mistakes++;
        if (mistakes >= maxMistakes) {
            mistakes = maxMistakes;
            running = false;
            return GuessResult.LOST;
        }

        return GuessResult.INCORRECT;
    }

    public GuessResult guessWord(String answer) {
        if (!running) {
            return GuessResult.NOT_RUNNING;
        }

        String normalizedAnswer = normalizeForComparison(answer);
        for (String accepted : acceptedAnswers) {
            if (normalizedAnswer.equals(normalizeForComparison(accepted))) {
                revealAll();
                running = false;
                return GuessResult.WON;
            }
        }

        mistakes = maxMistakes;
        running = false;
        return GuessResult.LOST;
    }

    public void stop() {
        running = false;
    }

    public void revealAll() {
        visibleCharacters = secretWord.toCharArray();
    }

    public boolean isSolved() {
        for (char c : visibleCharacters) {
            if (c == '_') {
                return false;
            }
        }
        return visibleCharacters.length > 0;
    }

    public boolean isRunning() {
        return running;
    }

    public int getMistakes() {
        return mistakes;
    }

    public int getMaxMistakes() {
        return maxMistakes;
    }

    public String getSecretWord() {
        return secretWord;
    }

    public char[] getVisibleCharacters() {
        return visibleCharacters.clone();
    }

    public boolean hasGuessed(char letter) {
        return guessedLetters.contains(normalizeLetter(letter));
    }

    /**
     * A, Á, À, Ä, Â -> A. La Ñ se mantiene distinta de N.
     */
    public static char normalizeLetter(char letter) {
        char upper = Character.toUpperCase(letter);
        if (upper == 'Ñ') {
            return 'Ñ';
        }

        String decomposed = Normalizer.normalize(
                String.valueOf(upper),
                Normalizer.Form.NFD
        );

        for (int i = 0; i < decomposed.length(); i++) {
            char c = decomposed.charAt(i);
            if (Character.getType(c) != Character.NON_SPACING_MARK) {
                return Character.toUpperCase(c);
            }
        }

        return upper;
    }

    /**
     * Ignora mayúsculas, tildes, espacios y signos; conserva Ñ y números.
     */
    public static String normalizeForComparison(String text) {
        if (text == null) {
            return "";
        }

        String clean = text.trim().toUpperCase(Locale.ROOT);
        StringBuilder normalized = new StringBuilder(clean.length());

        for (int i = 0; i < clean.length(); i++) {
            char c = clean.charAt(i);
            if (Character.isLetter(c)) {
                normalized.append(normalizeLetter(c));
            } else if (Character.isDigit(c)) {
                normalized.append(c);
            }
        }

        return normalized.toString();
    }
}
