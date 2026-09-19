package com.llorieb.ahorcado;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.os.ConfigurationCompat;
import androidx.core.graphics.Insets;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.llorieb.ahorcado.core.GameEngine;
import com.llorieb.ahorcado.data.WordEntry;
import com.llorieb.ahorcado.data.WordRepository;
import com.llorieb.ahorcado.ui.FlowLayout;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int MAX_MISTAKES = 6;
    private static final int DEFAULT_TIME = 30;
    private static final String DEFAULT_CATEGORY = "countries";
    private static final String PREFS_NAME = "ahorcado_preferences";
    private static final String PREF_CATEGORY = "category";
    private static final String PREF_TIME = "time_seconds";
    private static final long WRONG_FEEDBACK_MS = 220L;

    private static final int MENU_HELP = 1001;
    private static final int MENU_ABOUT = 1002;
    private static final int MENU_EXIT = 1003;

    private static final String LANGUAGE_AUTO = "auto";
    private static final String LANGUAGE_ES = "es";
    private static final String LANGUAGE_EN = "en";

    // Height available to the actual game area (between header and action bar).
    // Shorter viewports use a compact presentation automatically.
    private static final int COMPACT_VIEWPORT_DP = 590;
    private static final int TABLET_VIEWPORT_DP = 560;

    private static final String[] LETTERS_ES = {
            "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N",
            "Ñ", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };

    private static final String[] LETTERS_EN = {
            "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P",
            "Q", "R", "S", "T", "U", "V", "W", "X",
            "Y", "Z"
    };

    private static final int[] HANGMAN_IMAGES = {
            R.drawable.hangman_0, R.drawable.hangman_1, R.drawable.hangman_2,
            R.drawable.hangman_3, R.drawable.hangman_4, R.drawable.hangman_5,
            R.drawable.hangman_6
    };

    private static final int[] CLOCK_IMAGES = {
            R.drawable.clock_0, R.drawable.clock_1, R.drawable.clock_2,
            R.drawable.clock_3, R.drawable.clock_4, R.drawable.clock_5,
            R.drawable.clock_6, R.drawable.clock_7, R.drawable.clock_8,
            R.drawable.clock_9, R.drawable.clock_10, R.drawable.clock_11,
            R.drawable.clock_12
    };

    private final GameEngine engine = new GameEngine(MAX_MISTAKES);
    private final WordRepository wordRepository = new WordRepository();
    private final Random random = new Random();
    private final Map<Character, MaterialButton> keyboardButtons = new HashMap<>();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private SharedPreferences preferences;
    private WordEntry currentWord;
    private String lastWordId;
    private String selectedCategory;
    private int selectedTime;
    private int gameDuration;
    private int remainingSeconds;
    private boolean corpusLoaded;
    private boolean lifecyclePaused;
    private boolean guessOverlayVisible;
    private boolean guessPausedTimer;
    private boolean overlayPaused;
    private boolean compactHeightMode;
    private boolean tabletMode;

    private CountDownTimer countDownTimer;

    private TextView titleView;
    private TextView categoryValue;
    private TextView timeValue;
    private TextView readyMessage;
    private ImageView characterImage;
    private ImageView clockImage;
    private FlowLayout wordContainer;
    private GridLayout keyboardGrid;
    private MaterialButton startButton;
    private MaterialButton stopButton;
    private MaterialButton guessButton;
    private ImageButton preferencesButton;
    private ImageButton overflowButton;
    private FrameLayout overlayHost;
    private View activeOverlayCard;
    private MaterialCardView characterCard;
    private MaterialCardView clockCard;
    private MaterialCardView wordCard;
    private View characterPanel;
    private View clockContent;
    private View wordContent;
    private NestedScrollView gameScroll;
    private View screenContent;
    private LinearLayout topBar;
    private LinearLayout contentColumn;
    private LinearLayout actionBar;

    private View guessOverlay;
    private MaterialCardView guessOverlayCard;
    private TextInputEditText guessOverlayInput;
    private MaterialButton guessCancelButton;
    private MaterialButton guessConfirmButton;

    private View resultOverlay;
    private MaterialCardView resultCard;
    private LinearLayout resultCardContent;
    private ImageView resultImage;
    private TextView resultTitle;
    private TextView resultMessage;
    private TextView resultWord;
    private MaterialButton resultPlayAgainButton;
    private MaterialButton resultCloseButton;

    private SoundPool soundPool;
    private int soundWrong;
    private int soundDefeat;
    private int soundVictory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Ahorcado);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindViews();
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        selectedCategory = preferences.getString(PREF_CATEGORY, DEFAULT_CATEGORY);
        selectedTime = preferences.getInt(PREF_TIME, DEFAULT_TIME);
        gameDuration = selectedTime;
        remainingSeconds = selectedTime;

        setupSounds();
        setupKeyboard();
        setupActions();
        loadCorpus();
        updatePreferenceLabels();
        setInitialState();
        requestAdaptiveVerticalLayout();

        // Re-evaluate the responsive mode when the usable game viewport changes
        // (rotation, split screen, resize, different system-bar dimensions).
        gameScroll.addOnLayoutChangeListener((v, left, top, right, bottom,
                                              oldLeft, oldTop, oldRight, oldBottom) -> {
            int newHeight = bottom - top;
            int oldHeight = oldBottom - oldTop;
            int newWidth = right - left;
            int oldWidth = oldRight - oldLeft;
            if (newHeight != oldHeight || newWidth != oldWidth) {
                requestAdaptiveVerticalLayout();
            }
        });

        setupBackNavigation();
    }

    private void bindViews() {
        titleView = findViewById(R.id.titleView);
        categoryValue = findViewById(R.id.categoryValue);
        timeValue = findViewById(R.id.timeValue);
        readyMessage = findViewById(R.id.readyMessage);
        characterImage = findViewById(R.id.characterImage);
        clockImage = findViewById(R.id.clockImage);
        wordContainer = findViewById(R.id.wordContainer);
        keyboardGrid = findViewById(R.id.keyboardGrid);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        guessButton = findViewById(R.id.guessButton);
        preferencesButton = findViewById(R.id.preferencesButton);
        overflowButton = findViewById(R.id.overflowButton);
        overlayHost = findViewById(R.id.overlayHost);
        characterCard = findViewById(R.id.characterCard);
        clockCard = findViewById(R.id.clockCard);
        wordCard = findViewById(R.id.wordCard);
        characterPanel = findViewById(R.id.characterPanel);
        clockContent = findViewById(R.id.clockContent);
        wordContent = findViewById(R.id.wordContent);
        gameScroll = findViewById(R.id.gameScroll);
        screenContent = findViewById(R.id.screenContent);
        topBar = findViewById(R.id.topBar);
        contentColumn = findViewById(R.id.contentColumn);
        actionBar = findViewById(R.id.actionBar);

        guessOverlay = findViewById(R.id.guessOverlay);
        guessOverlayCard = findViewById(R.id.guessOverlayCard);
        guessOverlayInput = findViewById(R.id.guessOverlayInput);
        guessCancelButton = findViewById(R.id.guessCancelButton);
        guessConfirmButton = findViewById(R.id.guessConfirmButton);

        resultOverlay = findViewById(R.id.resultOverlay);
        resultCard = findViewById(R.id.resultCard);
        resultCardContent = findViewById(R.id.resultCardContent);
        resultImage = findViewById(R.id.resultImage);
        resultTitle = findViewById(R.id.resultTitle);
        resultMessage = findViewById(R.id.resultMessage);
        resultWord = findViewById(R.id.resultWord);
        resultPlayAgainButton = findViewById(R.id.resultPlayAgainButton);
        resultCloseButton = findViewById(R.id.resultCloseButton);
    }

    private void setupSounds() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(attributes)
                .build();

        soundWrong = soundPool.load(this, R.raw.wrong_letter_soft_bup, 1);
        soundDefeat = soundPool.load(this, R.raw.game_over_glass_descent, 1);
        soundVictory = soundPool.load(this, R.raw.victory_glass_ascent, 1);
    }

    private void setupKeyboard() {
        keyboardGrid.removeAllViews();
        keyboardButtons.clear();

        int margin = compactHeightMode ? dp(1) : (tabletMode ? dp(3) : dp(2));
        int buttonHeight = compactHeightMode
                ? (tabletMode ? dp(38) : dp(36))
                : getResources().getDimensionPixelSize(R.dimen.keyboard_key_height);

        String[] letters = getKeyboardLetters();
        for (int i = 0; i < letters.length; i++) {
            String value = letters[i];
            char letter = value.charAt(0);

            MaterialButton button = new MaterialButton(this);
            button.setText(value);
            button.setTextSize(
                    compactHeightMode
                            ? (tabletMode ? 14.5f : 13.5f)
                            : (tabletMode ? 17f : 15f));
            button.setAllCaps(false);
            button.setMinWidth(0);
            button.setMinimumWidth(0);
            button.setInsetTop(0);
            button.setInsetBottom(0);
            button.setPadding(0, 0, 0, 0);
            button.setCornerRadius(dp(7));
            button.setStrokeWidth(dp(1));
            button.setRippleColor(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.key_pressed)));
            styleKeyDefault(button);
            button.setOnClickListener(v -> onLetterPressed(letter, button));

            int row;
            int column;
            if (i < 8) {
                row = 0;
                column = i;
            } else if (i < 16) {
                row = 1;
                column = i - 8;
            } else if (i < 24) {
                row = 2;
                column = i - 16;
            } else {
                row = 3;
                column = (i - 24) + 2;
            }

            GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                    GridLayout.spec(row),
                    GridLayout.spec(column, 1f));
            params.width = 0;
            params.height = buttonHeight;
            params.setMargins(margin, margin, margin, margin);
            keyboardGrid.addView(button, params);
            keyboardButtons.put(letter, button);
        }
    }

    private void setupActions() {
        startButton.setOnClickListener(v -> startGame());
        stopButton.setOnClickListener(v -> stopGame());
        guessButton.setOnClickListener(v -> showGuessOverlay());
        preferencesButton.setOnClickListener(v -> showPreferencesOverlay());
        overflowButton.setOnClickListener(v -> showOverflowMenu());

        guessCancelButton.setOnClickListener(v -> hideGuessOverlay(true));
        guessConfirmButton.setOnClickListener(v -> submitGuessFromOverlay());
        guessOverlayInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submitGuessFromOverlay();
                return true;
            }
            return false;
        });

        resultPlayAgainButton.setOnClickListener(v -> {
            hideResultOverlayImmediate();
            startGame();
        });
        resultCloseButton.setOnClickListener(v -> hideResultOverlayImmediate());
    }

    private void loadCorpus() {
        try {
            wordRepository.load(getResources().openRawResource(R.raw.words));
            corpusLoaded = wordRepository.size() > 0;
        } catch (IOException | RuntimeException e) {
            corpusLoaded = false;
            Toast.makeText(this, R.string.corpus_error, Toast.LENGTH_LONG).show();
        }
    }

    private void setInitialState() {
        characterImage.setImageResource(HANGMAN_IMAGES[0]);
        clockImage.setImageResource(CLOCK_IMAGES[0]);
        timeValue.setText(String.valueOf(selectedTime));
        readyMessage.setVisibility(View.VISIBLE);
        clearWordDisplay();
        setKeyboardEnabled(false);
        restoreCharacterPanel();
        hideGuessOverlay(false);
        hideResultOverlayImmediate();
        updateGameControls(false);
        requestAdaptiveVerticalLayout();
    }

    private void startGame() {
        if (!corpusLoaded) {
            Toast.makeText(this, R.string.corpus_error, Toast.LENGTH_LONG).show();
            return;
        }

        String requestedLocale = getEffectiveGameLocale();
        currentWord = wordRepository.randomWord(requestedLocale, selectedCategory, random, lastWordId);
        if (currentWord == null) {
            Toast.makeText(this, R.string.word_error, Toast.LENGTH_LONG).show();
            return;
        }

        lastWordId = currentWord.getId();
        engine.start(currentWord.getText(), currentWord.getAliases());
        gameDuration = selectedTime;
        remainingSeconds = gameDuration;
        lifecyclePaused = false;

        hideGuessOverlay(false);
        hideResultOverlayImmediate();
        restoreCharacterPanel();
        resetKeyboard();
        readyMessage.setVisibility(View.GONE);
        characterImage.setImageResource(HANGMAN_IMAGES[0]);
        clockImage.setImageResource(CLOCK_IMAGES[0]);
        timeValue.setText(String.valueOf(remainingSeconds));
        renderWord(false);
        updateGameControls(true);
        requestAdaptiveVerticalLayout();
        startTimer(remainingSeconds);
    }

    private void stopGame() {
        if (!engine.isRunning()) {
            return;
        }
        cancelTimer();
        engine.stop();
        hideGuessOverlay(false);
        updateGameControls(false);
        setKeyboardEnabled(false);
        readyMessage.setText(R.string.game_stopped);
        readyMessage.setVisibility(View.VISIBLE);
        requestAdaptiveVerticalLayout();
    }

    private void onLetterPressed(char letter, MaterialButton button) {
        GameEngine.GuessResult result = engine.guessLetter(letter);

        if (result == GameEngine.GuessResult.NOT_RUNNING
                || result == GameEngine.GuessResult.ALREADY_GUESSED) {
            return;
        }

        button.setEnabled(false);

        if (result == GameEngine.GuessResult.CORRECT || result == GameEngine.GuessResult.WON) {
            styleKeyCorrect(button);
            renderWord(false);
        } else {
            styleKeyWrong(button);
            updateHangman();

            if (result == GameEngine.GuessResult.INCORRECT) {
                flashWrongAnswer();
                playSound(soundWrong, 1.00f);
            }
        }

        if (result == GameEngine.GuessResult.WON) {
            finishGame(ResultType.VICTORY);
        } else if (result == GameEngine.GuessResult.LOST) {
            finishGame(ResultType.DEFEAT);
        }
    }

    private void showGuessOverlay() {
        if (!engine.isRunning() || guessOverlayVisible) {
            return;
        }

        cancelTimer();
        guessPausedTimer = true;
        guessOverlayVisible = true;

        guessOverlayInput.setText("");
        guessOverlayInput.setError(null);

        guessOverlay.setVisibility(View.VISIBLE);
        guessOverlay.bringToFront();
        guessOverlay.setAlpha(0f);
        guessOverlayCard.setScaleX(0.94f);
        guessOverlayCard.setScaleY(0.94f);

        guessOverlay.animate()
                .alpha(1f)
                .setDuration(180)
                .start();

        guessOverlayCard.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(180)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        guessOverlayInput.postDelayed(() -> {
            guessOverlayInput.requestFocus();
            InputMethodManager keyboard =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (keyboard != null) {
                keyboard.showSoftInput(guessOverlayInput, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 120);
    }

    private void submitGuessFromOverlay() {
        if (!guessOverlayVisible || !engine.isRunning()) {
            return;
        }

        String answer = guessOverlayInput.getText() == null
                ? ""
                : guessOverlayInput.getText().toString().trim();

        if (answer.isEmpty()) {
            guessOverlayInput.setError(getString(R.string.guess_hint));
            return;
        }

        GameEngine.GuessResult result = engine.guessWord(answer);
        hideGuessOverlay(false);

        if (result == GameEngine.GuessResult.WON) {
            finishGame(ResultType.VICTORY);
        } else if (result == GameEngine.GuessResult.LOST) {
            updateHangman();
            finishGame(ResultType.WRONG_GUESS);
        }
    }

    private void hideGuessOverlay(boolean resumeTimer) {
        if (guessOverlay == null) {
            return;
        }

        boolean wasVisible = guessOverlayVisible;
        guessOverlayVisible = false;

        guessOverlay.animate().cancel();
        if (guessOverlayCard != null) {
            guessOverlayCard.animate().cancel();
            guessOverlayCard.setScaleX(1f);
            guessOverlayCard.setScaleY(1f);
        }

        if (guessOverlayInput != null) {
            InputMethodManager keyboard =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (keyboard != null) {
                keyboard.hideSoftInputFromWindow(guessOverlayInput.getWindowToken(), 0);
            }
            guessOverlayInput.clearFocus();
        }

        guessOverlay.setAlpha(0f);
        guessOverlay.setVisibility(View.GONE);

        if (resumeTimer && wasVisible && guessPausedTimer
                && engine.isRunning() && remainingSeconds > 0) {
            startTimer(remainingSeconds);
        }
        guessPausedTimer = false;
    }

    private void finishGame(ResultType type) {
        cancelTimer();
        hideGuessOverlay(false);
        restoreCharacterPanel();
        engine.revealAll();
        renderWord(true);
        updateHangman();
        updateGameControls(false);
        setKeyboardEnabled(false);
        requestAdaptiveVerticalLayout();

        int sound = type == ResultType.VICTORY ? soundVictory : soundDefeat;
        playSound(sound, type == ResultType.VICTORY ? 0.86f : 0.88f);
        showResultOverlay(type);
    }

    private void showResultOverlay(ResultType type) {
        int iconResource;
        int backgroundResource;
        int titleResource;
        int iconSize;
        int wordColor;

        if (type == ResultType.VICTORY) {
            iconResource = HANGMAN_IMAGES[0];
            backgroundResource = R.drawable.result_card_victory;
            titleResource = R.string.victory_overlay_title;
            iconSize = 125;
            wordColor = ContextCompat.getColor(this, R.color.result_word_victory);
        } else if (type == ResultType.TIMEOUT) {
            iconResource = CLOCK_IMAGES[CLOCK_IMAGES.length - 1];
            backgroundResource = R.drawable.result_card_timeout;
            titleResource = R.string.timeout_overlay_title;
            iconSize = 92;
            wordColor = ContextCompat.getColor(this, R.color.result_word_default);
        } else {
            iconResource = HANGMAN_IMAGES[MAX_MISTAKES];
            backgroundResource = R.drawable.result_card_defeat;
            titleResource = type == ResultType.WRONG_GUESS
                    ? R.string.wrong_guess_overlay_title
                    : R.string.defeat_overlay_title;
            iconSize = 125;
            wordColor = ContextCompat.getColor(this, R.color.result_word_default);
        }

        resultCardContent.setBackgroundResource(backgroundResource);
        resultImage.setImageResource(iconResource);
        ViewGroup.LayoutParams imageParams = resultImage.getLayoutParams();
        imageParams.width = dp(iconSize);
        imageParams.height = dp(iconSize);
        resultImage.setLayoutParams(imageParams);

        resultTitle.setText(titleResource);
        resultMessage.setText(R.string.result_word_intro);
        resultWord.setText(engine.getSecretWord());
        resultWord.setTextColor(wordColor);

        resultOverlay.setVisibility(View.VISIBLE);
        resultOverlay.bringToFront();
        resultOverlay.setAlpha(0f);
        resultCard.setScaleX(0.94f);
        resultCard.setScaleY(0.94f);

        resultOverlay.animate()
                .alpha(1f)
                .setDuration(180)
                .start();

        resultCard.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(180)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void hideResultOverlayImmediate() {
        if (resultOverlay == null) {
            return;
        }
        resultOverlay.animate().cancel();
        if (resultCard != null) {
            resultCard.animate().cancel();
            resultCard.setScaleX(1f);
            resultCard.setScaleY(1f);
        }
        resultOverlay.setAlpha(0f);
        resultOverlay.setVisibility(View.GONE);
    }

    private void timeoutGame() {
        if (!engine.isRunning()) {
            return;
        }
        remainingSeconds = 0;
        timeValue.setText("0");
        clockImage.setImageResource(CLOCK_IMAGES[CLOCK_IMAGES.length - 1]);
        engine.stop();
        finishGame(ResultType.TIMEOUT);
    }

    private void startTimer(int seconds) {
        cancelTimer();
        if (!engine.isRunning() || seconds <= 0) {
            return;
        }

        remainingSeconds = seconds;
        countDownTimer = new CountDownTimer(seconds * 1000L, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) Math.ceil(millisUntilFinished / 1000.0);
                remainingSeconds = Math.min(gameDuration, Math.max(0, secondsLeft));
                timeValue.setText(String.valueOf(remainingSeconds));
                updateClock();
            }

            @Override
            public void onFinish() {
                timeoutGame();
            }
        }.start();
    }

    private void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void updateClock() {
        if (gameDuration <= 0) {
            return;
        }
        int elapsed = gameDuration - remainingSeconds;
        int last = CLOCK_IMAGES.length - 1;
        int index = (int) Math.floor((double) elapsed * last / gameDuration);
        index = Math.max(0, Math.min(last, index));
        clockImage.setImageResource(CLOCK_IMAGES[index]);
    }

    private void updateHangman() {
        int index = Math.max(0, Math.min(MAX_MISTAKES, engine.getMistakes()));
        characterImage.setImageResource(HANGMAN_IMAGES[index]);
    }

    private void renderWord(boolean revealAll) {
        wordContainer.removeAllViews();
        if (currentWord == null) {
            return;
        }

        String secret = engine.getSecretWord();
        char[] visible = revealAll ? secret.toCharArray() : engine.getVisibleCharacters();

        int wordStart = 0;
        for (int i = 0; i <= secret.length(); i++) {
            boolean end = i == secret.length();
            boolean space = !end && secret.charAt(i) == ' ';
            if (end || space) {
                if (i > wordStart) {
                    wordContainer.addView(createWordGroup(secret, visible, wordStart, i));
                }
                wordStart = i + 1;
            }
        }
    }

    private View createWordGroup(String secret, char[] visible, int start, int end) {
        int length = end - start;
        int cellWidth;
        int cellHeight;
        int textSize;
        int spacing;

        if (compactHeightMode) {
            if (tabletMode) {
                if (length > 13) {
                    cellWidth = 23;
                    cellHeight = 32;
                    textSize = 15;
                    spacing = 1;
                } else if (length > 11) {
                    cellWidth = 25;
                    cellHeight = 34;
                    textSize = 16;
                    spacing = 1;
                } else if (length > 9) {
                    cellWidth = 28;
                    cellHeight = 36;
                    textSize = 17;
                    spacing = 1;
                } else {
                    cellWidth = 31;
                    cellHeight = 36;
                    textSize = 18;
                    spacing = 1;
                }
            } else if (length > 13) {
                cellWidth = 20;
                cellHeight = 30;
                textSize = 14;
                spacing = 1;
            } else if (length > 11) {
                cellWidth = 22;
                cellHeight = 32;
                textSize = 15;
                spacing = 1;
            } else if (length > 9) {
                cellWidth = 25;
                cellHeight = 34;
                textSize = 16;
                spacing = 1;
            } else {
                cellWidth = 28;
                cellHeight = 34;
                textSize = 17;
                spacing = 1;
            }
        } else if (tabletMode) {
            if (length > 13) {
                cellWidth = 27;
                cellHeight = 42;
                textSize = 18;
                spacing = 2;
            } else if (length > 11) {
                cellWidth = 30;
                cellHeight = 44;
                textSize = 19;
                spacing = 2;
            } else if (length > 9) {
                cellWidth = 34;
                cellHeight = 46;
                textSize = 20;
                spacing = 2;
            } else {
                cellWidth = 38;
                cellHeight = 48;
                textSize = 21;
                spacing = 3;
            }
        } else if (length > 13) {
            cellWidth = 22;
            cellHeight = 36;
            textSize = 16;
            spacing = 1;
        } else if (length > 11) {
            cellWidth = 25;
            cellHeight = 38;
            textSize = 17;
            spacing = 1;
        } else if (length > 9) {
            cellWidth = 28;
            cellHeight = 40;
            textSize = 18;
            spacing = 2;
        } else {
            cellWidth = 32;
            cellHeight = 40;
            textSize = 19;
            spacing = 2;
        }

        LinearLayout group = new LinearLayout(this);
        group.setOrientation(LinearLayout.HORIZONTAL);
        group.setGravity(Gravity.CENTER);

        for (int i = start; i < end; i++) {
            char secretChar = secret.charAt(i);
            TextView cell = new TextView(this);
            cell.setGravity(Gravity.CENTER);
            cell.setTextSize(textSize);
            cell.setTypeface(cell.getTypeface(), android.graphics.Typeface.BOLD);

            LinearLayout.LayoutParams lp;
            if (Character.isLetter(secretChar)) {
                boolean revealed = visible[i] != '_';
                lp = new LinearLayout.LayoutParams(dp(cellWidth), dp(cellHeight));
                cell.setBackgroundResource(revealed
                        ? R.drawable.letter_cell_revealed
                        : R.drawable.letter_cell);
                cell.setTextColor(ContextCompat.getColor(this,
                        revealed ? R.color.letter_revealed_text : R.color.text_primary));
                cell.setText(revealed ? String.valueOf(secretChar) : "");
            } else {
                lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, dp(cellHeight));
                cell.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
                cell.setText(String.valueOf(secretChar));
                cell.setPadding(dp(2), 0, dp(2), 0);
            }

            lp.setMargins(dp(spacing), 0, dp(spacing), 0);
            group.addView(cell, lp);
        }

        return group;
    }

    private void clearWordDisplay() {
        wordContainer.removeAllViews();
    }

    private void resetKeyboard() {
        for (MaterialButton button : keyboardButtons.values()) {
            styleKeyDefault(button);
            button.setAlpha(1f);
            button.setEnabled(true);
        }
    }

    private void setKeyboardEnabled(boolean enabled) {
        for (MaterialButton button : keyboardButtons.values()) {
            char letter = button.getText().charAt(0);
            boolean guessed = engine.hasGuessed(letter);

            if (enabled && !guessed) {
                button.setEnabled(true);
                button.setAlpha(1f);
            } else {
                button.setEnabled(false);
                button.setAlpha(guessed ? 1f : 0.48f);
            }
        }
    }

    private void styleKeyDefault(MaterialButton button) {
        button.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.key_default)));
        button.setStrokeColor(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.key_border)));
        button.setTextColor(ContextCompat.getColor(this, R.color.key_text));
    }

    private void styleKeyCorrect(MaterialButton button) {
        button.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.key_correct)));
        button.setStrokeColor(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.key_correct_border)));
        button.setTextColor(ContextCompat.getColor(this, R.color.key_correct_text));
        button.setAlpha(1f);
    }

    private void styleKeyWrong(MaterialButton button) {
        button.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.key_wrong)));
        button.setStrokeColor(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.key_wrong_border)));
        button.setTextColor(ContextCompat.getColor(this, R.color.key_wrong_text));
        button.setAlpha(1f);
    }

    private void updateGameControls(boolean playing) {
        setControlEnabled(startButton, !playing && corpusLoaded);
        setControlEnabled(stopButton, playing);
        setControlEnabled(guessButton, playing);
        setControlEnabled(preferencesButton, !playing);
    }

    private void setControlEnabled(View button, boolean enabled) {
        button.setEnabled(enabled);
        button.setAlpha(enabled ? 1f : 0.38f);
    }

    private void showOverflowMenu() {
        PopupMenu popup = new PopupMenu(this, overflowButton);
        popup.getMenu().add(0, MENU_HELP, 0, R.string.how_to_play);
        popup.getMenu().add(0, MENU_ABOUT, 1, R.string.about);
        popup.getMenu().add(0, MENU_EXIT, 2, R.string.exit);

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == MENU_HELP) {
                showHelpOverlay();
                return true;
            }
            if (item.getItemId() == MENU_ABOUT) {
                showAboutOverlay();
                return true;
            }
            if (item.getItemId() == MENU_EXIT) {
                if (engine.isRunning()) {
                    showExitOverlay();
                } else {
                    finishAndRemoveTask();
                }
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showPreferencesOverlay() {
        if (engine.isRunning()) {
            return;
        }

        View card = showOverlay(R.layout.overlay_preferences, 410, 620, false);
        if (card == null) {
            return;
        }

        RadioGroup categoryGroup = card.findViewById(R.id.categoryGroup);
        RadioGroup timeGroup = card.findViewById(R.id.timeGroup);
        RadioGroup languageGroup = card.findViewById(R.id.languageGroup);
        checkCategoryRadio(card, selectedCategory);
        checkTimeRadio(card, selectedTime);
        checkLanguageRadio(card, currentLanguagePreference());

        card.findViewById(R.id.cancelPreferencesButton)
                .setOnClickListener(v -> hideOverlay());

        card.findViewById(R.id.savePreferencesButton).setOnClickListener(v -> {
            selectedCategory = categoryFromCheckedId(categoryGroup.getCheckedRadioButtonId());
            selectedTime = timeFromCheckedId(timeGroup.getCheckedRadioButtonId());
            String selectedLanguage =
                    languageFromCheckedId(languageGroup.getCheckedRadioButtonId());
            String previousLanguage = currentLanguagePreference();

            preferences.edit()
                    .putString(PREF_CATEGORY, selectedCategory)
                    .putInt(PREF_TIME, selectedTime)
                    .apply();

            gameDuration = selectedTime;
            remainingSeconds = selectedTime;

            if (!selectedLanguage.equals(previousLanguage)) {
                // AppCompat recrea la Activity y vuelve a cargar UI + corpus
                // en el idioma elegido. No hay partida activa en Preferencias.
                applyLanguagePreference(selectedLanguage);
                return;
            }

            updatePreferenceLabels();
            clockImage.setImageResource(CLOCK_IMAGES[0]);

            hideOverlay();
            requestAdaptiveVerticalLayout();
        });
    }

    private void showHelpOverlay() {
        View card = showOverlay(R.layout.overlay_help, 420, 650, true);
        if (card != null) {
            card.findViewById(R.id.closeHelpButton)
                    .setOnClickListener(v -> hideOverlay());
        }
    }

    private void showAboutOverlay() {
        View card = showOverlay(R.layout.overlay_about, 372, 0, true);
        if (card == null) {
            return;
        }

        TextView version = card.findViewById(R.id.aboutVersion);
        version.setText(getString(R.string.about_version, getVersionName()));

        card.findViewById(R.id.privacyPolicyButton)
                .setOnClickListener(v -> showPrivacyOverlayFromAbout());

        card.findViewById(R.id.closeAboutButton)
                .setOnClickListener(v -> hideOverlay());
    }

    private void showPrivacyOverlayFromAbout() {
        View card = replaceActiveOverlay(R.layout.overlay_privacy, 420, 650);
        if (card == null) {
            return;
        }

        card.findViewById(R.id.closePrivacyButton)
                .setOnClickListener(v -> hideOverlay());
    }

    private void showExitOverlay() {
        View card = showOverlay(R.layout.overlay_exit, 360, 0, true);
        if (card == null) {
            return;
        }

        card.findViewById(R.id.cancelExitButton)
                .setOnClickListener(v -> hideOverlay());

        card.findViewById(R.id.confirmExitButton).setOnClickListener(v -> {
            overlayPaused = false;
            cancelTimer();
            hideGuessOverlay(false);
            finishAndRemoveTask();
        });
    }

    private View showOverlay(int layoutRes, int maxWidthDp, int maxHeightDp,
                             boolean pauseGame) {
        if (overlayHost == null || overlayHost.getVisibility() == View.VISIBLE) {
            return null;
        }

        if (pauseGame) {
            pauseForOverlay();
        } else {
            overlayPaused = false;
        }

        View card = getLayoutInflater().inflate(layoutRes, overlayHost, false);

        int availableWidth = overlayHost.getWidth() > 0
                ? overlayHost.getWidth() - dp(36)
                : getResources().getDisplayMetrics().widthPixels - dp(36);
        int width = Math.min(dp(maxWidthDp), availableWidth);

        int height = FrameLayout.LayoutParams.WRAP_CONTENT;
        if (maxHeightDp > 0) {
            int hostHeight = overlayHost.getHeight() > 0
                    ? overlayHost.getHeight()
                    : getResources().getDisplayMetrics().heightPixels;
            int availableHeight = hostHeight - dp(72);
            height = Math.min(dp(maxHeightDp), availableHeight);
        }

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(width, height, Gravity.CENTER);

        overlayHost.removeAllViews();
        overlayHost.addView(card, params);
        activeOverlayCard = card;

        overlayHost.setVisibility(View.VISIBLE);
        overlayHost.bringToFront();
        overlayHost.setAlpha(0f);

        card.setScaleX(0.95f);
        card.setScaleY(0.95f);

        overlayHost.animate()
                .alpha(1f)
                .setDuration(170)
                .start();

        card.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(170)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        return card;
    }

    private View replaceActiveOverlay(int layoutRes, int maxWidthDp,
                                      int maxHeightDp) {
        if (overlayHost == null || overlayHost.getVisibility() != View.VISIBLE) {
            return null;
        }

        View card = getLayoutInflater().inflate(layoutRes, overlayHost, false);

        int availableWidth = overlayHost.getWidth() > 0
                ? overlayHost.getWidth() - dp(36)
                : getResources().getDisplayMetrics().widthPixels - dp(36);
        int width = Math.min(dp(maxWidthDp), availableWidth);

        int height = FrameLayout.LayoutParams.WRAP_CONTENT;
        if (maxHeightDp > 0) {
            int hostHeight = overlayHost.getHeight() > 0
                    ? overlayHost.getHeight()
                    : getResources().getDisplayMetrics().heightPixels;
            int availableHeight = hostHeight - dp(72);
            height = Math.min(dp(maxHeightDp), availableHeight);
        }

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(width, height, Gravity.CENTER);

        overlayHost.removeAllViews();
        overlayHost.addView(card, params);
        activeOverlayCard = card;

        card.setScaleX(0.96f);
        card.setScaleY(0.96f);
        card.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(150)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        return card;
    }

    private void hideOverlay() {
        if (overlayHost == null || overlayHost.getVisibility() != View.VISIBLE) {
            return;
        }

        View card = activeOverlayCard;
        if (card != null) {
            card.animate()
                    .scaleX(0.97f)
                    .scaleY(0.97f)
                    .setDuration(120)
                    .start();
        }

        overlayHost.animate()
                .alpha(0f)
                .setDuration(120)
                .withEndAction(() -> {
                    overlayHost.setVisibility(View.GONE);
                    overlayHost.removeAllViews();
                    overlayHost.setAlpha(1f);
                    activeOverlayCard = null;
                    resumeAfterOverlay();
                })
                .start();
    }

    private void pauseForOverlay() {
        overlayPaused = engine.isRunning() && countDownTimer != null;
        if (overlayPaused) {
            cancelTimer();
        }
    }

    private void resumeAfterOverlay() {
        if (overlayPaused && engine.isRunning() && remainingSeconds > 0) {
            overlayPaused = false;
            startTimer(remainingSeconds);
        } else {
            overlayPaused = false;
        }
    }

    private String getVersionName() {
        try {
            return getPackageManager()
                    .getPackageInfo(getPackageName(), 0)
                    .versionName;
        } catch (Exception ignored) {
            return "1.0.0";
        }
    }

    private String currentLanguagePreference() {
        LocaleListCompat appLocales = AppCompatDelegate.getApplicationLocales();
        if (appLocales.isEmpty()) {
            return LANGUAGE_AUTO;
        }

        Locale locale = appLocales.get(0);
        if (locale != null && LANGUAGE_ES.equals(locale.getLanguage())) {
            return LANGUAGE_ES;
        }
        return LANGUAGE_EN;
    }

    private void applyLanguagePreference(String language) {
        LocaleListCompat locales;
        if (LANGUAGE_AUTO.equals(language)) {
            locales = LocaleListCompat.getEmptyLocaleList();
        } else {
            locales = LocaleListCompat.forLanguageTags(language);
        }
        AppCompatDelegate.setApplicationLocales(locales);
    }

    private void checkLanguageRadio(View root, String language) {
        int id;
        if (LANGUAGE_ES.equals(language)) {
            id = R.id.languageSpanish;
        } else if (LANGUAGE_EN.equals(language)) {
            id = R.id.languageEnglish;
        } else {
            id = R.id.languageAutomatic;
        }
        ((RadioGroup) root.findViewById(R.id.languageGroup)).check(id);
    }

    private String languageFromCheckedId(int id) {
        if (id == R.id.languageSpanish) {
            return LANGUAGE_ES;
        }
        if (id == R.id.languageEnglish) {
            return LANGUAGE_EN;
        }
        return LANGUAGE_AUTO;
    }

    private String getEffectiveGameLocale() {
        LocaleListCompat locales =
                ConfigurationCompat.getLocales(getResources().getConfiguration());
        Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);

        // Español para cualquier locale es*. El resto usa inglés,
        // que además es el fallback universal del corpus.
        if (locale != null && LANGUAGE_ES.equals(locale.getLanguage())) {
            return LANGUAGE_ES;
        }
        return LANGUAGE_EN;
    }

    private String[] getKeyboardLetters() {
        return LANGUAGE_ES.equals(getEffectiveGameLocale())
                ? LETTERS_ES
                : LETTERS_EN;
    }

    private void checkCategoryRadio(View root, String category) {
        int id;
        switch (category) {
            case "cities": id = R.id.categoryCities; break;
            case "car_brands": id = R.id.categoryCarBrands; break;
            case "rock_bands": id = R.id.categoryRockBands; break;
            default: id = R.id.categoryCountries;
        }
        ((RadioGroup) root.findViewById(R.id.categoryGroup)).check(id);
    }

    private void checkTimeRadio(View root, int seconds) {
        int id;
        switch (seconds) {
            case 15: id = R.id.time15; break;
            case 45: id = R.id.time45; break;
            case 60: id = R.id.time60; break;
            default: id = R.id.time30;
        }
        ((RadioGroup) root.findViewById(R.id.timeGroup)).check(id);
    }

    private String categoryFromCheckedId(int id) {
        if (id == R.id.categoryCities) return "cities";
        if (id == R.id.categoryCarBrands) return "car_brands";
        if (id == R.id.categoryRockBands) return "rock_bands";
        return "countries";
    }

    private int timeFromCheckedId(int id) {
        if (id == R.id.time15) return 15;
        if (id == R.id.time45) return 45;
        if (id == R.id.time60) return 60;
        return 30;
    }

    private void updatePreferenceLabels() {
        categoryValue.setText(categoryLabel(selectedCategory));
        timeValue.setText(String.valueOf(engine.isRunning() ? remainingSeconds : selectedTime));
    }

    private String categoryLabel(String category) {
        switch (category) {
            case "cities": return getString(R.string.category_cities);
            case "car_brands": return getString(R.string.category_car_brands);
            case "rock_bands": return getString(R.string.category_rock_bands);
            default: return getString(R.string.category_countries);
        }
    }

    private void flashWrongAnswer() {
        handler.removeCallbacksAndMessages(null);
        characterPanel.setBackgroundResource(R.drawable.bg_character_error);

        // Igual que en desktop: durante el efecto corto no se aceptan nuevas letras.
        for (MaterialButton button : keyboardButtons.values()) {
            button.setEnabled(false);
        }

        handler.postDelayed(() -> {
            restoreCharacterPanel();
            if (engine.isRunning()) {
                setKeyboardEnabled(true);
            }
        }, WRONG_FEEDBACK_MS);
    }

    private void restoreCharacterPanel() {
        if (characterPanel != null) {
            characterPanel.setBackgroundResource(R.drawable.bg_character_panel);
        }
    }

    private void playSound(int soundId, float volume) {
        if (soundPool != null && soundId != 0) {
            soundPool.play(soundId, volume, volume, 1, 0, 1f);
        }
    }

    private void requestAdaptiveVerticalLayout() {
        if (gameScroll == null || contentColumn == null) {
            return;
        }

        // Esperar a que Android termine de medir palabra, teclado y visibilidades.
        gameScroll.post(() -> contentColumn.post(this::applyAdaptiveVerticalLayout));
    }

    private void applyAdaptiveVerticalLayout() {
        if (gameScroll == null || contentColumn == null) {
            return;
        }

        int viewportHeight = gameScroll.getHeight();
        int viewportWidth = gameScroll.getWidth();
        if (viewportHeight <= 0 || viewportWidth <= 0) {
            return;
        }

        boolean wideWindow = viewportWidth >= dp(TABLET_VIEWPORT_DP);
        boolean widePortrait = wideWindow && viewportHeight > viewportWidth;
        boolean compactByHeight = viewportHeight < dp(COMPACT_VIEWPORT_DP);

        // First pass: choose the presentation from the actual window size.
        applyResponsivePresentation(compactByHeight, wideWindow, widePortrait);
        contentColumn.requestLayout();

        // Second pass: measure what the chosen presentation really needs.
        // If it overflows vertically, compact it regardless of the nominal
        // height threshold. This covers landscape tablets, split screen,
        // foldables and unusual system-bar configurations.
        contentColumn.post(() -> {
            int currentViewportHeight = gameScroll.getHeight();
            int naturalHeight = contentColumn.getHeight();
            int safetyMargin = dp(wideWindow ? 14 : 8);

            boolean contentDoesNotFit =
                    naturalHeight + safetyMargin > currentViewportHeight;
            boolean compactRequired = compactByHeight || contentDoesNotFit;

            if (compactRequired != compactHeightMode) {
                applyResponsivePresentation(
                        compactRequired, wideWindow, widePortrait);
                contentColumn.requestLayout();

                contentColumn.post(() ->
                        finalizeResponsivePositioning(
                                wideWindow, widePortrait, compactRequired));
                return;
            }

            finalizeResponsivePositioning(
                    wideWindow, widePortrait, compactRequired);
        });
    }

    private void finalizeResponsivePositioning(
            boolean wideWindow,
            boolean widePortrait,
            boolean compact) {
        if (compact) {
            // Compact layouts stay close to the edges of the usable viewport.
            // Scrolling remains a last-resort fallback.
            setActionBarBottomMargin(wideWindow ? dp(8) : dp(6));
            setContentVerticalBias(0.5f);
            return;
        }

        if (wideWindow) {
            // Wide layouts keep the game content attached to the action bar.
            // In portrait, the entire game+actions composition is centered
            // using the real spare height. RC3 capped this value at 140dp,
            // which left the composition visibly too low on tall tablets.
            setContentVerticalBias(1.0f);

            int parentHeight = screenContent.getHeight();
            int topHeight = topBar.getHeight();
            int actionHeight = actionBar.getHeight();
            int contentHeight = contentColumn.getHeight();
            int desiredGap = dp(widePortrait ? 18 : 14);

            int spare = parentHeight
                    - topHeight
                    - actionHeight
                    - contentHeight
                    - desiredGap;

            int centeredBottomMargin = Math.max(dp(10), spare / 2);

            if (widePortrait) {
                // A generous safety cap only prevents absurd positioning on
                // unusually tall/freeform windows; normal tablets use the
                // full calculated centering distance.
                setActionBarBottomMargin(
                        Math.min(centeredBottomMargin, dp(280)));
            } else {
                // Landscape behavior from RC3 is intentionally preserved.
                setActionBarBottomMargin(
                        Math.min(centeredBottomMargin, dp(140)));
            }

            int baseHeight =
                    getResources().getDimensionPixelSize(R.dimen.top_card_height);
            setTopCardHeight(
                    widePortrait ? baseHeight + dp(18) : baseHeight);

            contentColumn.requestLayout();
            return;
        }

        // Existing phone behavior: centered block with restrained growth on
        // tall displays.
        setActionBarBottomMargin(dp(6));
        setContentVerticalBias(0.5f);

        int baseCardHeight =
                getResources().getDimensionPixelSize(R.dimen.top_card_height);
        setTopCardHeight(baseCardHeight);
        contentColumn.requestLayout();

        contentColumn.post(() -> {
            if (compactHeightMode || tabletMode) {
                return;
            }

            int currentViewportHeight = gameScroll.getHeight();
            int naturalHeight = contentColumn.getHeight();
            int extra = currentViewportHeight - naturalHeight - dp(8);

            if (extra <= 0) {
                return;
            }

            int maxCardGrowth = dp(54);
            int cardGrowth =
                    Math.min(maxCardGrowth, Math.round(extra * 0.34f));
            setTopCardHeight(baseCardHeight + cardGrowth);
            contentColumn.requestLayout();
        });
    }

    private void applyResponsivePresentation(
            boolean compact,
            boolean wideWindow,
            boolean widePortrait) {
        boolean modeChanged =
                compactHeightMode != compact || tabletMode != wideWindow;
        compactHeightMode = compact;
        tabletMode = wideWindow;

        applyWidePortraitWidth(wideWindow, widePortrait);

        int baseCardHeight =
                getResources().getDimensionPixelSize(R.dimen.top_card_height);

        int compactCardHeight =
                wideWindow ? dp(188) : dp(165);
        int normalCardHeight =
                widePortrait ? baseCardHeight + dp(18) : baseCardHeight;
        setTopCardHeight(compact ? compactCardHeight : normalCardHeight);

        ViewGroup.MarginLayoutParams columnParams =
                (ViewGroup.MarginLayoutParams) contentColumn.getLayoutParams();
        columnParams.topMargin =
                compact ? dp(4) : (wideWindow ? dp(10) : dp(10));
        columnParams.bottomMargin =
                compact ? dp(4) : (wideWindow ? dp(10) : dp(10));
        contentColumn.setLayoutParams(columnParams);
        contentColumn.setPadding(
                contentColumn.getPaddingLeft(),
                contentColumn.getPaddingTop(),
                contentColumn.getPaddingRight(),
                compact ? dp(2) : (wideWindow ? dp(8) : dp(8)));

        ViewGroup.MarginLayoutParams characterPanelParams =
                (ViewGroup.MarginLayoutParams) characterPanel.getLayoutParams();
        int characterMargin =
                compact ? dp(5) : (wideWindow ? dp(9) : dp(8));
        characterPanelParams.setMargins(
                characterMargin, characterMargin,
                characterMargin, characterMargin);
        characterPanel.setLayoutParams(characterPanelParams);

        clockContent.setPadding(
                dp(wideWindow ? 10 : 8),
                compact
                        ? dp(wideWindow ? 8 : 8)
                        : dp(wideWindow ? 16 : 16),
                dp(wideWindow ? 10 : 8),
                compact
                        ? dp(wideWindow ? 7 : 7)
                        : dp(wideWindow ? 14 : 14));

        timeValue.setTextSize(
                compact
                        ? (wideWindow ? 22f : 20f)
                        : (widePortrait ? 29f : (wideWindow ? 27f : 24f)));

        ViewGroup.MarginLayoutParams wordCardParams =
                (ViewGroup.MarginLayoutParams) wordCard.getLayoutParams();
        wordCardParams.topMargin =
                compact ? dp(7) : (wideWindow ? dp(13) : dp(14));
        wordCard.setLayoutParams(wordCardParams);

        wordContent.setPadding(
                dp(wideWindow ? 14 : 12),
                compact ? dp(5) : (wideWindow ? dp(9) : dp(10)),
                dp(wideWindow ? 14 : 12),
                compact ? dp(6) : (wideWindow ? dp(10) : dp(12)));

        categoryValue.setTextSize(
                compact
                        ? (wideWindow ? 17f : 16f)
                        : (widePortrait ? 21f : (wideWindow ? 20f : 18f)));

        ViewGroup.MarginLayoutParams readyParams =
                (ViewGroup.MarginLayoutParams) readyMessage.getLayoutParams();
        readyParams.topMargin =
                compact ? dp(5) : (wideWindow ? dp(10) : dp(12));
        readyMessage.setLayoutParams(readyParams);
        readyMessage.setTextSize(
                compact
                        ? (wideWindow ? 12.5f : 12f)
                        : (wideWindow ? 14f : 13f));

        ViewGroup.MarginLayoutParams wordContainerParams =
                (ViewGroup.MarginLayoutParams) wordContainer.getLayoutParams();
        wordContainerParams.topMargin =
                compact ? dp(4) : (wideWindow ? dp(8) : dp(10));
        wordContainer.setLayoutParams(wordContainerParams);

        int wordMinHeight;
        if (compact) {
            wordMinHeight = wideWindow ? dp(58) : dp(52);
        } else {
            wordMinHeight = wideWindow ? dp(96) : dp(88);
        }
        wordContainer.setMinimumHeight(wordMinHeight);

        wordContainer.setPadding(
                wordContainer.getPaddingLeft(),
                compact ? dp(2) : (wideWindow ? dp(5) : dp(5)),
                wordContainer.getPaddingRight(),
                compact ? dp(2) : (wideWindow ? dp(5) : dp(5)));

        ViewGroup.MarginLayoutParams keyboardParams =
                (ViewGroup.MarginLayoutParams) keyboardGrid.getLayoutParams();
        keyboardParams.topMargin =
                compact ? dp(6) : (wideWindow ? dp(13) : dp(16));
        keyboardGrid.setLayoutParams(keyboardParams);

        applyHeaderAndActionBarSize(compact, wideWindow);
        applyKeyboardSizeForCurrentMode();

        if (modeChanged && currentWord != null) {
            renderWord(false);
        }
    }

    private void applyHeaderAndActionBarSize(boolean compact, boolean wideWindow) {
        int headerHeight = wideWindow ? dp(58) : dp(52);
        setViewHeight(topBar, headerHeight);
        titleView.setTextSize(wideWindow ? 23f : 21f);

        int headerButtonSize = wideWindow ? dp(50) : dp(48);
        setSquareViewSize(preferencesButton, headerButtonSize);
        setSquareViewSize(overflowButton, headerButtonSize);

        ViewGroup.MarginLayoutParams actionParams =
                (ViewGroup.MarginLayoutParams) actionBar.getLayoutParams();

        actionParams.height =
                wideWindow ? dp(62) : dp(56);
        actionBar.setLayoutParams(actionParams);

        int actionButtonHeight =
                wideWindow ? dp(50) : dp(48);
        setViewHeight(startButton, actionButtonHeight);
        setViewHeight(stopButton, actionButtonHeight);
        setViewHeight(guessButton, actionButtonHeight);

        float actionTextSize = wideWindow ? 13f : 12f;
        startButton.setTextSize(actionTextSize);
        stopButton.setTextSize(actionTextSize);
        guessButton.setTextSize(actionTextSize);

        if (wideWindow) {
            startButton.setIconSize(dp(20));
            stopButton.setIconSize(dp(20));
            guessButton.setIconSize(dp(20));
        } else {
            startButton.setIconSize(dp(18));
            stopButton.setIconSize(dp(18));
            guessButton.setIconSize(dp(19));
        }
    }

    private void applyKeyboardSizeForCurrentMode() {
        int keyHeight;
        int margin;
        float textSize;

        if (compactHeightMode) {
            keyHeight = tabletMode ? dp(38) : dp(36);
            margin = dp(1);
            textSize = tabletMode ? 14.5f : 13.5f;
        } else {
            keyHeight =
                    getResources().getDimensionPixelSize(R.dimen.keyboard_key_height);
            margin = tabletMode ? dp(3) : dp(2);
            textSize = tabletMode ? 17f : 15f;
        }

        for (MaterialButton button : keyboardButtons.values()) {
            ViewGroup.LayoutParams rawParams = button.getLayoutParams();
            if (!(rawParams instanceof GridLayout.LayoutParams)) {
                continue;
            }

            GridLayout.LayoutParams params =
                    (GridLayout.LayoutParams) rawParams;
            params.height = keyHeight;
            params.setMargins(margin, margin, margin, margin);
            button.setLayoutParams(params);
            button.setTextSize(textSize);
        }
    }

    private void applyWidePortraitWidth(
            boolean wideWindow,
            boolean widePortrait) {
        int resourceMaxWidth =
                getResources().getDimensionPixelSize(R.dimen.content_max_width);

        int targetMaxWidth = resourceMaxWidth;

        if (wideWindow && widePortrait) {
            // Use roughly 84% of the real viewport, with a sensible cap.
            // This makes portrait tablets feel intentionally designed rather
            // than like a phone layout floating in the middle.
            int viewportWidth = gameScroll.getWidth();
            int proportionalWidth = Math.round(viewportWidth * 0.84f);
            targetMaxWidth = Math.min(
                    proportionalWidth,
                    dp(760));
            targetMaxWidth = Math.max(
                    targetMaxWidth,
                    resourceMaxWidth);
        }

        setConstraintMaxWidth(contentColumn, targetMaxWidth);
        setConstraintMaxWidth(topBar, targetMaxWidth);
        setConstraintMaxWidth(actionBar, targetMaxWidth);
    }

    private void setConstraintMaxWidth(View view, int widthPx) {
        ViewGroup.LayoutParams rawParams = view.getLayoutParams();
        if (!(rawParams instanceof ConstraintLayout.LayoutParams)) {
            return;
        }

        ConstraintLayout.LayoutParams params =
                (ConstraintLayout.LayoutParams) rawParams;

        if (params.matchConstraintMaxWidth == widthPx) {
            return;
        }

        params.matchConstraintMaxWidth = widthPx;
        view.setLayoutParams(params);
    }

    private void setActionBarBottomMargin(int margin) {
        ViewGroup.MarginLayoutParams params =
                (ViewGroup.MarginLayoutParams) actionBar.getLayoutParams();

        if (params.bottomMargin == margin) {
            return;
        }

        params.bottomMargin = margin;
        actionBar.setLayoutParams(params);
    }

    private void setContentVerticalBias(float bias) {
        ViewGroup.LayoutParams rawParams = contentColumn.getLayoutParams();
        if (!(rawParams instanceof ConstraintLayout.LayoutParams)) {
            return;
        }

        ConstraintLayout.LayoutParams params =
                (ConstraintLayout.LayoutParams) rawParams;

        if (Math.abs(params.verticalBias - bias) < 0.001f) {
            return;
        }

        params.verticalBias = bias;
        contentColumn.setLayoutParams(params);
    }

    private void setViewHeight(View view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params.height == height) {
            return;
        }

        params.height = height;
        view.setLayoutParams(params);
    }

    private void setSquareViewSize(View view, int size) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params.width == size && params.height == size) {
            return;
        }

        params.width = size;
        params.height = size;
        view.setLayoutParams(params);
    }

    private void setTopCardHeight(int height) {
        ViewGroup.LayoutParams characterParams = characterCard.getLayoutParams();
        characterParams.height = height;
        characterCard.setLayoutParams(characterParams);

        ViewGroup.LayoutParams clockParams = clockCard.getLayoutParams();
        clockParams.height = height;
        clockCard.setLayoutParams(clockParams);
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (overlayHost != null
                        && overlayHost.getVisibility() == View.VISIBLE) {
                    hideOverlay();
                    return;
                }

                if (guessOverlayVisible) {
                    hideGuessOverlay(true);
                    return;
                }

                if (resultOverlay != null
                        && resultOverlay.getVisibility() == View.VISIBLE) {
                    hideResultOverlayImmediate();
                    return;
                }

                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (engine.isRunning() && countDownTimer != null) {
            cancelTimer();
            lifecyclePaused = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lifecyclePaused && engine.isRunning()
                && !guessOverlayVisible
                && (overlayHost == null || overlayHost.getVisibility() != View.VISIBLE)
                && remainingSeconds > 0) {
            lifecyclePaused = false;
            startTimer(remainingSeconds);
        }
    }

    @Override
    protected void onDestroy() {
        cancelTimer();
        hideGuessOverlay(false);
        handler.removeCallbacksAndMessages(null);
        if (overlayHost != null) {
            overlayHost.removeAllViews();
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        super.onDestroy();
    }

    private enum ResultType {
        VICTORY,
        DEFEAT,
        WRONG_GUESS,
        TIMEOUT
    }
}
