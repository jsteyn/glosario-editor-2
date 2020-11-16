package com.jannetta.glossary.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.jannetta.glossary.model.LanguageCode;
import com.jannetta.glossary.model.LanguageEntry;
import com.jannetta.glossary.model.Slug;

import net.miginfocom.swing.MigLayout;

public class MainPanel extends JPanel implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private JPanel pnl_slugButtons;
    private ArrayList<JButton> buttons;

    private JPanel pnl_fromLanguage = new JPanel();
    private JLabel lbl_fromLanguage = new JLabel("English Entry:");
    private JLabel lbl_fromSlug = new JLabel("Slug");
    private JTextField tf_fromSlug = new JTextField(30);
    private JLabel lbl_fromReferences = new JLabel("References:");
    private JComboBox<String> cb_references = new JComboBox<>();
    private JTextArea ta_fromText = new JTextArea(10, 50);

    private JLabel lbl_language = new JLabel("Select language");
    private JComboBox<String> cb_language = new JComboBox<>();
    private JLabel lbl_slug = new JLabel("Slug");
    private JTextField tf_slug = new JTextField(20);
    private ArrayList<LanguageCode> languageCodes;
    private LinkedHashMap<String, Slug> listOfSlugs;

    public MainPanel() {
        super();
        /**
         * Layout: +---------------------+------------- | Slug buttons | English entry |
         * +----------------------------- | | Select language combobox | | Slug | |
         * References | +--------------- | | Term | | Acronymn | | Definition
         */

        /**
         * Load the glossary file
         */
        listOfSlugs = parseYAML(new File("data/glossary.yml"));
        write(listOfSlugs, new File("data/new_glossary.yaml"));

        MigLayout migLayout = new MigLayout("fillx", "[]rel[]rel[]", "[]10[]10[]");
        setLayout(migLayout);
        languageCodes = loadLanguages();
        languageCodes.forEach((language) -> cb_language.addItem(language.getName()));

        // Panel SlugButtons
        buttons = loadSlugButtons(this);
        pnl_slugButtons = addSlugButtons(buttons);
        JScrollPane scrollPane = new JScrollPane(pnl_slugButtons);

        // Panel fromLanguage
        pnl_fromLanguage.setLayout(new MigLayout("fillx", "[]rel[]", "[]10[]"));
        pnl_fromLanguage.add(lbl_fromLanguage, "wrap");
        pnl_fromLanguage.add(lbl_fromSlug);
        pnl_fromLanguage.add(tf_fromSlug, "wrap");
        pnl_fromLanguage.add(lbl_fromReferences);
        pnl_fromLanguage.add(cb_references, "wrap");
        ta_fromText.setLineWrap(false);
        ta_fromText.setRows(10);
        JScrollPane sp_textArea = new JScrollPane(ta_fromText);
        pnl_fromLanguage.add(sp_textArea, "span 2, wrap");
        pnl_fromLanguage.add(new JSeparator(), "span, wrap");
        pnl_fromLanguage.add(lbl_language);
        pnl_fromLanguage.add(cb_language, "wrap");

        add(scrollPane, "span 1 6");
        add(pnl_fromLanguage, "wrap");

    }

    private ArrayList<JButton> loadSlugButtons(ActionListener actionListener) {
        ArrayList<JButton> buttons = new ArrayList<>();
        try {
            Scanner sc = new Scanner(new File("data/glossary.yml"));
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.startsWith("-")) {
                    String[] tokens = line.split(":");
                    JButton button = new JButton(tokens[1].strip());
                    buttons.add(button);
                    button.addActionListener(actionListener);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return buttons;
    }

    private JPanel addSlugButtons(ArrayList<JButton> buttons) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new MigLayout("fillx", "[fill]"));
        buttons.forEach((button) -> {
            buttonPanel.add(button, "wrap");
        });
        return buttonPanel;
    }

    private ArrayList<LanguageCode> loadLanguages() {
        ArrayList<LanguageCode> languageCodes = new ArrayList<>();
        try {
            Scanner sc = new Scanner(new File("data/language-codes.csv"));
            while (sc.hasNextLine()) {
                String[] tokens = sc.nextLine().split(",");
                LanguageCode languageCode = new LanguageCode(tokens[0], tokens[1]);
                languageCodes.add(languageCode);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return languageCodes;
    }

    private void write(LinkedHashMap<String, Slug> listOfSlugs, File filename) {
        try {
            PrintWriter pw = new PrintWriter(filename);
            Set<String> set = listOfSlugs.keySet();
            Iterator<String> s = set.iterator();
            while (s.hasNext()) {
                Slug slug = listOfSlugs.get(s.next());
                pw.println("- slug: " + slug.getSlug());
                ArrayList<String> references = slug.getReferences();
                if (references.size() > 0) {
                    pw.println("  ref:");
                    for (int r = 0; r < references.size(); r++) {
                        pw.println("    - " + references.get(r));
                    }
                }
                LinkedHashMap<String, LanguageEntry> languages = slug.getLanguageEntries();
                if (languages.size() > 0) {
                    Set<String> set_languages = languages.keySet();
                    Iterator<String> i = set_languages.iterator();
                    while (i.hasNext()) {
                        LanguageEntry languageEntry = languages.get(i.next());
                        pw.println("  " + languageEntry.getLanguage() + ": ");
                        pw.println("    term: \"" + languageEntry.getTerm() + "\"");
                        pw.println("    def: >");
                        pw.print(languageEntry.getDefinition());
                    }
                }
                pw.println();
            }
            pw.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private LinkedHashMap<String, Slug> parseYAML(File filename) {
        LinkedHashMap<String, Slug> listOfSlugs = new LinkedHashMap<>();
        boolean EOF = false;
        try {
            Scanner sc = new Scanner(filename);
            Slug slug = null;
            String line = "";
            if (sc.hasNextLine())
                do {
                    if (sc.hasNextLine())
                        line = sc.nextLine();
                    else
                        EOF = true;
                    // If slug found
                    if (line.startsWith("- slug:")) {
                        String slugname = line.split(":")[1].strip();
                        slug = new Slug(slugname);
                        System.out.println("- slug:" + slug.getSlug());
                        listOfSlugs.put(slugname, slug);
                    } else System.out.println("X1: " + line);
                    if (line.startsWith("  ref:")) {
                        // If references found
                        System.out.println("  ref:");
                        ArrayList<String> references = slug.getReferences();
                        if (sc.hasNextLine())
                            line = sc.nextLine();
                        else
                            EOF = true;
                        while (line.startsWith("    -")) {
                            String reference = line.strip().split(" ")[1].strip();
                            references.add(reference);
                            System.out.println("    - " + reference);
                            if (sc.hasNextLine())
                                line = sc.nextLine();
                            else
                                EOF = true;
                        }
                    } else System.out.println("X2: " + line);
                    // Language line
                    if (line.matches("^  [a-z][a-z]:")) {
                        LinkedHashMap<String, LanguageEntry> languageEntries = slug.getLanguageEntries();
                        LanguageEntry languageEntry = new LanguageEntry();
                        String language = line.replace(':', ' ').strip();
                        languageEntry.setLanguage(language);
                        languageEntries.put(language, languageEntry);
                        System.out.println("  " + languageEntries.get(language).getLanguage() + ": ");
                        // if (sc.hasNextLine())
                        //     line = sc.nextLine();
                        // else
                        //     EOF = true;
                        // Get term
                        if (line.startsWith("    term:")) {
                            String term = line.split(":")[1];
                            term = term.strip();
                            term = stripQuotes(term);
                            languageEntry.setTerm(term);
                            System.out.println("    term: " + languageEntry.getTerm());
                            if (sc.hasNextLine())
                                line = sc.nextLine();
                            else
                                EOF = true;

                        } else System.out.println("X3: " + line);
                        // Get def line
                        if (line.startsWith("    def")) {
                            // Read the def text
                            System.out.println(line);
                            StringBuilder definition = new StringBuilder();
                            if (sc.hasNextLine())
                                line = sc.nextLine();
                            else
                                EOF = true;
                            while (line.startsWith("      ")) {
                                definition.append(line + "\n");
                                if (sc.hasNextLine())
                                    line = sc.nextLine();
                                else
                                    EOF = true;
                            }
                            languageEntry.setDefinition(definition.toString());
                            System.out.println(languageEntry.getDefinition());

                        } else System.out.println("X4: " + line);

                    }  else System.out.println("X5: " + line);
                } while (!EOF);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return listOfSlugs;
    }

    private String stripQuotes(String term) {
        if (term.startsWith("\""))
            term = term.substring(1);
        if (term.endsWith("\""))
            term = term.substring(0, term.length() - 1);
        return term;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Slug slug = listOfSlugs.get(e.getActionCommand());
        String[] references = {};
        if (Arrays.copyOf(slug.getReferences().toArray(), slug.getReferences().size(), String[].class) != null)
            references = Arrays.copyOf(slug.getReferences().toArray(), slug.getReferences().size(), String[].class);
        tf_fromSlug.setText(slug.getSlug());
        cb_references.setModel(new DefaultComboBoxModel<String>(references));
        ta_fromText.setText(slug.getLanguageEntries().get("en").getDefinition());
        System.out.println(slug.getLanguageEntries().get("en").getDefinition());

    }

}
