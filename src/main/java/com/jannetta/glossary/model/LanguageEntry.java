package com.jannetta.glossary.model;

public class LanguageEntry {

    private String language;
    private String term;
    private String definition;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String serialize() {
        return "";
    }
}
