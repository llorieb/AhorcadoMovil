# Ahorcado Android v0.8.0 — Compact-height mode

Base: v0.7.4.

## Responsive compact-height mode

The normal layout is preserved on ordinary/tall phones.

When the actual game viewport (the area between the top bar and bottom action
bar) is under 590dp high, Ahorcado switches automatically to a compact layout.

Compact mode changes:
- character/time cards: 165dp high;
- reduced card internal padding;
- smaller timer value;
- tighter vertical margins;
- smaller category/word area;
- slightly smaller word cells;
- word container minimum height: 52dp;
- keyboard keys: 36dp high with tighter spacing;
- Play / Stop / Guess stay unchanged for touch usability.

The existing NestedScrollView remains as a fallback for very small windows,
large accessibility font scaling, or extra word wrapping.

The decision is based on the actual available Android viewport in dp, not on
physical resolution or a specific phone model. The mode is also re-evaluated
when the viewport height changes.

No game logic, language/corpus behavior, sound, splash, launcher icon, or
settings behavior was changed.
