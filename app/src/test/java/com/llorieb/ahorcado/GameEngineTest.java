package com.llorieb.ahorcado;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.llorieb.ahorcado.core.GameEngine;

import org.junit.Test;

import java.util.Collections;

public class GameEngineTest {

    @Test
    public void accentedLettersAreRevealedWithBaseLetter() {
        GameEngine engine = new GameEngine(6);
        engine.start("PERÚ", Collections.emptyList());
        engine.guessLetter('U');
        assertEquals('Ú', engine.getVisibleCharacters()[3]);
    }

    @Test
    public void enyeRemainsDifferentFromN() {
        assertEquals('Ñ', GameEngine.normalizeLetter('Ñ'));
        assertEquals('N', GameEngine.normalizeLetter('N'));
    }

    @Test
    public void punctuationCanBeOmittedInFullGuess() {
        GameEngine engine = new GameEngine(6);
        engine.start("AC/DC", Collections.emptyList());
        assertEquals(GameEngine.GuessResult.WON, engine.guessWord("acdc"));
        assertTrue(engine.isSolved());
    }

    @Test
    public void wrongFullGuessEndsGame() {
        GameEngine engine = new GameEngine(6);
        engine.start("ARGENTINA", Collections.emptyList());
        assertEquals(GameEngine.GuessResult.LOST, engine.guessWord("BRASIL"));
        assertEquals(6, engine.getMistakes());
    }
}
