# Ahorcado Android 1.0.0-rc2

Base: 1.0.0-rc1.

## Tablet / wide-window mode

This release candidate adds a third responsive presentation mode without
changing gameplay:

- compact-height mode: short windows (existing v0.8.0 behavior);
- normal phone mode: unchanged;
- tablet/wide-window mode: activates when the current game viewport is at
  least 600dp wide.

Tablet mode uses the extra space in a controlled way:
- content maximum width: 660dp at 600dp+ windows, 720dp at 840dp+;
- character/time cards grow to a tablet-sized base height;
- timer, category, ready text and keyboard typography increase moderately;
- word cells and word area grow;
- keyboard keys become 52–54dp high depending on window width;
- header and bottom action controls scale slightly;
- the complete game block remains centered and constrained instead of
  stretching edge-to-edge.

The resource override now uses `w600dp` / `w840dp` rather than `sw600dp`, so
tablets placed in a narrow split-screen window can fall back to the phone
layout based on the actual available width.

Compact-height mode still takes precedence for vertically constrained windows.

## Frozen identity
- Application ID: `com.llorieb.ahorcado`
- versionCode: 27
- versionName: `1.0.0-rc2`

No corpus, language, sound, permissions, splash, launcher icon, or gameplay
logic was changed.
