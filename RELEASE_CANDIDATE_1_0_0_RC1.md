# Ahorcado Android 1.0.0-rc1

Base: v0.8.0.

This is the first release candidate for Google Play.

## Frozen application identity
- Application ID: `com.llorieb.ahorcado`
- Name: `Ahorcado`

## Release-candidate change
- `android:supportsRtl` changed from `true` to `false`.

Ahorcado 1.0 supports Spanish and English only. Both layouts are LTR. Unsupported
device languages fall back to English, so disabling RTL prevents an Arabic or
Hebrew system locale from mirroring an English fallback UI.

## Version
- versionCode: 26
- versionName: 1.0.0-rc1

## Functional scope
No gameplay, corpus, language selection, compact-height mode, audio, splash,
launcher icon, preferences, or navigation behavior was changed from v0.8.0.

This build is intended for final QA before producing 1.0.0.
