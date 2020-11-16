package com.jannetta.glossary.view;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainFrame extends JFrame {

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

	public MainFrame() {
		super("Glossary");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (UnsupportedLookAndFeelException e) {
		}
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		try {
			Image icon = toolkit.getImage(ClassLoader.getSystemResource("Certify.png"));
			// URL resource = getClass().getClassLoader().getResource("Certify1.png");
			// BufferedImage image = ImageIO.read(resource);
			setIconImage(icon);
		} catch (NullPointerException e) {
			logger.error("Certify.png not found.");
		}
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closer();
				System.exit(0);
			}
		});

		setContentPane(pn_main);
		pack();
		setVisible(true);
		setSize(1024, 768);
	}

	private void closer() {

	}
}
