package com.jannetta.glossary.view;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

public class JButtonPanel extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    ArrayList<JButton> buttons;

    JButtonPanel() {
        super();
        setLayout(new MigLayout("fill"));
    }

    JButtonPanel(ArrayList<JButton> buttons) {
        super();
        setLayout(new MigLayout("fill"));
        this.buttons = buttons;
        setBorder(new EmptyBorder(0, 0, 0, 10));
        fireUpdate();
    }

    public void fireUpdate() {
        this.removeAll();
        buttons.forEach((button) -> {
            add(button, "grow, wrap");
        });
        updateUI();
    }

    public ArrayList<JButton> getButtons() {
        return buttons;
    }

    public void setButtons(ArrayList<JButton> buttons) {
        this.buttons = buttons;
        fireUpdate();
    }

    

}
