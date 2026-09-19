package com.llorieb.ahorcado.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Repositorio en memoria para words.tsv.
 *
 * Formato:
 * id <TAB> category <TAB> locale <TAB> text <TAB> aliases
 *
 * aliases es opcional y separa respuestas alternativas con punto y coma.
 */
public final class WordRepository {

    public static final String FALLBACK_LOCALE = "en";

    private final Map<String, Map<String, List<WordEntry>>> byLocaleAndCategory = new HashMap<>();
    private final Set<String> uniqueRows = new HashSet<>();
    private int size;

    public void load(InputStream inputStream) throws IOException {
        byLocaleAndCategory.clear();
        uniqueRows.clear();
        size = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] columns = line.split("\\t", -1);
                if (columns.length < 4) {
                    throw new IOException("words.tsv inválido en línea " + lineNumber);
                }

                String id = columns[0].trim();
                String category = columns[1].trim();
                String locale = columns[2].trim().toLowerCase();
                String text = columns[3].trim();
                String aliasColumn = columns.length >= 5 ? columns[4].trim() : "";

                if (id.isEmpty() || category.isEmpty() || locale.isEmpty() || text.isEmpty()) {
                    throw new IOException("words.tsv tiene campos obligatorios vacíos en línea " + lineNumber);
                }

                String uniqueKey = locale + "\u0000" + id;
                if (!uniqueRows.add(uniqueKey)) {
                    throw new IOException("ID duplicado para locale en línea " + lineNumber + ": " + id);
                }

                List<String> aliases = new ArrayList<>();
                if (!aliasColumn.isEmpty()) {
                    for (String alias : aliasColumn.split(";")) {
                        String cleanAlias = alias.trim();
                        if (!cleanAlias.isEmpty()) {
                            aliases.add(cleanAlias);
                        }
                    }
                }

                WordEntry entry = new WordEntry(id, category, locale, text, aliases);
                byLocaleAndCategory
                        .computeIfAbsent(locale, ignored -> new HashMap<>())
                        .computeIfAbsent(category, ignored -> new ArrayList<>())
                        .add(entry);
                size++;
            }
        }
    }

    public WordEntry randomWord(String requestedLocale, String category, Random random, String excludedId) {
        String locale = resolveLocale(requestedLocale);
        List<WordEntry> candidates = byLocaleAndCategory
                .getOrDefault(locale, Collections.emptyMap())
                .getOrDefault(category, Collections.emptyList());

        if (candidates.isEmpty() && !FALLBACK_LOCALE.equals(locale)) {
            candidates = byLocaleAndCategory
                    .getOrDefault(FALLBACK_LOCALE, Collections.emptyMap())
                    .getOrDefault(category, Collections.emptyList());
        }

        if (candidates.isEmpty()) {
            return null;
        }

        if (candidates.size() == 1 || excludedId == null) {
            return candidates.get(random.nextInt(candidates.size()));
        }

        WordEntry selected;
        int attempts = 0;
        do {
            selected = candidates.get(random.nextInt(candidates.size()));
            attempts++;
        } while (selected.getId().equals(excludedId) && attempts < 12);

        return selected;
    }

    public String resolveLocale(String requestedLocale) {
        if (requestedLocale != null) {
            String normalized = requestedLocale.trim().toLowerCase();
            if (byLocaleAndCategory.containsKey(normalized)) {
                return normalized;
            }
        }
        return FALLBACK_LOCALE;
    }

    public boolean hasLocale(String locale) {
        return locale != null && byLocaleAndCategory.containsKey(locale.toLowerCase());
    }

    public int size() {
        return size;
    }

    public int count(String locale, String category) {
        String resolved = resolveLocale(locale);
        return byLocaleAndCategory
                .getOrDefault(resolved, Collections.emptyMap())
                .getOrDefault(category, Collections.emptyList())
                .size();
    }
}
