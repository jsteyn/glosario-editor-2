package com.jannetta.glossary.controller;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.JButton;

import com.jannetta.glossary.model.LanguageCode;

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

   /**
     * Load properties from system.properties file
     */
    public static Properties loadProperties() {
        Properties properties = new Properties();
        try {
            File f = new File("system.properties");
            if (!(f.exists())) {
                OutputStream out = new FileOutputStream(f);
                out.close();
            }
            InputStream is = new FileInputStream(f);
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * Set the specified property with the specified value
     * @param property The property to set
     * @param value The value to set the property to
     */
    public static void saveProperties(Properties properties) {
        File f = new File("system.properties");
        try {
            OutputStream out = new FileOutputStream(f);
            properties.store(out, "");
            out.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
