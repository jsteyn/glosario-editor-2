package com.jannetta.glossary.model;

public class LanguageEntry {

    private String language; // two letter iso code
    private String term;
    private String definition;
    private String acronym;

    /**
     * Get two letter code for language
     * @return
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Set two letter code of language
     * @param language
     */
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

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }
}
