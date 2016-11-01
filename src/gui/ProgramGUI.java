package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.LineBorder;
import javax.swing.text.*;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import ontologies.SupplierVocabulary;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.BevelBorder;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;

@SuppressWarnings("serial")
public class ProgramGUI extends JFrame implements SupplierVocabulary {
	private static JTextPane tpGlobalMessages;
	
	private static ProgramGUI singleton = new ProgramGUI();
	public static ProgramGUI getInstance() {
		return singleton;
	}
	
	private ProgramGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
		GridBagLayout gbl_pAppliance1 = new GridBagLayout();
		gbl_pAppliance1.columnWidths = new int[]{0, 0, 0};
		gbl_pAppliance1.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_pAppliance1.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_pAppliance1.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		pAppliance1.setLayout(gbl_pAppliance1);
		
		JButton bAppliance1 = new JButton("Appliance 1");
		bAppliance1.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_bAppliance1 = new GridBagConstraints();
		gbc_bAppliance1.weighty = 25.0;
		gbc_bAppliance1.gridwidth = 2;
		gbc_bAppliance1.insets = new Insets(0, 0, 5, 0);
		gbc_bAppliance1.fill = GridBagConstraints.BOTH;
		gbc_bAppliance1.weightx = 100.0;
		gbc_bAppliance1.gridx = 0;
		gbc_bAppliance1.gridy = 0;
		pAppliance1.add(bAppliance1, gbc_bAppliance1);
		
		JLabel lA1GenerationRate = new JLabel("Generation Rate:");
		GridBagConstraints gbc_lA1GenerationRate = new GridBagConstraints();
		gbc_lA1GenerationRate.weighty = 20.0;
		gbc_lA1GenerationRate.weightx = 60.0;
		gbc_lA1GenerationRate.insets = new Insets(5, 10, 5, 5);
		gbc_lA1GenerationRate.gridx = 0;
		gbc_lA1GenerationRate.gridy = 1;
		pAppliance1.add(lA1GenerationRate, gbc_lA1GenerationRate);
		
		JLabel lA1GenerationRateValue = new JLabel("value");
		GridBagConstraints gbc_lA1GenerationRateValue = new GridBagConstraints();
		gbc_lA1GenerationRateValue.insets = new Insets(0, 0, 5, 0);
		gbc_lA1GenerationRateValue.weightx = 40.0;
		gbc_lA1GenerationRateValue.gridx = 1;
		gbc_lA1GenerationRateValue.gridy = 1;
		pAppliance1.add(lA1GenerationRateValue, gbc_lA1GenerationRateValue);
		
		JLabel lA1UsageRate = new JLabel("Usage Rate:");
		GridBagConstraints gbc_lA1UsageRate = new GridBagConstraints();
		gbc_lA1UsageRate.weighty = 20.0;
		gbc_lA1UsageRate.weightx = 60.0;
		gbc_lA1UsageRate.insets = new Insets(0, 0, 5, 5);
		gbc_lA1UsageRate.gridx = 0;
		gbc_lA1UsageRate.gridy = 2;
		pAppliance1.add(lA1UsageRate, gbc_lA1UsageRate);
		
		JLabel lA1UsageRateValue = new JLabel("value");
		GridBagConstraints gbc_lA1UsageRateValue = new GridBagConstraints();
		gbc_lA1UsageRateValue.insets = new Insets(0, 0, 5, 0);
		gbc_lA1UsageRateValue.gridx = 1;
		gbc_lA1UsageRateValue.gridy = 2;
		pAppliance1.add(lA1UsageRateValue, gbc_lA1UsageRateValue);
		
		JPanel pAppliance1Messages = new JPanel();
		pAppliance1Messages.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_pAppliance1Messages = new GridBagConstraints();
		gbc_pAppliance1Messages.weighty = 25.0;
		gbc_pAppliance1Messages.fill = GridBagConstraints.BOTH;
		gbc_pAppliance1Messages.gridwidth = 2;
		gbc_pAppliance1Messages.insets = new Insets(5, 5, 5, 5);
		gbc_pAppliance1Messages.gridx = 0;
		gbc_pAppliance1Messages.gridy = 3;
		pAppliance1.add(pAppliance1Messages, gbc_pAppliance1Messages);
		pAppliance1Messages.setLayout(new BorderLayout(0, 0));
		
		JTextPane tpAppliance1 = new JTextPane();
		pAppliance1Messages.add(tpAppliance1);
		
		JPanel pAppliance2 = new JPanel();
		pAppliance2.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_pAppliance2 = new GridBagConstraints();
		gbc_pAppliance2.insets = new Insets(0, 0, 5, 5);
		gbc_pAppliance2.weighty = 25.0;
		gbc_pAppliance2.weightx = 20.0;
		gbc_pAppliance2.fill = GridBagConstraints.BOTH;
		gbc_pAppliance2.gridx = 1;
		gbc_pAppliance2.gridy = 3;
		pAgents.add(pAppliance2, gbc_pAppliance2);
		GridBagLayout gbl_pAppliance2 = new GridBagLayout();
		gbl_pAppliance2.columnWidths = new int[]{0, 0, 0};
		gbl_pAppliance2.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_pAppliance2.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_pAppliance2.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		pAppliance2.setLayout(gbl_pAppliance2);
		
		JButton bAppliance2 = new JButton("Appliance 2");
		bAppliance2.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_bAppliance2 = new GridBagConstraints();
		gbc_bAppliance2.weighty = 25.0;
		gbc_bAppliance2.gridwidth = 2;
		gbc_bAppliance2.insets = new Insets(0, 0, 5, 0);
		gbc_bAppliance2.fill = GridBagConstraints.BOTH;
		gbc_bAppliance2.weightx = 100.0;
		gbc_bAppliance2.gridx = 0;
		gbc_bAppliance2.gridy = 0;
		pAppliance2.add(bAppliance2, gbc_bAppliance2);
		
		JLabel lA2GenerationRate = new JLabel("Generation Rate:");
		GridBagConstraints gbc_lA2GenerationRate = new GridBagConstraints();
		gbc_lA2GenerationRate.weighty = 20.0;
		gbc_lA2GenerationRate.weightx = 60.0;
		gbc_lA2GenerationRate.insets = new Insets(5, 10, 5, 5);
		gbc_lA2GenerationRate.gridx = 0;
		gbc_lA2GenerationRate.gridy = 1;
		pAppliance2.add(lA2GenerationRate, gbc_lA2GenerationRate);
		
		JLabel lA2GenerationRateValue = new JLabel("value");
		GridBagConstraints gbc_lA2GenerationRateValue = new GridBagConstraints();
		gbc_lA2GenerationRateValue.insets = new Insets(0, 0, 5, 0);
		gbc_lA2GenerationRateValue.weightx = 40.0;
		gbc_lA2GenerationRateValue.gridx = 1;
		gbc_lA2GenerationRateValue.gridy = 1;
		pAppliance2.add(lA2GenerationRateValue, gbc_lA2GenerationRateValue);
		
		JLabel lA2UsageRate = new JLabel("Usage Rate:");
		GridBagConstraints gbc_lA2UsageRate = new GridBagConstraints();
		gbc_lA2UsageRate.weighty = 20.0;
		gbc_lA2UsageRate.weightx = 60.0;
		gbc_lA2UsageRate.insets = new Insets(0, 0, 5, 5);
		gbc_lA2UsageRate.gridx = 0;
		gbc_lA2UsageRate.gridy = 2;
		pAppliance2.add(lA2UsageRate, gbc_lA2UsageRate);
		
		JLabel lA2UsageRateValue = new JLabel("value");
		GridBagConstraints gbc_lA2UsageRateValue = new GridBagConstraints();
		gbc_lA2UsageRateValue.insets = new Insets(0, 0, 5, 0);
		gbc_lA2UsageRateValue.gridx = 1;
		gbc_lA2UsageRateValue.gridy = 2;
		pAppliance2.add(lA2UsageRateValue, gbc_lA2UsageRateValue);
		
		JPanel pAppliance2Messages = new JPanel();
		pAppliance2Messages.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pAppliance1Messages.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_pAppliance2Messages = new GridBagConstraints();
		gbc_pAppliance2Messages.weighty = 25.0;
		gbc_pAppliance2Messages.fill = GridBagConstraints.BOTH;
		gbc_pAppliance2Messages.gridwidth = 2;
		gbc_pAppliance2Messages.insets = new Insets(5, 5, 5, 5);
		gbc_pAppliance2Messages.gridx = 0;
		gbc_pAppliance2Messages.gridy = 3;
		pAppliance2.add(pAppliance2Messages, gbc_pAppliance2Messages);
		pAppliance2Messages.setLayout(new BorderLayout(0, 0));
		
		JTextPane tpAppliance2 = new JTextPane();
		pAppliance2Messages.add(tpAppliance2);
		
		JPanel pAppliance3 = new JPanel();
		pAppliance3.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_pAppliance3 = new GridBagConstraints();
		gbc_pAppliance3.insets = new Insets(0, 0, 5, 5);
		gbc_pAppliance3.weighty = 25.0;
		gbc_pAppliance3.weightx = 20.0;
		gbc_pAppliance3.fill = GridBagConstraints.BOTH;
		gbc_pAppliance3.gridx = 1;
		gbc_pAppliance3.gridy = 5;
		pAgents.add(pAppliance3, gbc_pAppliance3);
		GridBagLayout gbl_pAppliance3 = new GridBagLayout();
		gbl_pAppliance3.columnWidths = new int[]{0, 0, 0};
		gbl_pAppliance3.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_pAppliance3.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_pAppliance3.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		pAppliance3.setLayout(gbl_pAppliance3);
		
		JButton bAppliance3 = new JButton("Appliance 3");
		bAppliance3.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_bAppliance3 = new GridBagConstraints();
		gbc_bAppliance3.weighty = 25.0;
		gbc_bAppliance3.gridwidth = 2;
		gbc_bAppliance3.insets = new Insets(0, 0, 5, 0);
		gbc_bAppliance3.fill = GridBagConstraints.BOTH;
		gbc_bAppliance3.weightx = 100.0;
		gbc_bAppliance3.gridx = 0;
		gbc_bAppliance3.gridy = 0;
		pAppliance3.add(bAppliance3, gbc_bAppliance3);
		
		JLabel lA3GenerationRate = new JLabel("Generation Rate:");
		GridBagConstraints gbc_lA3GenerationRate = new GridBagConstraints();
		gbc_lA3GenerationRate.weighty = 20.0;
		gbc_lA3GenerationRate.weightx = 60.0;
		gbc_lA3GenerationRate.insets = new Insets(5, 10, 5, 5);
		gbc_lA3GenerationRate.gridx = 0;
		gbc_lA3GenerationRate.gridy = 1;
		pAppliance3.add(lA3GenerationRate, gbc_lA3GenerationRate);
		
		JLabel lA3GenerationRateValue = new JLabel("value");
		GridBagConstraints gbc_lA3GenerationRateValue = new GridBagConstraints();
		gbc_lA3GenerationRateValue.insets = new Insets(0, 0, 5, 0);
		gbc_lA3GenerationRateValue.weightx = 40.0;
		gbc_lA3GenerationRateValue.gridx = 1;
		gbc_lA3GenerationRateValue.gridy = 1;
		pAppliance3.add(lA3GenerationRateValue, gbc_lA3GenerationRateValue);
		
		JLabel lA3UsageRate = new JLabel("Usage Rate:");
		GridBagConstraints gbc_lA3UsageRate = new GridBagConstraints();
		gbc_lA3UsageRate.weighty = 20.0;
		gbc_lA3UsageRate.weightx = 60.0;
		gbc_lA3UsageRate.insets = new Insets(0, 0, 5, 5);
		gbc_lA3UsageRate.gridx = 0;
		gbc_lA3UsageRate.gridy = 2;
		pAppliance3.add(lA3UsageRate, gbc_lA3UsageRate);
		
		JLabel lA3UsageRateValue = new JLabel("value");
		GridBagConstraints gbc_lA3UsageRateValue = new GridBagConstraints();
		gbc_lA3UsageRateValue.insets = new Insets(0, 0, 5, 0);
		gbc_lA3UsageRateValue.gridx = 1;
		gbc_lA3UsageRateValue.gridy = 2;
		pAppliance3.add(lA3UsageRateValue, gbc_lA3UsageRateValue);
		
		JPanel pAppliance3Messages = new JPanel();
		pAppliance3Messages.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_pAppliance3Messages = new GridBagConstraints();
		gbc_pAppliance3Messages.weighty = 25.0;
		gbc_pAppliance3Messages.fill = GridBagConstraints.BOTH;
		gbc_pAppliance3Messages.gridwidth = 2;
		gbc_pAppliance3Messages.insets = new Insets(5, 5, 5, 5);
		gbc_pAppliance3Messages.gridx = 0;
		gbc_pAppliance3Messages.gridy = 3;
		pAppliance3.add(pAppliance3Messages, gbc_pAppliance3Messages);
		pAppliance3Messages.setLayout(new BorderLayout(0, 0));
		
		JTextPane tpAppliance3 = new JTextPane();
		pAppliance3Messages.add(tpAppliance3);
		
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
		
		tpGlobalMessages = new JTextPane();
		tpGlobalMessages.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(tpGlobalMessages, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
		
		// Clear the text pane
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tpGlobalMessages.setText("");
			}
		});
		
		setSize(1920, 1080);
	}
	
	private static void appendToPane(JTextPane tp, String msg, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.FontSize, 16);
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        try {
        	StyledDocument document = tp.getStyledDocument();
        	document.insertString(document.getLength(), msg, aset);
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }
	
	public static void printToLog(String msg, Color c) {
		System.out.println("\n\n\n PRINTING \n\n\n");
		appendToPane(tpGlobalMessages, msg, c);
	}
}
