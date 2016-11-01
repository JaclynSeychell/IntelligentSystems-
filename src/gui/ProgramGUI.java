package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import ontologies.SupplierVocabulary;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class ProgramGUI extends JFrame implements SupplierVocabulary {
	private static ProgramGUI singleton = new ProgramGUI();
	
	public static ProgramGUI getInstance() {
		return singleton;
	}
	
	private ProgramGUI() {
		// HEADER
		JPanel pHeader = new JPanel();
		getContentPane().add(pHeader, BorderLayout.NORTH);
		pHeader.setLayout(new BoxLayout(pHeader, BoxLayout.Y_AXIS));
		
		JLabel lTitle = new JLabel("Home Energy Trading System");
		lTitle.setFont(new Font("Calibri", Font.BOLD, 32));
		lTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		pHeader.add(lTitle);
		
		JLabel lNames = new JLabel("Designed by James Martin, Nathan Harris & Jacyln Seychell");
		lNames.setAlignmentX(Component.CENTER_ALIGNMENT);
		lNames.setFont(new Font("Calibri", Font.PLAIN, 24));
		pHeader.add(lNames);
		
		JSeparator separator = new JSeparator();
		pHeader.add(separator);
		
		// BODY
		JPanel pBody = new JPanel();
		getContentPane().add(pBody, BorderLayout.CENTER);
		GridBagLayout gbl_pBody = new GridBagLayout();
		gbl_pBody.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gbl_pBody.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_pBody.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_pBody.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		pBody.setLayout(gbl_pBody);
		
		JLabel lAgents = new JLabel("Agents");
		lAgents.setFont(new Font("Calibri", Font.BOLD, 24));
		GridBagConstraints gbc_lAgents = new GridBagConstraints();
		gbc_lAgents.weightx = 75.0;
		gbc_lAgents.insets = new Insets(0, 0, 5, 5);
		gbc_lAgents.gridx = 1;
		gbc_lAgents.gridy = 1;
		pBody.add(lAgents, gbc_lAgents);
		
		JLabel lMessages = new JLabel("Global Messages");
		lMessages.setFont(new Font("Calibri", Font.BOLD, 24));
		GridBagConstraints gbc_lMessages = new GridBagConstraints();
		gbc_lMessages.insets = new Insets(0, 0, 5, 5);
		gbc_lMessages.weightx = 25.0;
		gbc_lMessages.gridx = 3;
		gbc_lMessages.gridy = 1;
		pBody.add(lMessages, gbc_lMessages);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridheight = 2;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 2;
		pBody.add(panel, gbc_panel);
		
		JTextArea textArea = new JTextArea("Test");
		textArea.setEnabled(false);
		
		JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 3;
		gbc_scrollPane.gridy = 2;
		pBody.add(scrollPane, gbc_scrollPane);
		
		JButton btnClear = new JButton("Clear");
		GridBagConstraints gbc_btnClear = new GridBagConstraints();
		gbc_btnClear.fill = GridBagConstraints.BOTH;
		gbc_btnClear.insets = new Insets(0, 0, 5, 5);
		gbc_btnClear.gridx = 3;
		gbc_btnClear.gridy = 3;
		pBody.add(btnClear, gbc_btnClear);
		
		setSize(1920, 1080);
	}
	
	public static void printToLog() {
		
	}
}
