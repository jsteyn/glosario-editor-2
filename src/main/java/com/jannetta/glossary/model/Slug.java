package com.jannetta.glossary.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Slug {

    private String slug;
    private ArrayList<String> references;
    private LinkedHashMap<String, LanguageEntry> languageEntries = new LinkedHashMap<String, LanguageEntry>();

    public Slug(String slug) {
        this.slug = slug;
        references = new ArrayList<>();
        languageEntries = new LinkedHashMap<String, LanguageEntry>();
    }

    public Slug(String slug, ArrayList<String> references, LinkedHashMap<String, LanguageEntry> languageEntries) {
        this.slug = slug;
        this.references = references;
        this.languageEntries = languageEntries;
    }

    public String getSlug() {
        return slug;
    }


    public void setSlug(String slug) {
        this.slug = slug;
    }

    public ArrayList<String> getReferences() {
        return references;
    }

    public void setReferences(ArrayList<String> references) {
        this.references = references;
    }

    public LinkedHashMap<String, LanguageEntry> getLanguageEntries() {
        return this.languageEntries;
    }

    public void setLanguageEntries(LinkedHashMap<String, LanguageEntry> languageEntry) {
        this.languageEntries = languageEntry;
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append("- slug:" + slug);
        return sb.toString();
    }

    
}
