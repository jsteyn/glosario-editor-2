package com.jannetta.glossary.view;

import java.awt.Color;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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

    private String lastdir = ".";
    private String filename_from = "glossary.yml";
    private String filename_to = filename_from;
    private JButtonPanel pnl_slugButtons;
    private ArrayList<JButton> buttons;

    private JPanel pnl_fromLanguage = new JPanel();

    private JLabel lbl_fromLanguage = new JLabel("Language: en");
    private JLabel lbl_fromSlug = new JLabel("Slug");
    private JTextField tf_fromSlug = new JTextField(20);
    private JLabel lbl_fromTerm = new JLabel("Term");
    private JTextField tf_fromTerm = new JTextField(20);
    private JLabel lbl_References = new JLabel("References:");
    private JComboBox<String> cb_references = new JComboBox<>();
    private JTextField tf_newReference = new JTextField(15);
    private JButton btn_AddReference = new JButton("Add Reference");
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
    private JScrollPane scrollPane;

    private LinkedHashMap<String, LanguageCode> languageCodes;
    private LinkedHashMap<String, Slug> listOfSlugs;

    private Properties properties = new Properties();

    public MainPanel() {
        super();
        /**
         * Load defaults from system.properties
         */
        properties = StaticUtils.loadProperties();
        if (!(properties.getProperty("lastdir") == null))
            lastdir = properties.getProperty("lastdir");
        else
            properties.setProperty("lastdir", lastdir);
        String fn = properties.getProperty("filename_from");
        if (!(fn == null)) {
            filename_from = fn;
        }

        /**
         * Load the glossary file
         */
        File f = new File(lastdir + "/" + filename_from);
        if (!(f.exists())) {

            try {
                InputStream in = getClass().getResourceAsStream("/sampleGlossaryDefault.yml");
                byte[] buffer = new byte[in.available()];
                in.read(buffer);
                File targetFile = new File(lastdir + "/" + filename_from);
                OutputStream outStream = new FileOutputStream(targetFile);
                outStream.write(buffer);
                outStream.close();
            } catch (NullPointerException e) {
                logger.error("file not found.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        listOfSlugs = YAML.parseYAML(new File(lastdir + "/" + filename_from));

        /**
         * Load the slug buttons
         */
        buttons = StaticUtils.loadSlugButtons(this, lastdir + "/" + filename_from);

        /**
         * load the languages file
         */
        InputStream languagefile = getClass().getResourceAsStream("/language-codes.csv");
        languageCodes = StaticUtils.loadLanguages(languagefile);
        languageCodes.forEach((lang, language) -> cb_language.addItem(language.getCode() + ": " + language.getName()));

        MigLayout migLayout = new MigLayout("", "[shrink 0]5[]5[grow]", "[][]");
        setLayout(migLayout);
        pnl_fromLanguage.setLayout(new MigLayout("", "[][][grow]"));
        pnl_toLanguage.setLayout(new MigLayout("", "[][grow]"));
        cb_language.addActionListener(this);
        cb_language.setPrototypeDisplayValue("ln: language");
        cb_references.setPrototypeDisplayValue("aaaaaaaaaaaaaaa");
        cb_references.setEditable(false);
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

        tf_newReference.setEditable(false);
        btn_AddReference.setEnabled(false);
        btn_Save.addActionListener(this);
        btn_Save.setEnabled(false);
        btn_AddSlug.addActionListener(this);
        btn_AddSlug.setVisible(false);
        btn_AddSlug.setActionCommand("addslug");
        btn_AddReference.addActionListener(this);

        cb_language.setSelectedIndex(0);
        updateForm();

        // Panel slugButtons
        pnl_slugButtons = new JButtonPanel(buttons);
        scrollPane = new JScrollPane(pnl_slugButtons);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(pnl_slugButtons);

        // Panel fromLanguage
        pnl_fromLanguage.setBorder(BorderFactory.createLineBorder(Color.black));
        pnl_fromLanguage.add(lbl_fromSlug);
        pnl_fromLanguage.add(tf_fromSlug);
        pnl_fromLanguage.add(btn_AddSlug, "wrap");
        pnl_fromLanguage.add(new JLabel("New Reference"));
        pnl_fromLanguage.add(tf_newReference);
        pnl_fromLanguage.add(btn_AddReference, "wrap");
        pnl_fromLanguage.add(lbl_References);
        pnl_fromLanguage.add(cb_references, "wrap");
        pnl_fromLanguage.add(lbl_fromLanguage, "span, wrap");
        pnl_fromLanguage.add(lbl_fromTerm);
        pnl_fromLanguage.add(tf_fromTerm, "wrap");
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

        if (e.getActionCommand().equals("About")) {
            ImageIcon icon = StaticUtils.createImageIcon("parrot.png", "Glosario Logo");
            JOptionPane.showMessageDialog(this,
            "GlossaryEditor is an editor, written in Java for updating.\n"
            + "the Carpentries Glosario project YAML file.\n\n"
            + "GlosarioEditor: "
            + "<a href=\"https://github.com/jsteyn/glossario-editor-2>"
            + "https://github.com/jsteyn/glossario-editor-2</a>\n\n"
            + "Glosario is an Open Source project maintained by the Carpentries Community\n"
            + "Glosario: https://glosario.carpentries.org/\n\n"
            + "Version 1.0 (2020-11-27)\n" + "Copyright: Jannetta S Steyn, 2020",
            "About GlosarioEditor", JOptionPane.PLAIN_MESSAGE, icon);

        }
        if (e.getActionCommand().equals("comboBoxChanged")) {
            logger.debug("Source: " + e.getSource().toString());
            updateForm();

        } else if (e.getActionCommand().equals("Save file")) {
            YAML.write(listOfSlugs, new File(lastdir + "/" + filename_to));
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
            StaticUtils.lockDocumentListeners = true;
            if (!tf_fromSlug.getText().strip().isBlank()) {
                String newslug = tf_fromSlug.getText().strip();
                if (listOfSlugs.get(newslug) == null) {
                    Slug newSlug = new Slug(tf_fromSlug.getText().strip());
                    listOfSlugs.put(tf_fromSlug.getText(), newSlug);
                    buttons = StaticUtils.addSlugButton(this, buttons, tf_fromSlug.getText());
                    pnl_slugButtons.fireUpdate();
                    pnl_slugButtons.getComponent(pnl_slugButtons.getComponentCount() - 1).requestFocus();
                    scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum() + 1);
                    btn_Save.setEnabled(true);
                    btn_AddSlug.setVisible(false);
                    updateUI();
                    tf_fromSlug.setEditable(false);
                    tf_fromSlug.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "This slug already exists.", "Duplicate Slug",
                            JOptionPane.ERROR_MESSAGE);
                    loadSlug(newslug);
                    btn_AddSlug.setVisible(false);
                    tf_fromSlug.setText("");
                    tf_fromSlug.setEditable(false);
                }
            } else
                tf_fromSlug.setText("");
            StaticUtils.lockDocumentListeners = false;

            // OPEN glossary.yml FILE
        } else if (e.getActionCommand().equals("Open")) {
            lastdir = properties.getProperty("lastdir");
            final JFileChooser fc = new JFileChooser(lastdir);
            FileFilter ymlFilter = new FileTypeFilter(".yml", "YAML file");
            fc.setFileFilter(ymlFilter);
            // fc.setFileFilter(docF);
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                lastdir = file.getParent();
                filename_from = file.getName();
                filename_to = filename_from.substring(0, filename_from.length() - 4);
                logger.debug(lastdir + "/" + filename_from + "\t" + lastdir + "/" + filename_to);
                // Read slugs
                listOfSlugs = YAML.parseYAML(new File(lastdir + "/" + filename_from));
                // Generate buttons for slugs
                buttons = StaticUtils.loadSlugButtons(this, lastdir + "/" + filename_from);
                // Load buttons into panel
                pnl_slugButtons.setButtons(buttons);
                pnl_slugButtons.fireUpdate();
                lbl_Save.setText(lastdir + "/" + filename_to);
                properties.setProperty("lastdir", file.getParent());
                properties.setProperty("filename_from", filename_from);
                pnl_slugButtons.getComponent(0).requestFocus();
                tf_fromSlug.setText(((JButton) pnl_slugButtons.getComponent(0)).getText());
                updateForm();
                StaticUtils.saveProperties(properties);

                this.updateUI();
            }

        } else if (e.getActionCommand().equals("Add Reference")) {
            logger.debug("New reference");
            StaticUtils.lockDocumentListeners = true;
            if (!tf_newReference.getText().isBlank()) {
                String newReference = tf_newReference.getText().strip();
                Slug slug = listOfSlugs.get(tf_fromSlug.getText());
                if (!slug.getReferences().contains(newReference)) {
                    slug.getReferences().contains(tf_newReference.getText().strip());
                    cb_references.addItem(newReference);
                    tf_newReference.setText("");
                    cb_references.showPopup();
                    slug.getReferences().add(newReference);
                } else {
                    JOptionPane.showMessageDialog(this, "This reference already exists.", "Duplicate Reference",
                            JOptionPane.ERROR_MESSAGE);
                    tf_newReference.setText("");
                }
                btn_Save.setEnabled(true);
            } else
                tf_newReference.setText("");

            StaticUtils.lockDocumentListeners = false;

            // SELECT SLUG
        } else if (e.getActionCommand().startsWith("btn_")) {
            btn_AddSlug.setVisible(false);
            loadSlug(e.getActionCommand().substring(4));
        }
    }

    /**
     * Load the selected slug record into the form
     */
    private void loadSlug(String slugkey) {
        // ENABLE ABILITY TO ADD A NEW REFERENCE
        tf_newReference.setEditable(true);
        btn_AddReference.setEnabled(true);

        // LOAD THE SLUG
        Slug slug = listOfSlugs.get(slugkey);
        StaticUtils.lockDocumentListeners = true;
        tf_fromSlug.setText(slug.getSlug());
        StaticUtils.lockDocumentListeners = false;
        updateForm();

    }

    /**
     * Update the form displayed on the screen
     */
    private void updateForm() {
        String language = ((String) cb_language.getSelectedItem()).substring(4);
        // CLEAR ALL FORM FIELDS
        StaticUtils.lockDocumentListeners = true;
        tf_fromTerm.setText("");
        ta_fromDefinition.setText("");
        tf_toAcronymn.setText("");
        tf_toTerm.setText("");
        ta_toDefinition.setText("");
        // NOW SET ALL FORM FIELDS
        if (!tf_fromSlug.getText().equals("")) {
            String slugname = tf_fromSlug.getText();
            Slug slug = listOfSlugs.get(slugname);
            String[] references = Arrays.copyOf(slug.getReferences().toArray(), slug.getReferences().size(),
                    String[].class);
            cb_references.removeAllItems();
            cb_references.setModel(new DefaultComboBoxModel<String>(references));

            // SET FROM FIELDS

            LanguageEntry defaultLanguageEntry = null;
            String defaultLanguage = null;
            // SLUG SHOULD NOT BE EMPTY
            if (!slug.getLanguageEntries().isEmpty()) {
                // IF AN ENGLISH SLUG EXISTS SET THE DEFAULT LANGUAGE TO ENGLISH
                if (slug.getLanguageEntries().get("en") != null) {
                    defaultLanguageEntry = slug.getLanguageEntries().get("en");
                    defaultLanguage = "en";
                    // IF AN ENGLISH SLUG DOES NOT EXIST GET THE FIRST ONE THAT DOES EXIST
                } else {
                    Set<String> keys = slug.getLanguageEntries().keySet();
                    String key = (String) keys.toArray()[0];
                    defaultLanguageEntry = slug.getLanguageEntries().get(key);
                    defaultLanguage = key;
                }
                logger.debug("Default languageEntry key: " + defaultLanguage);
                lbl_fromLanguage.setText("Language: " + defaultLanguage);

                if (defaultLanguageEntry != null) {
                    if (slug.getLanguageEntries().get(defaultLanguage).getTerm() != null) {
                        tf_fromTerm.setText(slug.getLanguageEntries().get(defaultLanguage).getTerm());
                        ta_fromDefinition.setText(slug.getLanguageEntries().get(defaultLanguage).getDefinition());
                    }
                }

                logger.debug(defaultLanguageEntry.getLanguage());
                tf_fromTerm.setText(defaultLanguageEntry.getTerm());
                ta_fromDefinition.setText(defaultLanguageEntry.getDefinition());

                // SET TO FIELDS
                String langcode = languageCodes.get(language).getCode();
                LanguageEntry languageEntry = slug.getLanguageEntries().get(langcode);
                StaticUtils.lockDocumentListeners = true;
                if (languageEntry != null) {
                    logger.debug("Get definition in " + language);
                    tf_toTerm.setText(languageEntry.getTerm());
                    tf_toAcronymn.setText(languageEntry.getAcronym());
                    ta_toDefinition.setText(languageEntry.getDefinition());
                    logger.debug("Definition: " + languageEntry.getDefinition());
                }
                StaticUtils.lockDocumentListeners = false;
            } else {
                StaticUtils.lockDocumentListeners = true;
                tf_fromTerm.setText("");
                ta_fromDefinition.setText("");
                tf_toTerm.setText("");
                tf_toAcronymn.setText("");
                ta_toDefinition.setText("");
                StaticUtils.lockDocumentListeners = false;
            }
        }
        StaticUtils.lockDocumentListeners = false;
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
            if (!(ta_toDefinition.getText().strip().equals(""))) {
                if (tf_toTerm.getText().strip().equals("")) {
                    JOptionPane.showMessageDialog(this, "First enter a term before adding a description",
                            "Missing Term", JOptionPane.INFORMATION_MESSAGE);
                }
            }
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

    /**
     * Returns true if the save button is not enabled and false if it is enabled. An
     * enabled save button implies that changes have been made that have not been
     * saved.
     * 
     * @return
     */
    public boolean allSaved() {
        return !btn_Save.isEnabled();
    }

    public void save() {
        YAML.write(listOfSlugs, new File(lastdir + "/" + filename_to));
    }

    public LinkedHashMap<String, Slug> getListOfSlugs() {
        return listOfSlugs;
    }

    public void setListOfSlugs(LinkedHashMap<String, Slug> listOfSlugs) {
        this.listOfSlugs = listOfSlugs;
    }


}
