package com.jannetta.glossary.view;

import static javax.swing.GroupLayout.Alignment.CENTER;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.jannetta.glossary.controller.StaticUtils;

class AboutDialog extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public AboutDialog(Frame parent) {
        super(parent);
        initUI();
    }

    private void initUI() {

        ImageIcon icon = StaticUtils.createImageIcon("parrotpaint2.png", "Glosario Logo");
        JLabel imgLabel = new JLabel(icon);

        SimpleAttributeSet sa = new SimpleAttributeSet();
        StyleConstants.setAlignment(sa, StyleConstants.ALIGN_CENTER);
        JTextPane textLabel = new JTextPane();
        textLabel.getStyledDocument().setParagraphAttributes(0, 20, sa, false);
        textLabel.setContentType("text/html");
        textLabel.setEditable(false);
        textLabel.setText("<html><head><style>div {text-align: center;}</style></head><body>"
                + "<div><b>GlosarioEditor2</b><div>" + "<div>Version 1.0</div>"
                + "<div>GlossaryEditor is an editor, written in Java for updating<br/> "
                + "the Carpentries Glosario project YAML file<br/>"
                + "<a href=\"https://github.com/jsteyn/glossario-editor-2\">"
                + "https://github.com/jsteyn/glossario-editor-2</a></div>"
                + "<br/>"
                + "<div><b>Authors:</b> Jannetta S. Steyn && Stuart M. Lewis</div"
                + "<hr/>" 
                + "<div><b>Glossario</b></div>"
                + "<div>Glosario is an Open Source project maintained by the Carpentries Community<br/>"
                + "<a href=\"https://glosario.carpentries.org/\">https://glosario.carpentries.org/</a></div>"
                + "</body></html>");
        textLabel.setFont(new Font("Serif", Font.BOLD, 13));

        JButton okBtn = new JButton("Close");
        okBtn.addActionListener(event -> dispose());

        createLayout(textLabel, imgLabel, okBtn);

        setModalityType(ModalityType.APPLICATION_MODAL);

        setTitle("About GlosarioEditor2");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    private void createLayout(JComponent... arg) {

        Container pane = getContentPane();
        pane.setBackground(new Color(58, 179, 76, 57));
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        gl.setHorizontalGroup(gl.createParallelGroup(CENTER).addComponent(arg[0]).addComponent(arg[1])
                .addComponent(arg[2]).addGap(200));

        gl.setVerticalGroup(gl.createSequentialGroup().addGap(30).addComponent(arg[0]).addGap(20).addComponent(arg[1])
                .addGap(20).addComponent(arg[2]).addGap(30));

        pack();
    }
}