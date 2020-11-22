package com.jannetta.glossary.view;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.miginfocom.swing.MigLayout;

public class MainFrame extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Naming convention: pn - pane lbl - lable btn - button
	 * 
	 */
	private Logger logger = LoggerFactory.getLogger(getClass());
	private MainPanel pn_main = new MainPanel();
	JMenuBar menuBar = new JMenuBar();
	JMenu filemenu = new JMenu("File");
	JMenu actionsmenu = new JMenu("Actions");
	JMenuItem file_open = new JMenuItem("Open");
	JMenuItem actions_add = new JMenuItem("New slug");


	public MainFrame() {
		super("Glossary");

		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		try {
			Image icon = toolkit.getImage(ClassLoader.getSystemResource("parrot.png"));

			setIconImage(icon);
		} catch (NullPointerException e) {
			logger.error("parrot.png not found.");
		}
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (closer())
					System.exit(0);
				else {
					int result2 = JOptionPane.showConfirmDialog(pn_main, "You have made changes, would you like to save before quitting?",
							"Exit GlosarioEditor?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (result2 == JOptionPane.YES_OPTION) {
						logger.trace("Save lessons file");

					} else if (result2 == JOptionPane.NO_OPTION) {
						logger.trace("Quit without saving lessons");
					} else {
						logger.trace("Cancel quitting");
					}

				}
			}
		});
		file_open.addActionListener(pn_main.getActionListener());
		actions_add.addActionListener(pn_main.getActionListener());
		menuBar.add(filemenu);
		filemenu.add(file_open);
		actionsmenu.add(actions_add);
		menuBar.add(actionsmenu);
		setJMenuBar(menuBar);
		setLayout(new MigLayout("", "[]", "[]"));
		setContentPane(pn_main);
		pack();
		setVisible(true);
		setSize(1024, 768);
	}

	private boolean closer() {
		return pn_main.allSaved();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}
}