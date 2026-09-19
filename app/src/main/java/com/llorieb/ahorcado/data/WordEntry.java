package com.llorieb.ahorcado.data;

import java.util.Collections;
import java.util.List;

public final class WordEntry {
    private final String id;
    private final String category;
    private final String locale;
    private final String text;
    private final List<String> aliases;

    public WordEntry(String id, String category, String locale, String text, List<String> aliases) {
        this.id = id;
        this.category = category;
        this.locale = locale;
        this.text = text;
        this.aliases = aliases == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new java.util.ArrayList<>(aliases));
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getLocale() {
        return locale;
    }

    public String getText() {
        return text;
    }

    public List<String> getAliases() {
        return aliases;
    }
}
