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
		
		JPanel pAgents = new JPanel();
		pAgents.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
		GridBagConstraints gbc_pAgents = new GridBagConstraints();
		gbc_pAgents.gridheight = 2;
		gbc_pAgents.insets = new Insets(0, 0, 5, 5);
		gbc_pAgents.fill = GridBagConstraints.BOTH;
		gbc_pAgents.gridx = 1;
		gbc_pAgents.gridy = 2;
		pBody.add(pAgents, gbc_pAgents);
		GridBagLayout gbl_pAgents = new GridBagLayout();
		gbl_pAgents.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_pAgents.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_pAgents.columnWeights = new double[]{1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_pAgents.rowWeights = new double[]{1.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		pAgents.setLayout(gbl_pAgents);
		
		JPanel pAppliance1 = new JPanel();
		pAppliance1.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_pAppliance1 = new GridBagConstraints();
		gbc_pAppliance1.insets = new Insets(0, 0, 5, 5);
		gbc_pAppliance1.weighty = 25.0;
		gbc_pAppliance1.weightx = 20.0;
		gbc_pAppliance1.fill = GridBagConstraints.BOTH;
		gbc_pAppliance1.gridx = 1;
		gbc_pAppliance1.gridy = 1;
		pAgents.add(pAppliance1, gbc_pAppliance1);
		
		JPanel pAppliance2 = new JPanel();
		pAppliance2.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_pAppliance2 = new GridBagConstraints();
		gbc_pAppliance2.weighty = 25.0;
		gbc_pAppliance2.weightx = 20.0;
		gbc_pAppliance2.insets = new Insets(0, 0, 5, 5);
		gbc_pAppliance2.fill = GridBagConstraints.BOTH;
		gbc_pAppliance2.gridx = 1;
		gbc_pAppliance2.gridy = 3;
		pAgents.add(pAppliance2, gbc_pAppliance2);
		
		JPanel pAppliance3 = new JPanel();
		pAppliance3.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_pAppliance3 = new GridBagConstraints();
		gbc_pAppliance3.weighty = 25.0;
		gbc_pAppliance3.weightx = 20.0;
		gbc_pAppliance3.insets = new Insets(0, 0, 5, 5);
		gbc_pAppliance3.fill = GridBagConstraints.BOTH;
		gbc_pAppliance3.gridx = 1;
		gbc_pAppliance3.gridy = 5;
		pAgents.add(pAppliance3, gbc_pAppliance3);
		
		JPanel pHome = new JPanel();
		pHome.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_pHome = new GridBagConstraints();
		gbc_pHome.weighty = 25.0;
		gbc_pHome.weightx = 20.0;
		gbc_pHome.insets = new Insets(0, 0, 5, 5);
		gbc_pHome.fill = GridBagConstraints.BOTH;
		gbc_pHome.gridx = 3;
		gbc_pHome.gridy = 3;
		pAgents.add(pHome, gbc_pHome);
		
		JPanel pBroker = new JPanel();
		pBroker.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_pBroker = new GridBagConstraints();
		gbc_pBroker.weighty = 25.0;
		gbc_pBroker.weightx = 20.0;
		gbc_pBroker.insets = new Insets(0, 0, 5, 5);
		gbc_pBroker.fill = GridBagConstraints.BOTH;
		gbc_pBroker.gridx = 5;
		gbc_pBroker.gridy = 3;
		pAgents.add(pBroker, gbc_pBroker);
		
		JPanel pRetailer1 = new JPanel();
		pRetailer1.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_pRetailer1 = new GridBagConstraints();
		gbc_pRetailer1.weighty = 25.0;
		gbc_pRetailer1.weightx = 20.0;
		gbc_pRetailer1.insets = new Insets(0, 0, 5, 5);
		gbc_pRetailer1.fill = GridBagConstraints.BOTH;
		gbc_pRetailer1.gridx = 7;
		gbc_pRetailer1.gridy = 1;
		pAgents.add(pRetailer1, gbc_pRetailer1);
		
		JPanel pRetailer2 = new JPanel();
		pRetailer2.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_pRetailer2 = new GridBagConstraints();
		gbc_pRetailer2.weighty = 25.0;
		gbc_pRetailer2.weightx = 20.0;
		gbc_pRetailer2.insets = new Insets(0, 0, 5, 5);
		gbc_pRetailer2.fill = GridBagConstraints.BOTH;
		gbc_pRetailer2.gridx = 7;
		gbc_pRetailer2.gridy = 3;
		pAgents.add(pRetailer2, gbc_pRetailer2);
		
		JPanel pRetailer3 = new JPanel();
		pRetailer3.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_pRetailer3 = new GridBagConstraints();
		gbc_pRetailer3.weighty = 25.0;
		gbc_pRetailer3.weightx = 20.0;
		gbc_pRetailer3.insets = new Insets(0, 0, 5, 5);
		gbc_pRetailer3.fill = GridBagConstraints.BOTH;
		gbc_pRetailer3.gridx = 7;
		gbc_pRetailer3.gridy = 5;
		pAgents.add(pRetailer3, gbc_pRetailer3);
		
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
