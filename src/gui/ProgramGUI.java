package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.text.*;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import ontologies.*;

@SuppressWarnings("serial")
public class ProgramGUI extends JFrame implements SupplierVocabulary {
	private JTextPane tpGlobalMessages;
	private HashMap<Integer, JTextPane> agentLogs = new HashMap<Integer, JTextPane>();
	private static ProgramGUI singleton = new ProgramGUI();
	
	// Components
	private JButton bAppliance1;
	private JLabel lA1GenerationRateValue;
	private JLabel lA1UsageRateValue;
	private JTextPane tpAppliance1;
	
	private JButton bAppliance2;
	private JLabel lA2GenerationRateValue;
	private JLabel lA2UsageRateValue;
	private JTextPane tpAppliance2;
	
	private JButton bAppliance3;
	private JLabel lA3GenerationRateValue;
	private JLabel lA3UsageRateValue;
	private JTextPane tpAppliance3;
	
	private JButton bRetailer1;
	private JLabel lR1GenerationRateValue;
	private JLabel lR1PricePerUnitValue;
	private JLabel lR1SupplyValue;
	private JLabel lR1TypeValue;
	private JTextPane tpRetailer1;
	
	private JButton bRetailer2;
	private JLabel lR2GenerationRateValue;
	private JLabel lR2PricePerUnitValue;
	private JLabel lR2SupplyValue;
	private JLabel lR2TypeValue;
	private JTextPane tpRetailer2;
	
	private JButton bRetailer3;
	private JLabel lR3GenerationRateValue;
	private JLabel lR3PricePerUnitValue;
	private JLabel lR3SupplyValue;
	private JLabel lR3TypeValue;
	private JTextPane tpRetailer3;
	
	private JButton bHome;
	private JLabel lHomeGenerationRateValue;
	private JLabel lHomeUsageRateValue;
	private JLabel lHomeSupplyValue;
	private JLabel lHomeBudgetValue;
	private JLabel lHomeExpenditureValue;
	private JTextPane tpHome;
	
	private JButton bBroker;
	private JTextPane tpBroker;
	
	public static ProgramGUI getInstance() {
		return singleton;
	}
	
	private Home getHome() {
		return SettingsGUI.getInstance().getHome();
	}
	
	private Appliance[] getAppliances() {
		return SettingsGUI.getInstance().getAppliances();
	}
	
	private Retailer[] getRetailers() {
		return SettingsGUI.getInstance().getRetailers();
	}
	
	private Broker getBroker() {
		return SettingsGUI.getInstance().getBroker();
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
		
		// APPLIANCES
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
		
		bAppliance1 = new JButton("Appliance 1");
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
		
		lA1GenerationRateValue = new JLabel("value");
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
		
		lA1UsageRateValue = new JLabel("value");
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
		
		tpAppliance1 = new JTextPane();
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
		
		bAppliance2 = new JButton("Appliance 2");
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
		
		lA2GenerationRateValue = new JLabel("value");
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
		
		lA2UsageRateValue = new JLabel("value");
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
		
		tpAppliance2 = new JTextPane();
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
		
		bAppliance3 = new JButton("Appliance 3");
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
		
		lA3GenerationRateValue = new JLabel("value");
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
		
		lA3UsageRateValue = new JLabel("value");
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
		
		tpAppliance3 = new JTextPane();
		pAppliance3Messages.add(tpAppliance3);
		
		// HOME
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
		GridBagLayout gbl_pHome = new GridBagLayout();
		gbl_pHome.columnWidths = new int[]{0};
		gbl_pHome.rowHeights = new int[]{0};
		gbl_pHome.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_pHome.rowWeights = new double[]{Double.MIN_VALUE};
		pHome.setLayout(gbl_pHome);
		
		bHome = new JButton("Home Agent");
		bHome.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_bHome = new GridBagConstraints();
		gbc_bHome.weighty = 25.0;
		gbc_bHome.gridwidth = 2;
		gbc_bHome.insets = new Insets(0, 0, 5, 0);
		gbc_bHome.fill = GridBagConstraints.BOTH;
		gbc_bHome.weightx = 100.0;
		gbc_bHome.gridx = 0;
		gbc_bHome.gridy = 0;
		pHome.add(bHome, gbc_bHome);
	
		JLabel lHomeGenerationRate = new JLabel("Generation Rate:");
		GridBagConstraints gbc_lHomeGenerationRate = new GridBagConstraints();
		gbc_lHomeGenerationRate.weighty = 5.0;
		gbc_lHomeGenerationRate.weightx = 60.0;
		gbc_lHomeGenerationRate.insets = new Insets(5, 10, 5, 5);
		gbc_lHomeGenerationRate.gridx = 0;
		gbc_lHomeGenerationRate.gridy = 1;
		pHome.add(lHomeGenerationRate, gbc_lHomeGenerationRate);
		
		lHomeGenerationRateValue = new JLabel("value");
		GridBagConstraints gbc_lHomeGenerationRateValue = new GridBagConstraints();
		gbc_lHomeGenerationRateValue.insets = new Insets(0, 0, 5, 0);
		gbc_lHomeGenerationRateValue.weightx = 40.0;
		gbc_lHomeGenerationRateValue.gridx = 1;
		gbc_lHomeGenerationRateValue.gridy = 1;
		pHome.add(lHomeGenerationRateValue, gbc_lHomeGenerationRateValue);
		
		JLabel lHomeUsageRate = new JLabel("Usage Rate:");
		GridBagConstraints gbc_lHomeUsageRate = new GridBagConstraints();
		gbc_lHomeUsageRate.weighty = 5.0;
		gbc_lHomeUsageRate.weightx = 60.0;
		gbc_lHomeUsageRate.insets = new Insets(0, 0, 5, 5);
		gbc_lHomeUsageRate.gridx = 0;
		gbc_lHomeUsageRate.gridy = 2;
		pHome.add(lHomeUsageRate, gbc_lHomeUsageRate);
		
		lHomeUsageRateValue = new JLabel("value");
		GridBagConstraints gbc_lHomeUsageRateValue = new GridBagConstraints();
		gbc_lHomeUsageRateValue.insets = new Insets(0, 0, 5, 0);
		gbc_lHomeUsageRateValue.gridx = 1;
		gbc_lHomeUsageRateValue.gridy = 2;
		pHome.add(lHomeUsageRateValue, gbc_lHomeUsageRateValue);
		
		JLabel lHomeSupply = new JLabel("Supply:");
		GridBagConstraints gbc_lHomeSupply = new GridBagConstraints();
		gbc_lHomeSupply.weighty = 5.0;
		gbc_lHomeSupply.weightx = 60.0;
		gbc_lHomeSupply.insets = new Insets(0, 0, 5, 5);
		gbc_lHomeSupply.gridx = 0;
		gbc_lHomeSupply.gridy = 3;
		pHome.add(lHomeSupply, gbc_lHomeSupply);
		
		lHomeSupplyValue = new JLabel("value");
		GridBagConstraints gbc_lHomeSupplyValue = new GridBagConstraints();
		gbc_lHomeSupplyValue.insets = new Insets(0, 0, 5, 0);
		gbc_lHomeSupplyValue.gridx = 1;
		gbc_lHomeSupplyValue.gridy = 3;
		pHome.add(lHomeSupplyValue, gbc_lHomeSupplyValue);
		
		JLabel lHomeBudget = new JLabel("Budget:");
		GridBagConstraints gbc_lHomeBudget = new GridBagConstraints();
		gbc_lHomeBudget.weighty = 5.0;
		gbc_lHomeBudget.weightx = 60.0;
		gbc_lHomeBudget.insets = new Insets(0, 0, 5, 5);
		gbc_lHomeBudget.gridx = 0;
		gbc_lHomeBudget.gridy = 4;
		pHome.add(lHomeBudget, gbc_lHomeBudget);
		
		lHomeBudgetValue = new JLabel("value");
		GridBagConstraints gbc_lHomeBudgetValue = new GridBagConstraints();
		gbc_lHomeBudgetValue.insets = new Insets(0, 0, 5, 0);
		gbc_lHomeBudgetValue.gridx = 1;
		gbc_lHomeBudgetValue.gridy = 4;
		pHome.add(lHomeBudgetValue, gbc_lHomeBudgetValue);
		
		JLabel lHomeExpediture = new JLabel("Income:");
		GridBagConstraints gbc_lHomeExpenditure = new GridBagConstraints();
		gbc_lHomeExpenditure.weighty = 5.0;
		gbc_lHomeExpenditure.weightx = 60.0;
		gbc_lHomeExpenditure.insets = new Insets(0, 0, 5, 5);
		gbc_lHomeExpenditure.gridx = 0;
		gbc_lHomeExpenditure.gridy = 5;
		pHome.add(lHomeExpediture, gbc_lHomeExpenditure);
		
		lHomeExpenditureValue = new JLabel("value");
		GridBagConstraints gbc_lHomeExpenditureValue = new GridBagConstraints();
		gbc_lHomeExpenditureValue.insets = new Insets(0, 0, 5, 0);
		gbc_lHomeExpenditureValue.gridx = 1;
		gbc_lHomeExpenditureValue.gridy = 5;
		pHome.add(lHomeExpenditureValue, gbc_lHomeExpenditureValue);
		
		JPanel pHomeMessages = new JPanel();
		pHomeMessages.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_pHomeMessages = new GridBagConstraints();
		gbc_pHomeMessages.weighty = 25.0;
		gbc_pHomeMessages.fill = GridBagConstraints.BOTH;
		gbc_pHomeMessages.gridwidth = 2;
		gbc_pHomeMessages.insets = new Insets(5, 5, 5, 5);
		gbc_pHomeMessages.gridx = 0;
		gbc_pHomeMessages.gridy = 6;
		pHome.add(pHomeMessages, gbc_pHomeMessages);
		pHomeMessages.setLayout(new BorderLayout(0, 0));
		
		tpHome = new JTextPane();
		pHomeMessages.add(tpHome);
		
		// BROKER
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
		GridBagLayout gbl_pBroker = new GridBagLayout();
		gbl_pBroker.columnWidths = new int[]{0};
		gbl_pBroker.rowHeights = new int[]{0};
		gbl_pBroker.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_pBroker.rowWeights = new double[]{Double.MIN_VALUE};
		pBroker.setLayout(gbl_pBroker);
		
		bBroker = new JButton("Broker Agent");
		bBroker.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_bBroker = new GridBagConstraints();
		gbc_bBroker.weighty = 25.0;
		gbc_bBroker.anchor = GridBagConstraints.NORTH;
		gbc_bBroker.gridwidth = 2;
		gbc_bBroker.insets = new Insets(0, 0, 5, 0);
		gbc_bBroker.fill = GridBagConstraints.BOTH;
		gbc_bBroker.weightx = 100.0;
		gbc_bBroker.gridx = 0;
		gbc_bBroker.gridy = 0;
		pBroker.add(bBroker, gbc_bBroker);
		
		JPanel pBrokerMessages = new JPanel();
		pBrokerMessages.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_pBrokerMessages = new GridBagConstraints();
		gbc_pBrokerMessages.fill = GridBagConstraints.BOTH;
		gbc_pBrokerMessages.gridwidth = 2;
		gbc_pBrokerMessages.insets = new Insets(5, 5, 5, 5);
		gbc_pBrokerMessages.gridx = 0;
		gbc_pBrokerMessages.gridy = 4;
		pBroker.add(pBrokerMessages, gbc_pBrokerMessages);
		pBrokerMessages.setLayout(new BorderLayout(0, 0));
		
		tpBroker = new JTextPane();
		pBrokerMessages.add(tpBroker);
		
		// RETAILERS
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
		GridBagLayout gbl_pRetailer1 = new GridBagLayout();
		gbl_pRetailer1.columnWidths = new int[]{0, 0, 0};
		gbl_pRetailer1.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_pRetailer1.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_pRetailer1.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		pRetailer1.setLayout(gbl_pRetailer1);
		
		bRetailer1 = new JButton("Retailer 1");
		bRetailer1.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_bRetailer1 = new GridBagConstraints();
		gbc_bRetailer1.weighty = 25.0;
		gbc_bRetailer1.gridwidth = 2;
		gbc_bRetailer1.insets = new Insets(0, 0, 5, 0);
		gbc_bRetailer1.fill = GridBagConstraints.BOTH;
		gbc_bRetailer1.weightx = 100.0;
		gbc_bRetailer1.gridx = 0;
		gbc_bRetailer1.gridy = 0;
		pRetailer1.add(bRetailer1, gbc_bRetailer1);
		
		JLabel lR1GenerationRate = new JLabel("Generation Rate:");
		GridBagConstraints gbc_lR1GenerationRate = new GridBagConstraints();
		gbc_lR1GenerationRate.weighty = 10.0;
		gbc_lR1GenerationRate.weightx = 60.0;
		gbc_lR1GenerationRate.insets = new Insets(5, 10, 5, 5);
		gbc_lR1GenerationRate.gridx = 0;
		gbc_lR1GenerationRate.gridy = 1;
		pRetailer1.add(lR1GenerationRate, gbc_lR1GenerationRate);
		
		lR1GenerationRateValue = new JLabel("value");
		GridBagConstraints gbc_lR1GenerationRateValue = new GridBagConstraints();
		gbc_lR1GenerationRateValue.insets = new Insets(0, 0, 5, 0);
		gbc_lR1GenerationRateValue.weightx = 40.0;
		gbc_lR1GenerationRateValue.gridx = 1;
		gbc_lR1GenerationRateValue.gridy = 1;
		pRetailer1.add(lR1GenerationRateValue, gbc_lR1GenerationRateValue);
		
		JLabel lR1PricePerUnit = new JLabel("Price Per Unit:");
		GridBagConstraints gbc_lR1PricePerUnit = new GridBagConstraints();
		gbc_lR1PricePerUnit.weighty = 10.0;
		gbc_lR1PricePerUnit.weightx = 60.0;
		gbc_lR1PricePerUnit.insets = new Insets(0, 0, 5, 5);
		gbc_lR1PricePerUnit.gridx = 0;
		gbc_lR1PricePerUnit.gridy = 2;
		pRetailer1.add(lR1PricePerUnit, gbc_lR1PricePerUnit);
		
		lR1PricePerUnitValue = new JLabel("value");
		GridBagConstraints gbc_lR1PricePerUnitValue = new GridBagConstraints();
		gbc_lR1PricePerUnitValue.insets = new Insets(0, 0, 5, 0);
		gbc_lR1PricePerUnitValue.gridx = 1;
		gbc_lR1PricePerUnitValue.gridy = 2;
		pRetailer1.add(lR1PricePerUnitValue, gbc_lR1PricePerUnitValue);
		
		JLabel lR1Supply = new JLabel("Supply:");
		GridBagConstraints gbc_lR1Supply = new GridBagConstraints();
		gbc_lR1Supply.weighty = 10.0;
		gbc_lR1Supply.weightx = 60.0;
		gbc_lR1Supply.insets = new Insets(0, 0, 5, 5);
		gbc_lR1Supply.gridx = 0;
		gbc_lR1Supply.gridy = 3;
		pRetailer1.add(lR1Supply, gbc_lR1Supply);
		
		lR1SupplyValue = new JLabel("value");
		GridBagConstraints gbc_lR1SupplyValue = new GridBagConstraints();
		gbc_lR1SupplyValue.insets = new Insets(0, 0, 5, 0);
		gbc_lR1SupplyValue.gridx = 1;
		gbc_lR1SupplyValue.gridy = 3;
		pRetailer1.add(lR1SupplyValue, gbc_lR1SupplyValue);
		
		JLabel lR1Type = new JLabel("Behaviour:");
		GridBagConstraints gbc_lR1Type = new GridBagConstraints();
		gbc_lR1Type.weighty = 10.0;
		gbc_lR1Type.weightx = 60.0;
		gbc_lR1Type.insets = new Insets(0, 0, 5, 5);
		gbc_lR1Type.gridx = 0;
		gbc_lR1Type.gridy = 4;
		pRetailer1.add(lR1Type, gbc_lR1Type);
		
		lR1TypeValue = new JLabel("value");
		GridBagConstraints gbc_lR1TypeValue = new GridBagConstraints();
		gbc_lR1TypeValue.insets = new Insets(0, 0, 5, 0);
		gbc_lR1TypeValue.gridx = 1;
		gbc_lR1TypeValue.gridy = 4;
		pRetailer1.add(lR1TypeValue, gbc_lR1TypeValue);
		
		JPanel pRetailer1Messages = new JPanel();
		pRetailer1Messages.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_pRetailer1Messages = new GridBagConstraints();
		gbc_pRetailer1Messages.weighty = 25.0;
		gbc_pRetailer1Messages.fill = GridBagConstraints.BOTH;
		gbc_pRetailer1Messages.gridwidth = 2;
		gbc_pRetailer1Messages.insets = new Insets(5, 5, 5, 5);
		gbc_pRetailer1Messages.gridx = 0;
		gbc_pRetailer1Messages.gridy = 5;
		pRetailer1.add(pRetailer1Messages, gbc_pRetailer1Messages);
		pRetailer1Messages.setLayout(new BorderLayout(0, 0));
		
		tpRetailer1 = new JTextPane();
		pRetailer1Messages.add(tpRetailer1);
		
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
		GridBagLayout gbl_pRetailer2 = new GridBagLayout();
		gbl_pRetailer2.columnWidths = new int[]{0, 0, 0};
		gbl_pRetailer2.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_pRetailer2.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_pRetailer1.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		pRetailer2.setLayout(gbl_pRetailer2);
		
		bRetailer2 = new JButton("Retailer 2");
		bRetailer2.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_bRetailer2 = new GridBagConstraints();
		gbc_bRetailer2.weighty = 25.0;
		gbc_bRetailer2.gridwidth = 2;
		gbc_bRetailer2.insets = new Insets(0, 0, 5, 0);
		gbc_bRetailer2.fill = GridBagConstraints.BOTH;
		gbc_bRetailer2.weightx = 100.0;
		gbc_bRetailer2.gridx = 0;
		gbc_bRetailer2.gridy = 0;
		pRetailer2.add(bRetailer2, gbc_bRetailer2);
		gbc_lR1GenerationRate.weighty = 10.0;
		gbc_lR1GenerationRate.weightx = 60.0;
		gbc_lR1GenerationRate.insets = new Insets(5, 10, 5, 5);
		gbc_lR1GenerationRate.gridx = 0;
		gbc_lR1GenerationRate.gridy = 1;
		
		JLabel lR2GenerationRate = new JLabel("Generation Rate:");
		GridBagConstraints gbc_lR2GenerationRate = new GridBagConstraints();
		gbc_lR2GenerationRate.weighty = 10.0;
		gbc_lR2GenerationRate.weightx = 60.0;
		gbc_lR2GenerationRate.insets = new Insets(5, 10, 5, 5);
		gbc_lR2GenerationRate.gridx = 0;
		gbc_lR2GenerationRate.gridy = 1;
		pRetailer2.add(lR2GenerationRate, gbc_lR2GenerationRate);
		
		lR2GenerationRateValue = new JLabel("value");
		GridBagConstraints gbc_lR2GenerationRateValue = new GridBagConstraints();
		gbc_lR2GenerationRateValue.insets = new Insets(0, 0, 5, 0);
		gbc_lR2GenerationRateValue.weightx = 40.0;
		gbc_lR2GenerationRateValue.gridx = 1;
		gbc_lR2GenerationRateValue.gridy = 1;
		pRetailer2.add(lR2GenerationRateValue, gbc_lR2GenerationRateValue);
		
		JLabel lR2PricePerUnit = new JLabel("Price Per Unit:");
		GridBagConstraints gbc_lR2PricePerUnit = new GridBagConstraints();
		gbc_lR2PricePerUnit.weighty = 10.0;
		gbc_lR2PricePerUnit.weightx = 60.0;
		gbc_lR2PricePerUnit.insets = new Insets(0, 0, 5, 5);
		gbc_lR2PricePerUnit.gridx = 0;
		gbc_lR2PricePerUnit.gridy = 2;
		pRetailer2.add(lR2PricePerUnit, gbc_lR2PricePerUnit);
		
		lR2PricePerUnitValue = new JLabel("value");
		GridBagConstraints gbc_lR2PricePerUnitValue = new GridBagConstraints();
		gbc_lR2PricePerUnitValue.insets = new Insets(0, 0, 5, 0);
		gbc_lR2PricePerUnitValue.gridx = 1;
		gbc_lR2PricePerUnitValue.gridy = 2;
		pRetailer2.add(lR2PricePerUnitValue, gbc_lR2PricePerUnitValue);
		
		JLabel lR2Supply = new JLabel("Supply:");
		GridBagConstraints gbc_lR2Supply = new GridBagConstraints();
		gbc_lR2Supply.weighty = 10.0;
		gbc_lR2Supply.weightx = 60.0;
		gbc_lR2Supply.insets = new Insets(0, 0, 5, 5);
		gbc_lR2Supply.gridx = 0;
		gbc_lR2Supply.gridy = 3;
		pRetailer2.add(lR2Supply, gbc_lR2Supply);
		
		lR2SupplyValue = new JLabel("value");
		GridBagConstraints gbc_lR2SupplyValue = new GridBagConstraints();
		gbc_lR2SupplyValue.insets = new Insets(0, 0, 5, 0);
		gbc_lR2SupplyValue.gridx = 1;
		gbc_lR2SupplyValue.gridy = 3;
		pRetailer2.add(lR2SupplyValue, gbc_lR2SupplyValue);
		
		JLabel lR2Type = new JLabel("Behaviour:");
		GridBagConstraints gbc_lR2Type = new GridBagConstraints();
		gbc_lR2Type.weighty = 10.0;
		gbc_lR2Type.weightx = 60.0;
		gbc_lR2Type.insets = new Insets(0, 0, 5, 5);
		gbc_lR2Type.gridx = 0;
		gbc_lR2Type.gridy = 4;
		pRetailer2.add(lR2Type, gbc_lR2Type);
		
		lR2TypeValue = new JLabel("value");
		GridBagConstraints gbc_lR2TypeValue = new GridBagConstraints();
		gbc_lR2TypeValue.insets = new Insets(0, 0, 5, 0);
		gbc_lR2TypeValue.gridx = 1;
		gbc_lR2TypeValue.gridy = 4;
		pRetailer2.add(lR2TypeValue, gbc_lR2TypeValue);
		
		JPanel pRetailer2Messages = new JPanel();
		pRetailer2Messages.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_pRetailer2Messages = new GridBagConstraints();
		gbc_pRetailer2Messages.weighty = 25.0;
		gbc_pRetailer2Messages.fill = GridBagConstraints.BOTH;
		gbc_pRetailer2Messages.gridwidth = 2;
		gbc_pRetailer2Messages.insets = new Insets(5, 5, 5, 5);
		gbc_pRetailer2Messages.gridx = 0;
		gbc_pRetailer2Messages.gridy = 5;
		pRetailer2.add(pRetailer2Messages, gbc_pRetailer2Messages);
		pRetailer2Messages.setLayout(new BorderLayout(0, 0));
		
		tpRetailer2 = new JTextPane();
		pRetailer2Messages.add(tpRetailer2);
		
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
		GridBagLayout gbl_pRetailer3 = new GridBagLayout();
		gbl_pRetailer3.columnWidths = new int[]{0, 0, 0};
		gbl_pRetailer3.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_pRetailer3.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_pRetailer3.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		pRetailer3.setLayout(gbl_pRetailer3);
		
		bRetailer3 = new JButton("Retailer 3");
		bRetailer3.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_bRetailer3 = new GridBagConstraints();
		gbc_bRetailer3.weighty = 25.0;
		gbc_bRetailer3.gridwidth = 2;
		gbc_bRetailer3.insets = new Insets(0, 0, 5, 0);
		gbc_bRetailer3.fill = GridBagConstraints.BOTH;
		gbc_bRetailer3.weightx = 100.0;
		gbc_bRetailer3.gridx = 0;
		gbc_bRetailer3.gridy = 0;
		pRetailer3.add(bRetailer3, gbc_bRetailer3);
		
		JLabel lR3GenerationRate = new JLabel("Generation Rate:");
		GridBagConstraints gbc_lR3GenerationRate = new GridBagConstraints();
		gbc_lR3GenerationRate.weighty = 10.0;
		gbc_lR3GenerationRate.weightx = 60.0;
		gbc_lR3GenerationRate.insets = new Insets(5, 10, 5, 5);
		gbc_lR3GenerationRate.gridx = 0;
		gbc_lR3GenerationRate.gridy = 1;
		pRetailer3.add(lR3GenerationRate, gbc_lR3GenerationRate);
		
		lR3GenerationRateValue = new JLabel("value");
		GridBagConstraints gbc_lR3GenerationRateValue = new GridBagConstraints();
		gbc_lR3GenerationRateValue.insets = new Insets(0, 0, 5, 0);
		gbc_lR3GenerationRateValue.weightx = 40.0;
		gbc_lR3GenerationRateValue.gridx = 1;
		gbc_lR3GenerationRateValue.gridy = 1;
		pRetailer3.add(lR3GenerationRateValue, gbc_lR3GenerationRateValue);
		
		JLabel lR3PricePerUnit = new JLabel("Price Per Unit:");
		GridBagConstraints gbc_lR3PricePerUnit = new GridBagConstraints();
		gbc_lR3PricePerUnit.weighty = 10.0;
		gbc_lR3PricePerUnit.weightx = 60.0;
		gbc_lR3PricePerUnit.insets = new Insets(0, 0, 5, 5);
		gbc_lR3PricePerUnit.gridx = 0;
		gbc_lR3PricePerUnit.gridy = 2;
		pRetailer3.add(lR3PricePerUnit, gbc_lR3PricePerUnit);
		
		lR3PricePerUnitValue = new JLabel("value");
		GridBagConstraints gbc_lR3PricePerUnitValue = new GridBagConstraints();
		gbc_lR3PricePerUnitValue.insets = new Insets(0, 0, 5, 0);
		gbc_lR3PricePerUnitValue.gridx = 1;
		gbc_lR3PricePerUnitValue.gridy = 2;
		pRetailer3.add(lR3PricePerUnitValue, gbc_lR3PricePerUnitValue);
		
		JLabel lR3Supply = new JLabel("Supply:");
		GridBagConstraints gbc_lR3Supply = new GridBagConstraints();
		gbc_lR3Supply.weighty = 10.0;
		gbc_lR3Supply.weightx = 60.0;
		gbc_lR3Supply.insets = new Insets(0, 0, 5, 5);
		gbc_lR3Supply.gridx = 0;
		gbc_lR3Supply.gridy = 3;
		pRetailer3.add(lR3Supply, gbc_lR3Supply);
		
		lR3SupplyValue = new JLabel("value");
		GridBagConstraints gbc_lR3SupplyValue = new GridBagConstraints();
		gbc_lR3SupplyValue.insets = new Insets(0, 0, 5, 0);
		gbc_lR3SupplyValue.gridx = 1;
		gbc_lR3SupplyValue.gridy = 3;
		pRetailer3.add(lR3SupplyValue, gbc_lR3SupplyValue);
		
		JLabel lR3Type = new JLabel("Behaviour:");
		GridBagConstraints gbc_lR3Type = new GridBagConstraints();
		gbc_lR3Type.weighty = 10.0;
		gbc_lR3Type.weightx = 60.0;
		gbc_lR3Type.insets = new Insets(0, 0, 5, 5);
		gbc_lR3Type.gridx = 0;
		gbc_lR3Type.gridy = 4;
		pRetailer3.add(lR3Type, gbc_lR3Type);
		
		lR3TypeValue = new JLabel("value");
		GridBagConstraints gbc_lR3TypeValue = new GridBagConstraints();
		gbc_lR3TypeValue.insets = new Insets(0, 0, 5, 0);
		gbc_lR3TypeValue.gridx = 1;
		gbc_lR3TypeValue.gridy = 4;
		pRetailer3.add(lR3TypeValue, gbc_lR3TypeValue);
		
		JPanel pRetailer3Messages = new JPanel();
		pRetailer3Messages.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_pRetailer3Messages = new GridBagConstraints();
		gbc_pRetailer3Messages.weighty = 25.0;
		gbc_pRetailer3Messages.fill = GridBagConstraints.BOTH;
		gbc_pRetailer3Messages.gridwidth = 2;
		gbc_pRetailer3Messages.insets = new Insets(5, 5, 5, 5);
		gbc_pRetailer3Messages.gridx = 0;
		gbc_pRetailer3Messages.gridy = 5;
		pRetailer3.add(pRetailer3Messages, gbc_pRetailer3Messages);
		pRetailer3Messages.setLayout(new BorderLayout(0, 0));
		
		tpRetailer3 = new JTextPane();
		pRetailer3Messages.add(tpRetailer3);
		
		// Global log
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
	
	public void updateGui() {	
		updateAppliance(0, bAppliance1, lA1GenerationRateValue, lA1UsageRateValue, tpAppliance1);
		updateAppliance(1, bAppliance2, lA2GenerationRateValue, lA2UsageRateValue, tpAppliance2);
		updateAppliance(2, bAppliance3, lA3GenerationRateValue, lA3UsageRateValue, tpAppliance3);
		updateRetailer(0, bRetailer1, lR1GenerationRateValue, lR1PricePerUnitValue, lR1SupplyValue, lR1TypeValue, tpRetailer1);
		updateRetailer(1, bRetailer2, lR2GenerationRateValue, lR2PricePerUnitValue, lR2SupplyValue, lR2TypeValue, tpRetailer2);
		updateRetailer(2, bRetailer3, lR3GenerationRateValue, lR3PricePerUnitValue, lR3SupplyValue, lR3TypeValue, tpRetailer3);
		
		// update home
		try {
			Home home = getHome();
			
			if(home.toString() != null) {
				bHome.setText(home.getName());
				lHomeGenerationRateValue.setText(Integer.toString(home.getGenerationRate()));
				lHomeUsageRateValue.setText(Integer.toString(home.getUsageRate()));
				lHomeSupplyValue.setText(Integer.toString(home.getSupply()));
				lHomeBudgetValue.setText(Integer.toString(home.getBudget()));
				lHomeExpenditureValue.setText(Integer.toString(home.getIncome()));
				
				if(!agentLogs.containsKey(home.hashCode())) {
					agentLogs.put(home.hashCode(), tpHome);
				}
			}
		} catch(Exception e) {
			// Catch
		}
		
		// update broker
		try {
			Broker broker = getBroker();
			
			if(broker.toString() != null) {
				if(!agentLogs.containsKey(broker.hashCode())) {
					agentLogs.put(broker.hashCode(), tpBroker);
				}
			}
		} catch(Exception e) {
			// Catch
		}
	}
	
	private void updateAppliance(int element, JButton bAppliance, JLabel lGenerationRate, JLabel lUsageRate, JTextPane tpLog) {
		try {
			Appliance[] appliances = getAppliances();
			Appliance appliance = appliances[element];
			
			if(appliance.toString() != null) {
				bAppliance.setText(appliance.getName());
				lGenerationRate.setText(Integer.toString(appliance.getGenerationRate()));
				lUsageRate.setText(Integer.toString(appliance.getUsageRate()));
				
				if (!agentLogs.containsKey(appliance.hashCode())) {
					agentLogs.put(appliance.hashCode(), tpLog);
				}
			}
		} catch(Exception e) {
			// Catch
		}
	}
	
	private void updateRetailer(int element, JButton bRetailer, JLabel lGenerationRate, JLabel lPricePerUnit, JLabel lSupply, JLabel lType, JTextPane tpLog) {
		try {
			Retailer[] retailers = getRetailers();
			Retailer retailer = retailers[element];
			
			if(retailer.toString() != null) {
				bRetailer.setText(retailer.getName());
				lGenerationRate.setText(Integer.toString(retailer.getGenerationRate()));
				lPricePerUnit.setText(Integer.toString(retailer.getPricePerUnit()));
				lSupply.setText(Integer.toString(retailer.getSupply()));
				lType.setText(retailer.getRetailerType().toString());
				
				if(!agentLogs.containsKey(retailer.hashCode())) {
					agentLogs.put(retailer.hashCode(), tpLog);
				}
			}
		} catch(Exception e) {
			// Catch
		}
	}
	
	private void appendToPane(JTextPane tp, String msg, Color c) {
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
	
	
	public void printToLog(int hash, String msg, Color c) {
		msg = msg + "\n";
		// Log all to global pane
		appendToPane(tpGlobalMessages, msg, c);
		
		// Log to individual agent pane
		try {
			JTextPane log = agentLogs.get(hash);
			if (log != null) {
				log.setText("");
				appendToPane(log, msg, c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
