package com.jannetta.glossary.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Set;

import com.jannetta.glossary.model.LanguageEntry;
import com.jannetta.glossary.model.Slug;

public class YAML {

    public static LinkedHashMap<String, Slug> parseYAML(File filename) {
        LinkedHashMap<String, Slug> listOfSlugs = new LinkedHashMap<>();
        boolean EOF = false;
        try {
            Scanner sc = new Scanner(filename);
            Slug slug = null;
            String line = "";
            if (sc.hasNextLine()) {
                do {
                    if (sc.hasNextLine())
                        line = sc.nextLine();
                    else
                        EOF = true;
                    // If slug found
                    if (line.startsWith("- slug:")) {
                        String slugname = line.split(":")[1].strip();
                        slug = new Slug(slugname);
                        listOfSlugs.put(slugname, slug);
                    }
                    if (line.startsWith("  ref:")) {
                        // If references found
                        ArrayList<String> references = slug.getReferences();
                        if (sc.hasNextLine())
                            line = sc.nextLine();
                        else
                            EOF = true;
                        while (line.startsWith("    -")) {
                            String reference = line.strip().split(" ")[1].strip();
                            references.add(reference);
                            if (sc.hasNextLine())
                                line = sc.nextLine();
                            else
                                EOF = true;
                        }
                    }
                    // Language line
                    while (line.stripTrailing().matches("^  [a-z][a-z]:")) {
                        LinkedHashMap<String, LanguageEntry> languageEntries = slug.getLanguageEntries();
                        LanguageEntry languageEntry = new LanguageEntry();
                        String language = line.replace(':', ' ').strip().strip();
                        languageEntry.setLanguage(language);
                        languageEntries.put(language, languageEntry);
                        if (sc.hasNextLine())
                            line = sc.nextLine();
                        else {
                            line = "";
                            EOF = true;
                    }
                        // Get term
                        do {
                            if (line.startsWith("    term:")) {
                                String term = line.split(":")[1];
                                term = term.strip();
                                term = stripQuotes(term);
                                languageEntry.setTerm(term);
                                if (sc.hasNextLine())
                                    line = sc.nextLine();
                                else
                                    EOF = true;

                            }

                            if (line.startsWith("    acronym:")) {
                                String acronym = line.split(":")[1];
                                acronym = acronym.strip();
                                languageEntry.setAcronym(stripQuotes(acronym));
                                if (sc.hasNextLine())
                                    line = sc.nextLine();
                                else
                                    EOF = true;
                            }

                            // Get def line

                            // Read the def text
                            StringBuilder definition = new StringBuilder();
                            if (sc.hasNextLine()) {
                                line = sc.nextLine();
                            } else
                                EOF = true;
                            while (line.startsWith("      ")) {
                                definition.append(line + "\n");
                                if (sc.hasNextLine())
                                    line = sc.nextLine();
                                else {
                                    EOF = true;
                                    line = "";
                                }
                            }
                            languageEntry.setDefinition(
                                    definition.toString().replaceFirst("^      ", "").replaceAll("\n      ", "\n"));

                        } while (line.matches("^  [a-z][a-z]") || line.matches("    term:")
                                || line.matches("    acronymn:") || line.matches("      ")
                                || line.matches("^    def: >"));

                    }
                } while (!EOF);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return listOfSlugs;
    }

    private static String stripQuotes(String term) {
        if (term.startsWith("\""))
            term = term.substring(1);
        if (term.endsWith("\""))
            term = term.substring(0, term.length() - 1);
        return term;
    }

    public static void write(LinkedHashMap<String, Slug> listOfSlugs, File filename) {
        boolean firstline = true;
        try {
            PrintWriter pw = new PrintWriter(filename);
            Set<String> set = listOfSlugs.keySet();
            Iterator<String> s = set.iterator();
            while (s.hasNext()) {
                if (firstline) {
                    firstline = false;
                } else {
                    pw.println("\n");
                }
                Slug slug = listOfSlugs.get(s.next());
                // WRITE SLUG
                pw.println("- slug: " + slug.getSlug());
                // WRITE REFERENCES
                ArrayList<String> references = slug.getReferences();
                if (references.size() > 0) {
                    pw.println("  ref:");
                    for (int r = 0; r < references.size(); r++) {
                        pw.println("    - " + references.get(r));
                    }
                }
                // WRITE LANGUAGE ENTRIES
                LinkedHashMap<String, LanguageEntry> languages = slug.getLanguageEntries();
                if (languages.size() > 0) {
                    Set<String> set_languages = languages.keySet();
                    Iterator<String> i = set_languages.iterator();
                    while (i.hasNext()) {
                        LanguageEntry languageEntry = languages.get(i.next());
                        // LANGUAGE
                        pw.println("  " + languageEntry.getLanguage() + ":");
                        // TERM
                        if (languageEntry.getTerm() != null) {
                            if (languageEntry.getTerm().strip().equals(""))
                                languageEntry.setTerm(null);
                            else
                                pw.println("    term: \"" + languageEntry.getTerm().strip() + "\"");

                        }
                        // ACRONYMN
                        if (languageEntry.getAcronym() != null) {
                            if (languageEntry.getAcronym().strip().equals(""))
                                languageEntry.setAcronym(null);
                            else
                                pw.println("    acronym: \"" + languageEntry.getAcronym().strip() + "\"");
                        }
                        // DEFINITION
                        pw.println("    def: >");
                        pw.println(languageEntry.getDefinition().stripTrailing().replaceAll("^", "      ")
                                .replaceAll("\n", "\n      "));
                    }
                }
            }
            pw.close();
        } catch (

        FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
