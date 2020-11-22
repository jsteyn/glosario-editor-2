package com.jannetta.glossary.view;

import java.awt.Color;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import com.jannetta.glossary.controller.FileTypeFilter;
import com.jannetta.glossary.controller.StaticUtils;
import com.jannetta.glossary.controller.YAML;
import com.jannetta.glossary.model.LanguageCode;
import com.jannetta.glossary.model.LanguageEntry;
import com.jannetta.glossary.model.Slug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.miginfocom.swing.MigLayout;

public class MainPanel extends JPanel implements ActionListener, DocumentListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    Logger logger = LoggerFactory.getLogger(getClass());

    private String filename_from = "/home/jannetta/CARPENTRIES/glosario/glossary.yml";
    // private String filename_from =
    // "/home/jannetta/CARPENTRIES/VSC-workspace/glosarioeditor/sampleGlossary.yml";
    private String filename_to = "/home/jannetta/CARPENTRIES/glosario/glossary_bu.yml";
    private JButtonPanel pnl_slugButtons;
    private ArrayList<JButton> buttons;

    private JPanel pnl_fromLanguage = new JPanel();

    private JLabel lbl_fromLanguage = new JLabel("English Entry:");
    private JLabel lbl_fromSlug = new JLabel("Slug");
    private JTextField tf_fromSlug = new JTextField(20);
    private JLabel lbl_fromTerm = new JLabel("Term");
    private JTextField tf_fromTerm = new JTextField(20);
    private JLabel lbl_References = new JLabel("References:");
    private JComboBox<String> cb_references = new JComboBox<>();
    private JTextArea ta_fromDefinition = new JTextArea(100, 80);

    private JPanel pnl_toLanguage = new JPanel();

    private JLabel lbl_language = new JLabel("Select language");
    private JComboBox<String> cb_language = new JComboBox<>();
    private JLabel lbl_toTerm = new JLabel("Term");
    private JTextField tf_toTerm = new JTextField(20);
    private JLabel lbl_toAcronymn = new JLabel("Acronymn");
    private JTextField tf_toAcronymn = new JTextField(20);
    private JTextArea ta_toDefinition = new JTextArea(100, 80);
    private JButton btn_Save = new JButton("Save file");
    private JLabel lbl_Save = new JLabel(filename_to);
    private JButton btn_AddSlug = new JButton("Add Slug");

    private LinkedHashMap<String, LanguageCode> languageCodes;
    private LinkedHashMap<String, Slug> listOfSlugs;
    private JList<String> lst_References;// = new JList<String>(new DefaultListModel<String>());

    public MainPanel() {
        super();

        /**
         * Load the glossary file
         */
        listOfSlugs = YAML.parseYAML(new File(filename_from));
        /**
         * load the languages file
         */
        languageCodes = StaticUtils.loadLanguages();
        languageCodes.forEach((lang, language) -> cb_language.addItem(language.getCode() + ": " + language.getName()));

        MigLayout migLayout = new MigLayout("", "[shrink 0]5[]5[grow]", "[][]");
        setLayout(migLayout);
        pnl_fromLanguage.setLayout(new MigLayout("", "[][grow]"));
        pnl_toLanguage.setLayout(new MigLayout("", "[][grow]"));
        cb_language.addActionListener(this);
        cb_language.setPrototypeDisplayValue("ln: language");
        cb_references.setPrototypeDisplayValue("aaaaaaaaaaaaaaa");
        cb_references.setEditable(true);
        cb_references.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if (ie.getStateChange() == ItemEvent.SELECTED) {
                    if (lst_References == null)
                        lst_References = new JList<String>(new DefaultListModel<String>());
                    ((DefaultListModel<String>) lst_References.getModel())
                            .addElement((String) cb_references.getSelectedItem());
                    cb_references.insertItemAt((String) cb_references.getSelectedItem(), 0);
                    Slug slug = listOfSlugs.get(tf_fromSlug.getText());
                    String addedReferenece = (String) cb_references.getSelectedItem();
                    slug.getReferences().add(addedReferenece);
                    btn_Save.setEnabled(true);
                }
            }
        });
        tf_fromSlug.setEditable(false);
        tf_fromTerm.setEditable(false);
        ta_fromDefinition.setEditable(false);
        ta_fromDefinition.setLineWrap(false);
        JScrollPane sp_fromDefinition = new JScrollPane(ta_fromDefinition);

        tf_toTerm.getDocument().addDocumentListener(this);
        tf_toTerm.getDocument().putProperty("prop", "tf_toTerm");
        tf_toAcronymn.getDocument().addDocumentListener(this);
        tf_toAcronymn.getDocument().putProperty("prop", "tf_toAcronymn");
        ta_toDefinition.getDocument().addDocumentListener(this);
        ta_toDefinition.getDocument().putProperty("prop", "ta_toDefinition");
        ta_toDefinition.setLineWrap(false);
        JScrollPane sp_toDefinition = new JScrollPane(ta_toDefinition);

        btn_Save.addActionListener(this);
        btn_Save.setEnabled(false);
        btn_AddSlug.addActionListener(this);
        btn_AddSlug.setVisible(false);
        btn_AddSlug.setActionCommand("addslug");

        cb_language.setSelectedIndex(0);
        updateForm();

        // Panel SlugButtons
        buttons = StaticUtils.loadSlugButtons(this, filename_from);
        pnl_slugButtons = new JButtonPanel(buttons);
        JScrollPane scrollPane = new JScrollPane(pnl_slugButtons);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(pnl_slugButtons);

        // Panel fromLanguage
        pnl_fromLanguage.setBorder(BorderFactory.createLineBorder(Color.black));
        pnl_fromLanguage.add(lbl_fromSlug);
        pnl_fromLanguage.add(tf_fromSlug);
        pnl_fromLanguage.add(btn_AddSlug, "wrap");
        pnl_fromLanguage.add(lbl_fromLanguage, "span, wrap");
        pnl_fromLanguage.add(lbl_fromTerm);
        pnl_fromLanguage.add(tf_fromTerm, "wrap");
        pnl_fromLanguage.add(lbl_References);
        pnl_fromLanguage.add(cb_references, "wrap");
        pnl_fromLanguage.add(sp_fromDefinition, "span, grow");

        // Panel toLanguage
        pnl_toLanguage.setBorder(BorderFactory.createLineBorder(Color.black));
        pnl_toLanguage.add(lbl_language);
        pnl_toLanguage.add(cb_language, "wrap");
        pnl_toLanguage.add(lbl_toTerm);
        pnl_toLanguage.add(tf_toTerm, "wrap");
        pnl_toLanguage.add(lbl_toAcronymn);
        pnl_toLanguage.add(tf_toAcronymn, "wrap");
        pnl_toLanguage.add(sp_toDefinition, "span, grow");
        pnl_toLanguage.add(btn_Save);
        pnl_toLanguage.add(lbl_Save, "wrap");

        // Holdall for from- and to panels
        JPanel holdAll = new JPanel();
        holdAll.setLayout(new MigLayout());
        holdAll.add(pnl_fromLanguage, "grow, wrap");
        holdAll.add(pnl_toLanguage, "grow, wrap");

        add(scrollPane, "shrink 0");
        add(holdAll, "grow, span, wrap");

    }

    /**
     * Return the ActionListener of this panel
     * 
     * @return this object
     */
    public ActionListener getActionListener() {
        return this;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        logger.debug("Event: " + e.getActionCommand());

        if (e.getActionCommand().equals("comboBoxChanged")) {
            updateForm();
        } else if (e.getActionCommand().equals("Save file")) {
            YAML.write(listOfSlugs, new File(filename_to));
            btn_Save.setEnabled(false);
        } else if (e.getActionCommand().equals("New slug")) {
            StaticUtils.lockDocumentListeners = true;
            tf_fromSlug.setText("");
            tf_fromTerm.setText("");
            ta_fromDefinition.setText("");
            tf_toTerm.setText("");
            tf_toAcronymn.setText("");
            ta_toDefinition.setText("");
            tf_fromSlug.setEditable(true);
            btn_AddSlug.setVisible(true);
            StaticUtils.lockDocumentListeners = false;
        } else if (e.getActionCommand().equals("addslug")) {
            Slug newSlug = new Slug(tf_fromSlug.getText());
            listOfSlugs.put(tf_fromSlug.getText(), newSlug);
            buttons = StaticUtils.addSlugButton(this, buttons, tf_fromSlug.getText());
            pnl_slugButtons.fireUpdate();
            updateUI();
            tf_fromSlug.setEditable(false);
        } else if (e.getActionCommand().equals("Open")) {
            final JFileChooser fc = new JFileChooser();
            FileFilter pdfFilter = new FileTypeFilter(".yml", "Comma separated value file");
            fc.addChoosableFileFilter(pdfFilter);
            // fc.setFileFilter(docF);
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                filename_from = file.getAbsolutePath();
                filename_to = filename_from.substring(0, filename_from.length() - 4) + "_bu.yml";
                logger.debug(filename_from + "\t" + filename_to);
                // Read slugs
                listOfSlugs = YAML.parseYAML(new File(filename_from));
                // Generate buttons for slugs
                buttons = StaticUtils.loadSlugButtons(this, filename_from);
                // Load buttons into panel
                pnl_slugButtons.setButtons(buttons);
                pnl_slugButtons.fireUpdate();
                lbl_Save.setText(filename_to);

                this.updateUI();
            }

        } else if (e.getActionCommand().startsWith("btn_")) {
            logger.debug("Pressed " + e.getActionCommand());
            // Slug button pressed
            Slug slug = listOfSlugs.get(e.getActionCommand().substring(4));
            String[] references = {};
            if (Arrays.copyOf(slug.getReferences().toArray(), slug.getReferences().size(), String[].class) != null)
                references = Arrays.copyOf(slug.getReferences().toArray(), slug.getReferences().size(), String[].class);
            StaticUtils.lockDocumentListeners = true;
            tf_fromSlug.setText(slug.getSlug());
            // Default language to translate from is en (English)
            if (slug.getLanguageEntries().get("en") != null) {
                if (slug.getLanguageEntries().get("en").getTerm() != null) {
                    tf_fromTerm.setText(slug.getLanguageEntries().get("en").getTerm());
                    cb_references.setModel(new DefaultComboBoxModel<String>(references));
                    ta_fromDefinition.setText(slug.getLanguageEntries().get("en").getDefinition());
                }
            } else {
                tf_fromTerm.setText("");
                ta_fromDefinition.setText("");
            }
            StaticUtils.lockDocumentListeners = false;
            updateForm();
        }
    }

    private void updateForm() {
        String language = ((String) cb_language.getSelectedItem()).substring(4);
        if (!tf_fromSlug.getText().equals("")) {
            String slugname = tf_fromSlug.getText();
            Slug slug = listOfSlugs.get(slugname);
            String langcode = languageCodes.get(language).getCode();
            LanguageEntry languageEntry = slug.getLanguageEntries().get(langcode);
            StaticUtils.lockDocumentListeners = true;
            if (languageEntry != null) {
                logger.debug("Get definition in " + language);
                tf_toTerm.setText(languageEntry.getTerm());
                tf_toAcronymn.setText(languageEntry.getAcronym());
                ta_toDefinition.setText(languageEntry.getDefinition());
            } else {
                tf_toTerm.setText("");
                tf_toAcronymn.setText("");
                ta_toDefinition.setText("");
            }
            StaticUtils.lockDocumentListeners = false;
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        update(e);

    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        update(e);

    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }

    public void update(DocumentEvent e) {

        if (e.getDocument().getProperty("prop").equals("tf_toTerm")) {
            logger.debug("tf_toTerm");
        }
        if (e.getDocument().getProperty("prop").equals("tf_toAcronymn")) {
            logger.debug("tf_toAcronymn");
        }
        if (e.getDocument().getProperty("prop").equals("ta_toDefinition")) {
            logger.debug("ta_toDefinition");
        }
        if (!StaticUtils.lockDocumentListeners) {
            String language = ((String) cb_language.getSelectedItem()).substring(4);
            String slugname = tf_fromSlug.getText();
            Slug slug = listOfSlugs.get(slugname);
            String langcode = languageCodes.get(language).getCode();
            logger.debug("update: " + language + "\t" + langcode);
            if (slug != null) {
                LanguageEntry languageEntry = slug.getLanguageEntries().get(langcode);
                if (languageEntry == null) {
                    logger.debug("update: " + "Langcode empty");
                    languageEntry = new LanguageEntry();

                }
                languageEntry.setLanguage(langcode);
                languageEntry.setTerm(tf_toTerm.getText());
                if (!tf_toAcronymn.getText().equals(""))
                    languageEntry.setAcronym(tf_toAcronymn.getText());
                languageEntry.setDefinition(ta_toDefinition.getText());
                slug.getLanguageEntries().put(langcode, languageEntry);
                btn_Save.setEnabled(true);
            }
        }

    }

    public boolean allSaved() {
        return !btn_Save.isEnabled();
    }

}
