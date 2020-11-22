package com.jannetta.glossary.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

import javax.swing.JButton;

import com.jannetta.glossary.model.LanguageCode;

import java.awt.event.ActionListener;

public class StaticUtils {
    public static boolean lockDocumentListeners = false;

    /**
     * Load slug for slug-buttons
     * 
     * @param actionListener
     * @param filename_from
     * @return
     */
    static public ArrayList<JButton> loadSlugButtons(ActionListener actionListener, String filename_from) {
        ArrayList<JButton> buttons = new ArrayList<>();
        try {
            Scanner sc = new Scanner(new File(filename_from));
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.startsWith("-")) {
                    String[] tokens = line.split(":");
                    JButton button = new JButton(tokens[1].strip());
                    buttons.add(button);
                    button.addActionListener(actionListener);
                    button.setActionCommand("btn_" + button.getText());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return buttons;
    }

    static public ArrayList<JButton> addSlugButton(ActionListener actionListener, ArrayList<JButton> buttons,
            String text) {
        JButton button = new JButton(text);
        buttons.add(button);
        button.addActionListener(actionListener);
        button.setActionCommand("btn_" + button.getText());
        return buttons;
    }

    static public LinkedHashMap<String, LanguageCode> loadLanguages() {
        LinkedHashMap<String, LanguageCode> languageCodes = new LinkedHashMap<>();
        try {
            Scanner sc = new Scanner(new File("data/language-codes.csv"));
            while (sc.hasNextLine()) {
                String[] tokens = sc.nextLine().split(",");
                LanguageCode languageCode = new LanguageCode(tokens[0], tokens[1]);
                languageCodes.put(tokens[1], languageCode);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return languageCodes;
    }

}
