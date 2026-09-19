# Ahorcado Android 1.0.0-rc3

Base: 1.0.0-rc2.

## Responsive refinement after tablet portrait + landscape QA

RC3 keeps the same single-column game identity but makes the responsive
selection depend on both window dimensions and the content's real measured
height.

### Wide / tablet mode
- App-specific wide mode now starts at 560dp.
- New `values-w560dp` tier scales the board before the generic 600dp tier.
- Existing `w600dp` and `w840dp` tiers remain.

### Automatic fit check
After selecting the initial presentation, Ahorcado measures the actual content.
If it does not fit vertically, the app switches automatically to a compact
presentation even when the nominal height is above the old 590dp threshold.

This creates four effective states:
1. phone normal;
2. phone compact-height;
3. wide/tablet normal;
4. wide/tablet compact-height.

### Tablet portrait composition
On tall wide windows, the action bar is no longer visually isolated at the
bottom edge. Game content + action bar are treated as one composition:
- game content is anchored toward the actions;
- a calculated bottom margin centers the full composition vertically;
- tablet cards do not keep growing simply because more height is available.

### Tablet landscape
Wide compact mode keeps the extra horizontal space but reduces vertical cost:
- top cards 188dp;
- keyboard keys 38dp;
- tighter word panel / margins;
- action buttons remain usable.

## Frozen identity
- applicationId: `com.llorieb.ahorcado`
- versionCode: 28
- versionName: `1.0.0-rc3`

No gameplay, corpus, language, sound, permissions, splash or launcher-icon
behavior was changed.
