# Ahorcado Android 1.0.0-rc4

Base: 1.0.0-rc3.

## Tablet portrait refinement

RC3 made landscape tablets fit correctly, but portrait QA showed that the
composition still looked too small and too low on tall tablet windows.

RC4 changes only the wide-portrait presentation:

- Detects `widePortrait` when the window is at least 560dp wide and taller
  than it is wide.
- Uses more of the available portrait width:
  - approximately 84% of the real viewport;
  - capped at 760dp;
  - never smaller than the existing resource-based max width.
- Applies the width consistently to header, game board and action bar.
- Increases the portrait top-card base height by 18dp.
- Slightly increases timer/category typography in portrait-wide mode.
- Removes RC3's restrictive 140dp centering cap for portrait tablets:
  the calculated bottom margin can now reach 280dp so the complete
  game + keyboard + action-bar composition can be truly centered.

## Landscape

Wide landscape behavior is intentionally preserved from RC3, including its
compact fallback and the 140dp positioning cap.

## Frozen identity
- applicationId: `com.llorieb.ahorcado`
- versionCode: 29
- versionName: `1.0.0-rc4`

No gameplay, corpus, language, audio, permissions, splash or launcher-icon
behavior changed.
