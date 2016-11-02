package gui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import ontologies.*;
import ontologies.Retailer.RetailerType;
import utility.Utility;

@SuppressWarnings("serial")
public class SettingsGUI extends JFrame implements SupplierVocabulary {
	private static SettingsGUI singleton = new SettingsGUI();
	private Random rnd = Utility.newRandom(hashCode());
	
	// Store data between radio button selections
	private int selectedRetailer = 0;
	private String[] aRetailerNames = new String[3];
	private int[] aRetailerGenRates = new int[3];
	private int[] aRetailerPrices = new int[3];
	private int[] aRetailerSupplies = new int[3];
	private RetailerType[] aRetailerTypes = new RetailerType[3];
	private boolean[] aRetailerUpdateRangeCB = new boolean[3];
	private float aRetailerUpdateMinTimes[] = new float[3];
	private float aRetailerUpdateMaxTimes[] = new float[3];
	
	private int selectedAppliance = 0;
	private String[] aApplianceNames = new String[3];
	private int[] aApplianceGenRates = new int[3];
	private int[] aApplianceUseRates = new int[3];
	private boolean[] aApplianceUpdateRangeCB = new boolean[3];
	private float aApplianceUpdateMinTimes[] = new float[3];
	private float aApplianceUpdateMaxTimes[] = new float[3];
	
	// Agents
	private Appliance[] appliances = new Appliance[3];
	private Retailer[] retailers = new Retailer[3];
	private Home home;
	private Broker broker;
	
	// Components
	// Retailers
	private JRadioButton rbR1, rbR2, rbR3;
	private JTextField fRetailerName;
	private JComboBox<Retailer.RetailerType> ddRetailerType;
	private JSlider sRetailerGenRate;
	private JSlider sRetailerPrice;
	private JSlider sRetailerSupply;
	private JLabel lRetailerGenRateValue;
	private JLabel lRetailerPriceValue;
	private JLabel lRetailerSupplyValue; 
	private JCheckBox cbRandRetailerGenRate;
	private JCheckBox cbRandRetailerPrice;
	private JCheckBox cbRandRetailerSupply;
	private SliderListener slRetailerGenRate;
	private SliderListener slRetailerPrice;
	private SliderListener slRetailerSupply;
	private CheckBoxListener cblRetailerGenRate;
	private CheckBoxListener cblRetailerPrice;
	private CheckBoxListener cblRetailerSupply;
	private JSpinner spRetailerUpdateMinTime;
	private JSpinner spRetailerUpdateMaxTime;
	private JCheckBox cbRangeRetailerUpdateTime;
	
	// Home
	private JTextField fHomeName;
	private JSlider sHomeGenRate;
	private JSlider sHomeUseRate;
	private JSlider sHomeIncome;
	private JSlider sHomeSupply;
	private JSpinner spHomeTradeMinTime;
	private JSpinner spHomeTradeMaxTime;
	private JSpinner spHomeUpdateMinTime;
	private JSpinner spHomeUpdateMaxTime;
	private JCheckBox cbRangeHomeTradeTime;
	private JCheckBox cbRangeHomeUpdateTime;
	
	// Appliances
	private JRadioButton rbA1, rbA2, rbA3;
	private JTextField fApplianceName;
	private JSlider sApplianceGenRate;
	private JSlider sApplianceUseRate;
	private JLabel lApplianceGenRateValue;
	private JLabel lApplianceUseRateValue;
	private JCheckBox cbRandApplianceGenRate;
	private JCheckBox cbRandApplianceUseRate;
	private JSpinner spApplianceUpdateMinTime;
	private JSpinner spApplianceUpdateMaxTime;
	private JCheckBox cbRangeApplianceUpdateTime;
	private SliderListener slApplianceGenRate;
	private SliderListener slApplianceUseRate;
	private CheckBoxListener cblApplianceGenRate;
	private CheckBoxListener cblApplianceUseRate;
	
	public static SettingsGUI getInstance() {
		return singleton;
	}
	
	public Retailer[] getRetailers() {
		return retailers;
	}
	
	public Appliance[] getAppliances() {
		return appliances;
	}
	
	public Home getHome() {
		return home;
	}
	
	public Broker getBroker() {
		return broker;
	}
	
	private SettingsGUI() {
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
		pBody.setLayout(new GridLayout(1, 3, 0, 0));
		
		// APPLIANCES FORM
		JPanel pAppliances = new JPanel();
		pBody.add(pAppliances);
		GridBagLayout gbl_pAppliances = new GridBagLayout();
		gbl_pAppliances.columnWidths = new int[]{0, 0};
		gbl_pAppliances.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_pAppliances.columnWeights = new double[]{1.0, 1.0};
		gbl_pAppliances.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		pAppliances.setLayout(gbl_pAppliances);
		
		JLabel lAppliancesTitle = new JLabel("Appliance Agents Parameters");
		lAppliancesTitle.setFont(new Font("Calibri", Font.PLAIN, 24));
		GridBagConstraints gbc_lAppliancesTitle = new GridBagConstraints();
		gbc_lAppliancesTitle.gridwidth = 4;
		gbc_lAppliancesTitle.insets = new Insets(0, 0, 5, 0);
		gbc_lAppliancesTitle.gridx = 0;
		gbc_lAppliancesTitle.gridy = 0;
		pAppliances.add(lAppliancesTitle, gbc_lAppliancesTitle);
		
		JLabel lApplianceSelect = new JLabel("Appliance:");
		GridBagConstraints gbc_lApplianceSelect = new GridBagConstraints();
		gbc_lApplianceSelect.anchor = GridBagConstraints.EAST;
		gbc_lApplianceSelect.insets = new Insets(0, 0, 5, 5);
		gbc_lApplianceSelect.gridx = 0;
		gbc_lApplianceSelect.gridy = 1;
		pAppliances.add(lApplianceSelect, gbc_lApplianceSelect);
		
		JPanel pApplianceSelect = new JPanel();
		FlowLayout flApplianceSelect = (FlowLayout) pApplianceSelect.getLayout();
		flApplianceSelect.setVgap(0);
		flApplianceSelect.setHgap(0);
		GridBagConstraints gbc_pApplianceSelect = new GridBagConstraints();
		gbc_pApplianceSelect.fill = GridBagConstraints.BOTH;
		gbc_pApplianceSelect.insets = new Insets(0, 0, 5, 5);
		gbc_pApplianceSelect.gridx = 1;
		gbc_pApplianceSelect.gridy = 1;
		pAppliances.add(pApplianceSelect, gbc_pApplianceSelect);
		
		rbA1 = new JRadioButton("1");
		rbA1.setSelected(true);
		pApplianceSelect.add(rbA1);
		
		rbA2 = new JRadioButton("2");
		pApplianceSelect.add(rbA2);
		
		rbA3 = new JRadioButton("3");
		pApplianceSelect.add(rbA3);
		
		ButtonGroup rbgApplianceSelect = new ButtonGroup();
		rbgApplianceSelect.add(rbA1);
		rbgApplianceSelect.add(rbA2);
		rbgApplianceSelect.add(rbA3);
		
		JLabel lApplianceName = new JLabel("Name:");
		GridBagConstraints gbc_lApplianceName = new GridBagConstraints();
		gbc_lApplianceName.anchor = GridBagConstraints.EAST;
		gbc_lApplianceName.insets = new Insets(0, 0, 5, 5);
		gbc_lApplianceName.gridx = 0;
		gbc_lApplianceName.gridy = 2;
		pAppliances.add(lApplianceName, gbc_lApplianceName);
		
		fApplianceName = new JTextField();
		fApplianceName.setText("A1");
		fApplianceName.setColumns(10);
		GridBagConstraints gbc_fApplianceName = new GridBagConstraints();
		gbc_fApplianceName.fill = GridBagConstraints.HORIZONTAL;
		gbc_fApplianceName.insets = new Insets(0, 0, 5, 5);
		gbc_fApplianceName.gridx = 1;
		gbc_fApplianceName.gridy = 2;
		pAppliances.add(fApplianceName, gbc_fApplianceName);
		
		JLabel lApplianceGenRate = new JLabel("Generation Rate:");
		GridBagConstraints gbc_lApplianceGenRate = new GridBagConstraints();
		gbc_lApplianceGenRate.anchor = GridBagConstraints.EAST;
		gbc_lApplianceGenRate.insets = new Insets(0, 0, 5, 5);
		gbc_lApplianceGenRate.gridx = 0;
		gbc_lApplianceGenRate.gridy = 3;
		pAppliances.add(lApplianceGenRate, gbc_lApplianceGenRate);
		
		sApplianceGenRate = new JSlider();
		sApplianceGenRate.setValue(10);
		sApplianceGenRate.setMaximum(20);
		sApplianceGenRate.setPaintTicks(true);
		sApplianceGenRate.setMajorTickSpacing(5);
		GridBagConstraints gbc_sApplianceGenRate = new GridBagConstraints();
		gbc_sApplianceGenRate.fill = GridBagConstraints.HORIZONTAL;
		gbc_sApplianceGenRate.insets = new Insets(0, 0, 5, 5);
		gbc_sApplianceGenRate.gridx = 1;
		gbc_sApplianceGenRate.gridy = 3;
		pAppliances.add(sApplianceGenRate, gbc_sApplianceGenRate);
		
		lApplianceGenRateValue = new JLabel("10");
		lApplianceGenRateValue.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lApplianceGenRateValue = new GridBagConstraints();
		gbc_lApplianceGenRateValue.insets = new Insets(0, 0, 5, 5);
		gbc_lApplianceGenRateValue.gridx = 2;
		gbc_lApplianceGenRateValue.gridy = 3;
		pAppliances.add(lApplianceGenRateValue, gbc_lApplianceGenRateValue);
		
		cbRandApplianceGenRate = new JCheckBox("Randomize");
		GridBagConstraints gbc_cbRandApplianceGenRate = new GridBagConstraints();
		gbc_cbRandApplianceGenRate.insets = new Insets(0, 0, 5, 0);
		gbc_cbRandApplianceGenRate.gridx = 3;
		gbc_cbRandApplianceGenRate.gridy = 3;
		pAppliances.add(cbRandApplianceGenRate, gbc_cbRandApplianceGenRate);
		
		JLabel lApplianceUseRate = new JLabel("Usage Rate:");
		GridBagConstraints gbc_lApplianceUseRate = new GridBagConstraints();
		gbc_lApplianceUseRate.anchor = GridBagConstraints.EAST;
		gbc_lApplianceUseRate.insets = new Insets(0, 0, 5, 5);
		gbc_lApplianceUseRate.gridx = 0;
		gbc_lApplianceUseRate.gridy = 4;
		pAppliances.add(lApplianceUseRate, gbc_lApplianceUseRate);
		
		sApplianceUseRate = new JSlider();
		sApplianceUseRate.setValue(10);
		sApplianceUseRate.setMaximum(20);
		sApplianceUseRate.setPaintTicks(true);
		sApplianceUseRate.setMajorTickSpacing(5);
		GridBagConstraints gbc_sApplianceUseRate = new GridBagConstraints();
		gbc_sApplianceUseRate.fill = GridBagConstraints.HORIZONTAL;
		gbc_sApplianceUseRate.insets = new Insets(0, 0, 5, 5);
		gbc_sApplianceUseRate.gridx = 1;
		gbc_sApplianceUseRate.gridy = 4;
		pAppliances.add(sApplianceUseRate, gbc_sApplianceUseRate);
		
		lApplianceUseRateValue = new JLabel("10");
		lApplianceUseRateValue.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lApplianceUseRateValue = new GridBagConstraints();
		gbc_lApplianceUseRateValue.insets = new Insets(0, 0, 5, 5);
		gbc_lApplianceUseRateValue.gridx = 2;
		gbc_lApplianceUseRateValue.gridy = 4;
		pAppliances.add(lApplianceUseRateValue, gbc_lApplianceUseRateValue);
		
		cbRandApplianceUseRate = new JCheckBox("Randomize");
		GridBagConstraints gbc_cbRandApplianceUseRate = new GridBagConstraints();
		gbc_cbRandApplianceUseRate.insets = new Insets(0, 0, 5, 0);
		gbc_cbRandApplianceUseRate.gridx = 3;
		gbc_cbRandApplianceUseRate.gridy = 4;
		pAppliances.add(cbRandApplianceUseRate, gbc_cbRandApplianceUseRate);
		
		JLabel lApplianceUpdateFrequency = new JLabel("Update Frequency (min):");
		GridBagConstraints gbc_lApplianceUpdateFrequency = new GridBagConstraints();
		gbc_lApplianceUpdateFrequency.anchor = GridBagConstraints.EAST;
		gbc_lApplianceUpdateFrequency.insets = new Insets(0, 0, 5, 5);
		gbc_lApplianceUpdateFrequency.gridx = 0;
		gbc_lApplianceUpdateFrequency.gridy = 5;
		pAppliances.add(lApplianceUpdateFrequency, gbc_lApplianceUpdateFrequency);
		
		JPanel pApplianceUpdateTime = new JPanel();
		GridBagConstraints gbc_pApplianceUpdateTime = new GridBagConstraints();
		gbc_pApplianceUpdateTime.insets = new Insets(0, 0, 5, 5);
		gbc_pApplianceUpdateTime.fill = GridBagConstraints.BOTH;
		gbc_pApplianceUpdateTime.gridx = 1;
		gbc_pApplianceUpdateTime.gridy = 5;
		pAppliances.add(pApplianceUpdateTime, gbc_pApplianceUpdateTime);
		
		spApplianceUpdateMinTime = new JSpinner();
		spApplianceUpdateMinTime.setModel(new SpinnerNumberModel(new Float(0.0f), new Float(0.0f), new Float(120.0f), new Float(0.5f)));
		spApplianceUpdateMinTime.setEnabled(false);
		pApplianceUpdateTime.add(spApplianceUpdateMinTime);
		
		spApplianceUpdateMaxTime = new JSpinner();
		spApplianceUpdateMaxTime.setModel(new SpinnerNumberModel(new Float(5.0f), new Float(0.0f), new Float(120.0f), new Float(0.5f)));
		pApplianceUpdateTime.add(spApplianceUpdateMaxTime);
		
		cbRangeApplianceUpdateTime = new JCheckBox("Range");
		GridBagConstraints gbc_cbRangeApplianceUpdateTime = new GridBagConstraints();
		gbc_cbRangeApplianceUpdateTime.insets = new Insets(0, 0, 5, 0);
		gbc_cbRangeApplianceUpdateTime.anchor = GridBagConstraints.WEST;
		gbc_cbRangeApplianceUpdateTime.gridx = 3;
		gbc_cbRangeApplianceUpdateTime.gridy = 5;
		pAppliances.add(cbRangeApplianceUpdateTime, gbc_cbRangeApplianceUpdateTime);
		
		// HOMES FORM
		JPanel pHome = new JPanel();
		pBody.add(pHome);
		GridBagLayout gbl_pHome = new GridBagLayout();
		gbl_pHome.columnWidths = new int[]{0, 0};
		gbl_pHome.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_pHome.columnWeights = new double[]{1.0, 1.0};
		gbl_pHome.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		pHome.setLayout(gbl_pHome);
		
		JLabel lHomeTitle = new JLabel("Home Agent Parameters");
		lHomeTitle.setFont(new Font("Calibri", Font.PLAIN, 24));
		GridBagConstraints gbc_lHomeTitle = new GridBagConstraints();
		gbc_lHomeTitle.gridwidth = 4;
		gbc_lHomeTitle.insets = new Insets(0, 0, 5, 0);
		gbc_lHomeTitle.gridx = 0;
		gbc_lHomeTitle.gridy = 0;
		pHome.add(lHomeTitle, gbc_lHomeTitle);
		
		JLabel lHomeName = new JLabel("Name:");
		GridBagConstraints gbc_lHomeName = new GridBagConstraints();
		gbc_lHomeName.insets = new Insets(0, 0, 5, 5);
		gbc_lHomeName.anchor = GridBagConstraints.EAST;
		gbc_lHomeName.gridx = 0;
		gbc_lHomeName.gridy = 2;
		pHome.add(lHomeName, gbc_lHomeName);
		
		fHomeName = new JTextField();
		fHomeName.setText("HomeAgent");
		GridBagConstraints gbc_fHomeName = new GridBagConstraints();
		gbc_fHomeName.insets = new Insets(0, 0, 5, 5);
		gbc_fHomeName.fill = GridBagConstraints.HORIZONTAL;
		gbc_fHomeName.gridx = 1;
		gbc_fHomeName.gridy = 2;
		pHome.add(fHomeName, gbc_fHomeName);
		fHomeName.setColumns(10);
		
		JLabel lHomeGenRate = new JLabel("Generation Rate:");
		GridBagConstraints gbc_lHomeGenRate = new GridBagConstraints();
		gbc_lHomeGenRate.anchor = GridBagConstraints.EAST;
		gbc_lHomeGenRate.insets = new Insets(0, 0, 5, 5);
		gbc_lHomeGenRate.gridx = 0;
		gbc_lHomeGenRate.gridy = 3;
		pHome.add(lHomeGenRate, gbc_lHomeGenRate);
		
		sHomeGenRate = new JSlider();
		sHomeGenRate.setMajorTickSpacing(10);
		sHomeGenRate.setPaintTicks(true);
		GridBagConstraints gbc_sHomeGenRate = new GridBagConstraints();
		gbc_sHomeGenRate.fill = GridBagConstraints.HORIZONTAL;
		gbc_sHomeGenRate.insets = new Insets(0, 0, 5, 5);
		gbc_sHomeGenRate.gridx = 1;
		gbc_sHomeGenRate.gridy = 3;
		pHome.add(sHomeGenRate, gbc_sHomeGenRate);
		
		JLabel lHomeGenValue = new JLabel("genValue");
		lHomeGenValue.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lHomeGenValue = new GridBagConstraints();
		gbc_lHomeGenValue.insets = new Insets(0, 0, 5, 5);
		gbc_lHomeGenValue.gridx = 2;
		gbc_lHomeGenValue.gridy = 3;
		
		pHome.add(lHomeGenValue, gbc_lHomeGenValue);
		lHomeGenValue.setText(Integer.toString(sHomeGenRate.getValue()));
		
		JCheckBox cbRandHomeGenRate = new JCheckBox("Randomize");
		GridBagConstraints gbc_cbRandHomeGenRate = new GridBagConstraints();
		gbc_cbRandHomeGenRate.insets = new Insets(0, 0, 5, 0);
		gbc_cbRandHomeGenRate.gridx = 3;
		gbc_cbRandHomeGenRate.gridy = 3;
		pHome.add(cbRandHomeGenRate, gbc_cbRandHomeGenRate);
		
		JLabel lHomeUseRate = new JLabel("Usage Rate:");
		GridBagConstraints gbc_lHomeUseRate = new GridBagConstraints();
		gbc_lHomeUseRate.anchor = GridBagConstraints.EAST;
		gbc_lHomeUseRate.insets = new Insets(0, 0, 5, 5);
		gbc_lHomeUseRate.gridx = 0;
		gbc_lHomeUseRate.gridy = 4;
		pHome.add(lHomeUseRate, gbc_lHomeUseRate);
		
		sHomeUseRate = new JSlider();
		sHomeUseRate.setMajorTickSpacing(10);
		sHomeUseRate.setValue(55);
		sHomeUseRate.setMinimum(10);
		sHomeUseRate.setPaintTicks(true);
		GridBagConstraints gbc_sHomeUseRate = new GridBagConstraints();
		gbc_sHomeUseRate.fill = GridBagConstraints.HORIZONTAL;
		gbc_sHomeUseRate.insets = new Insets(0, 0, 5, 5);
		gbc_sHomeUseRate.gridx = 1;
		gbc_sHomeUseRate.gridy = 4;
		pHome.add(sHomeUseRate, gbc_sHomeUseRate);
		
		JLabel lHomeUseValue = new JLabel();
		lHomeUseValue.setText(Integer.toString(sHomeUseRate.getValue()));
		GridBagConstraints gbc_lHomeUseValue = new GridBagConstraints();
		gbc_lHomeUseValue.insets = new Insets(0, 0, 5, 5);
		gbc_lHomeUseValue.gridx = 2;
		gbc_lHomeUseValue.gridy = 4;
		pHome.add(lHomeUseValue, gbc_lHomeUseValue);
		
		JCheckBox cbRandHomeUseRate = new JCheckBox("Randomize");
		GridBagConstraints gbc_cbRandHomeUseRate = new GridBagConstraints();
		gbc_cbRandHomeUseRate.insets = new Insets(0, 0, 5, 0);
		gbc_cbRandHomeUseRate.gridx = 3;
		gbc_cbRandHomeUseRate.gridy = 4;
		pHome.add(cbRandHomeUseRate, gbc_cbRandHomeUseRate);
		
		JLabel lHomeSupply = new JLabel("Supply:");
		GridBagConstraints gbc_lHomeSupply = new GridBagConstraints();
		gbc_lHomeSupply.anchor = GridBagConstraints.EAST;
		gbc_lHomeSupply.insets = new Insets(0, 0, 5, 5);
		gbc_lHomeSupply.gridx = 0;
		gbc_lHomeSupply.gridy = 5;
		pHome.add(lHomeSupply, gbc_lHomeSupply);
		
		sHomeSupply = new JSlider();
		sHomeSupply.setMaximum(2000);
		sHomeSupply.setPaintTicks(true);
		sHomeSupply.setValue(1000);
		sHomeSupply.setMajorTickSpacing(100);
		GridBagConstraints gbc_sHomeSupply = new GridBagConstraints();
		gbc_sHomeSupply.fill = GridBagConstraints.HORIZONTAL;
		gbc_sHomeSupply.insets = new Insets(0, 0, 5, 5);
		gbc_sHomeSupply.gridx = 1;
		gbc_sHomeSupply.gridy = 5;
		pHome.add(sHomeSupply, gbc_sHomeSupply);
		
		JLabel lHomeSupplyValue = new JLabel();
		lHomeSupplyValue.setText(Integer.toString(sHomeSupply.getValue()));
		GridBagConstraints gbc_lHomeSupplyValue = new GridBagConstraints();
		gbc_lHomeSupplyValue.insets = new Insets(0, 0, 5, 5);
		gbc_lHomeSupplyValue.gridx = 2;
		gbc_lHomeSupplyValue.gridy = 5;
		pHome.add(lHomeSupplyValue, gbc_lHomeSupplyValue);
		
		JCheckBox cbRandHomeSupply = new JCheckBox("Randomize");
		GridBagConstraints gbc_cbRandHomeSupply = new GridBagConstraints();
		gbc_cbRandHomeSupply.insets = new Insets(0, 0, 5, 0);
		gbc_cbRandHomeSupply.gridx = 3;
		gbc_cbRandHomeSupply.gridy = 5;
		pHome.add(cbRandHomeSupply, gbc_cbRandHomeSupply);
		
		JLabel lHomeIncome = new JLabel("Income:");
		GridBagConstraints gbc_lHomeIncome = new GridBagConstraints();
		gbc_lHomeIncome.anchor = GridBagConstraints.EAST;
		gbc_lHomeIncome.insets = new Insets(0, 0, 5, 5);
		gbc_lHomeIncome.gridx = 0;
		gbc_lHomeIncome.gridy = 6;
		pHome.add(lHomeIncome, gbc_lHomeIncome);
		
		sHomeIncome = new JSlider();
		sHomeIncome.setMinimum(10);
		sHomeIncome.setValue(55);
		sHomeIncome.setMajorTickSpacing(10);
		sHomeIncome.setPaintTicks(true);
		GridBagConstraints gbc_sHomeIncome = new GridBagConstraints();
		gbc_sHomeIncome.fill = GridBagConstraints.HORIZONTAL;
		gbc_sHomeIncome.insets = new Insets(0, 0, 5, 5);
		gbc_sHomeIncome.gridx = 1;
		gbc_sHomeIncome.gridy = 6;
		pHome.add(sHomeIncome, gbc_sHomeIncome);
		
		JLabel lHomeIncomeValue = new JLabel("");
		lHomeIncomeValue.setText(Integer.toString(sHomeIncome.getValue()));
		GridBagConstraints gbc_lHomeIncomeValue = new GridBagConstraints();
		gbc_lHomeIncomeValue.insets = new Insets(0, 0, 5, 5);
		gbc_lHomeIncomeValue.gridx = 2;
		gbc_lHomeIncomeValue.gridy = 6;
		pHome.add(lHomeIncomeValue, gbc_lHomeIncomeValue);
				
		JCheckBox cbRandHomeIncome = new JCheckBox("Randomize");
		GridBagConstraints gbc_cbRandHomeIncome = new GridBagConstraints();
		gbc_cbRandHomeIncome.insets = new Insets(0, 0, 5, 0);
		gbc_cbRandHomeIncome.gridx = 3;
		gbc_cbRandHomeIncome.gridy = 6;
		pHome.add(cbRandHomeIncome, gbc_cbRandHomeIncome);
		
		// RETAILERS FORM
		JPanel pRetailers = new JPanel();
		pBody.add(pRetailers);
		GridBagLayout gbl_pRetailers = new GridBagLayout();
		gbl_pRetailers.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_pRetailers.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_pRetailers.columnWeights = new double[]{1.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_pRetailers.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		pRetailers.setLayout(gbl_pRetailers);
		
		JLabel lRetailerTitle = new JLabel("Retailer Agents Parameters");
		lRetailerTitle.setFont(new Font("Calibri", Font.PLAIN, 24));
		GridBagConstraints gbc_lRetailerTitle = new GridBagConstraints();
		gbc_lRetailerTitle.gridheight = 2;
		gbc_lRetailerTitle.gridwidth = 4;
		gbc_lRetailerTitle.insets = new Insets(0, 0, 5, 0);
		gbc_lRetailerTitle.gridx = 0;
		gbc_lRetailerTitle.gridy = 0;
		pRetailers.add(lRetailerTitle, gbc_lRetailerTitle);
		
		JLabel lRetailerSelect = new JLabel("Retailer:");
		GridBagConstraints gbc_lRetailerSelect = new GridBagConstraints();
		gbc_lRetailerSelect.anchor = GridBagConstraints.EAST;
		gbc_lRetailerSelect.insets = new Insets(0, 0, 5, 5);
		gbc_lRetailerSelect.gridx = 0;
		gbc_lRetailerSelect.gridy = 2;
		pRetailers.add(lRetailerSelect, gbc_lRetailerSelect);
		
		JPanel pRetailerSelect = new JPanel();
		FlowLayout flRetailerSelect = (FlowLayout) pRetailerSelect.getLayout();
		flRetailerSelect.setVgap(0);
		flRetailerSelect.setHgap(0);
		GridBagConstraints gbc_pRetailerSelect = new GridBagConstraints();
		gbc_pRetailerSelect.fill = GridBagConstraints.BOTH;
		gbc_pRetailerSelect.insets = new Insets(0, 0, 5, 5);
		gbc_pRetailerSelect.gridx = 1;
		gbc_pRetailerSelect.gridy = 2;
		pRetailers.add(pRetailerSelect, gbc_pRetailerSelect);
		
		rbR1 = new JRadioButton("1");
		rbR1.setSelected(true);
		pRetailerSelect.add(rbR1);
		
		rbR2 = new JRadioButton("2");
		pRetailerSelect.add(rbR2);
		
		rbR3 = new JRadioButton("3");
		pRetailerSelect.add(rbR3);
		
		ButtonGroup rbgRetailerSelect = new ButtonGroup();
		rbgRetailerSelect.add(rbR1);
		rbgRetailerSelect.add(rbR2);
		rbgRetailerSelect.add(rbR3);
		
		JLabel lRetailerName = new JLabel("Name:");
		GridBagConstraints gbc_lRetailerName = new GridBagConstraints();
		gbc_lRetailerName.anchor = GridBagConstraints.EAST;
		gbc_lRetailerName.insets = new Insets(0, 0, 5, 5);
		gbc_lRetailerName.gridx = 0;
		gbc_lRetailerName.gridy = 3;
		pRetailers.add(lRetailerName, gbc_lRetailerName);
		
		fRetailerName = new JTextField();
		fRetailerName.setText("R1");
		fRetailerName.setColumns(10);
		GridBagConstraints gbc_fRetailerName = new GridBagConstraints();
		gbc_fRetailerName.fill = GridBagConstraints.HORIZONTAL;
		gbc_fRetailerName.insets = new Insets(0, 0, 5, 5);
		gbc_fRetailerName.gridx = 1;
		gbc_fRetailerName.gridy = 3;
		pRetailers.add(fRetailerName, gbc_fRetailerName);
		
		JLabel lRetailerGenRate = new JLabel("Generation Rate:");
		GridBagConstraints gbc_lRetailerGenRate = new GridBagConstraints();
		gbc_lRetailerGenRate.anchor = GridBagConstraints.EAST;
		gbc_lRetailerGenRate.insets = new Insets(0, 0, 5, 5);
		gbc_lRetailerGenRate.gridx = 0;
		gbc_lRetailerGenRate.gridy = 4;
		pRetailers.add(lRetailerGenRate, gbc_lRetailerGenRate);
		
		sRetailerGenRate = new JSlider();
		sRetailerGenRate.setPaintTicks(true);
		sRetailerGenRate.setMajorTickSpacing(10);
		GridBagConstraints gbc_sRetailerGenRate = new GridBagConstraints();
		gbc_sRetailerGenRate.fill = GridBagConstraints.HORIZONTAL;
		gbc_sRetailerGenRate.insets = new Insets(0, 0, 5, 5);
		gbc_sRetailerGenRate.gridx = 1;
		gbc_sRetailerGenRate.gridy = 4;
		pRetailers.add(sRetailerGenRate, gbc_sRetailerGenRate);
		
		lRetailerGenRateValue = new JLabel("50");
		lRetailerGenRate.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lRetailerGenRateValue = new GridBagConstraints();
		gbc_lRetailerGenRateValue.insets = new Insets(0, 0, 5, 5);
		gbc_lRetailerGenRateValue.gridx = 2;
		gbc_lRetailerGenRateValue.gridy = 4;
		pRetailers.add(lRetailerGenRateValue, gbc_lRetailerGenRateValue);
		
		cbRandRetailerGenRate = new JCheckBox("Randomize");
		GridBagConstraints gbc_cbRandRetailerGenRate = new GridBagConstraints();
		gbc_cbRandRetailerGenRate.insets = new Insets(0, 0, 5, 0);
		gbc_cbRandRetailerGenRate.gridx = 3;
		gbc_cbRandRetailerGenRate.gridy = 4;
		pRetailers.add(cbRandRetailerGenRate, gbc_cbRandRetailerGenRate);
		
		JLabel lRetailerPrice = new JLabel("Price:");
		GridBagConstraints gbc_lRetailerPrice = new GridBagConstraints();
		gbc_lRetailerPrice.anchor = GridBagConstraints.EAST;
		gbc_lRetailerPrice.insets = new Insets(0, 0, 5, 5);
		gbc_lRetailerPrice.gridx = 0;
		gbc_lRetailerPrice.gridy = 5;
		pRetailers.add(lRetailerPrice, gbc_lRetailerPrice);
		
		sRetailerPrice = new JSlider();
		sRetailerPrice.setValue(55);
		sRetailerPrice.setPaintTicks(true);
		sRetailerPrice.setMinimum(10);
		sRetailerPrice.setMajorTickSpacing(10);
		GridBagConstraints gbc_sRetailerPrice = new GridBagConstraints();
		gbc_sRetailerPrice.fill = GridBagConstraints.HORIZONTAL;
		gbc_sRetailerPrice.insets = new Insets(0, 0, 5, 5);
		gbc_sRetailerPrice.gridx = 1;
		gbc_sRetailerPrice.gridy = 5;
		pRetailers.add(sRetailerPrice, gbc_sRetailerPrice);
		
		lRetailerPriceValue = new JLabel("55");
		GridBagConstraints gbc_lRetailerPriceValue = new GridBagConstraints();
		gbc_lRetailerPriceValue.insets = new Insets(0, 0, 5, 5);
		gbc_lRetailerPriceValue.gridx = 2;
		gbc_lRetailerPriceValue.gridy = 5;
		pRetailers.add(lRetailerPriceValue, gbc_lRetailerPriceValue);
		
		cbRandRetailerPrice = new JCheckBox("Randomize");
		GridBagConstraints gbc_cbRandRetailerPrice = new GridBagConstraints();
		gbc_cbRandRetailerPrice.insets = new Insets(0, 0, 5, 0);
		gbc_cbRandRetailerPrice.gridx = 3;
		gbc_cbRandRetailerPrice.gridy = 5;
		pRetailers.add(cbRandRetailerPrice, gbc_cbRandRetailerPrice);
		
		JLabel lRetailerSupply = new JLabel("Supply:");
		GridBagConstraints gbc_lRetailerSupply = new GridBagConstraints();
		gbc_lRetailerSupply.anchor = GridBagConstraints.EAST;
		gbc_lRetailerSupply.insets = new Insets(0, 0, 5, 5);
		gbc_lRetailerSupply.gridx = 0;
		gbc_lRetailerSupply.gridy = 6;
		pRetailers.add(lRetailerSupply, gbc_lRetailerSupply);
		
		sRetailerSupply = new JSlider();
		sRetailerSupply.setMaximum(5000);
		sRetailerSupply.setValue(2500);
		sRetailerSupply.setPaintTicks(true);
		sRetailerSupply.setMajorTickSpacing(500);
		GridBagConstraints gbc_sRetailerSupply = new GridBagConstraints();
		gbc_sRetailerSupply.fill = GridBagConstraints.HORIZONTAL;
		gbc_sRetailerSupply.insets = new Insets(0, 0, 5, 5);
		gbc_sRetailerSupply.gridx = 1;
		gbc_sRetailerSupply.gridy = 6;
		pRetailers.add(sRetailerSupply, gbc_sRetailerSupply);
		
		lRetailerSupplyValue = new JLabel("2500");
		GridBagConstraints gbc_lRetailerSupplyValue = new GridBagConstraints();
		gbc_lRetailerSupplyValue.insets = new Insets(0, 0, 5, 5);
		gbc_lRetailerSupplyValue.gridx = 2;
		gbc_lRetailerSupplyValue.gridy = 6;
		pRetailers.add(lRetailerSupplyValue, gbc_lRetailerSupplyValue);
		
		cbRandRetailerSupply = new JCheckBox("Randomize");
		GridBagConstraints gbc_cbRandRetailerSupply = new GridBagConstraints();
		gbc_cbRandRetailerSupply.insets = new Insets(0, 0, 5, 0);
		gbc_cbRandRetailerSupply.gridx = 3;
		gbc_cbRandRetailerSupply.gridy = 6;
		pRetailers.add(cbRandRetailerSupply, gbc_cbRandRetailerSupply);
		
		JLabel lHomeTradeFrequency = new JLabel("Trading Frequency (min):");
		GridBagConstraints gbc_lHomeTradeFrequency = new GridBagConstraints();
		gbc_lHomeTradeFrequency.anchor = GridBagConstraints.EAST;
		gbc_lHomeTradeFrequency.insets = new Insets(0, 0, 5, 5);
		gbc_lHomeTradeFrequency.gridx = 0;
		gbc_lHomeTradeFrequency.gridy = 7;
		pHome.add(lHomeTradeFrequency, gbc_lHomeTradeFrequency);
		
		JPanel pHomeTradeTime = new JPanel();
		GridBagConstraints gbc_pHomeTradeTime = new GridBagConstraints();
		gbc_pHomeTradeTime.fill = GridBagConstraints.BOTH;
		gbc_pHomeTradeTime.insets = new Insets(0, 0, 5, 5);
		gbc_pHomeTradeTime.gridx = 1;
		gbc_pHomeTradeTime.gridy = 7;
		pHome.add(pHomeTradeTime, gbc_pHomeTradeTime);
		
		spHomeTradeMinTime = new JSpinner();
		spHomeTradeMinTime.setModel(new SpinnerNumberModel(new Float(0.0f), new Float(0.0f), new Float(120.0f), new Float(0.5f)));
		spHomeTradeMinTime.setEnabled(false);
		pHomeTradeTime.add(spHomeTradeMinTime);
		
		spHomeTradeMaxTime = new JSpinner();
		spHomeTradeMaxTime.setModel(new SpinnerNumberModel(new Float(5.0f), new Float(0.0f), new Float(120.0f), new Float(0.5f)));
		pHomeTradeTime.add(spHomeTradeMaxTime);
		
		cbRangeHomeTradeTime = new JCheckBox("Range");
		GridBagConstraints gbc_cbRangeHomeTradeTime = new GridBagConstraints();
		gbc_cbRangeHomeTradeTime.anchor = GridBagConstraints.WEST;
		gbc_cbRangeHomeTradeTime.insets = new Insets(0, 0, 5, 0);
		gbc_cbRangeHomeTradeTime.gridx = 3;
		gbc_cbRangeHomeTradeTime.gridy = 7;
		pHome.add(cbRangeHomeTradeTime, gbc_cbRangeHomeTradeTime);
		
		JLabel lHomeUpdateFrequency = new JLabel("Update Frequency (min):");
		GridBagConstraints gbc_lHomeUpdateFrequency = new GridBagConstraints();
		gbc_lHomeUpdateFrequency.anchor = GridBagConstraints.EAST;
		gbc_lHomeUpdateFrequency.insets = new Insets(0, 0, 0, 5);
		gbc_lHomeUpdateFrequency.gridx = 0;
		gbc_lHomeUpdateFrequency.gridy = 8;
		pHome.add(lHomeUpdateFrequency, gbc_lHomeUpdateFrequency);
		
		JPanel pHomeUpdateTime = new JPanel();
		GridBagConstraints gbc_pHomeUpdateTime = new GridBagConstraints();
		gbc_pHomeUpdateTime.insets = new Insets(0, 0, 0, 5);
		gbc_pHomeUpdateTime.fill = GridBagConstraints.BOTH;
		gbc_pHomeUpdateTime.gridx = 1;
		gbc_pHomeUpdateTime.gridy = 8;
		pHome.add(pHomeUpdateTime, gbc_pHomeUpdateTime);
		
		spHomeUpdateMinTime = new JSpinner();
		spHomeUpdateMinTime.setModel(new SpinnerNumberModel(new Float(0.0f), new Float(0.0f), new Float(120.0f), new Float(0.5f)));
		spHomeUpdateMinTime.setEnabled(false);
		pHomeUpdateTime.add(spHomeUpdateMinTime);
		
		spHomeUpdateMaxTime = new JSpinner();
		spHomeUpdateMaxTime.setModel(new SpinnerNumberModel(new Float(5.0f), new Float(0.0f), new Float(120.0f), new Float(0.5f)));
		pHomeUpdateTime.add(spHomeUpdateMaxTime);
		
		cbRangeHomeUpdateTime = new JCheckBox("Range");
		GridBagConstraints gbc_cbRangeHomeUpdateTime = new GridBagConstraints();
		gbc_cbRangeHomeUpdateTime.anchor = GridBagConstraints.WEST;
		gbc_cbRangeHomeUpdateTime.gridx = 3;
		gbc_cbRangeHomeUpdateTime.gridy = 8;
		pHome.add(cbRangeHomeUpdateTime, gbc_cbRangeHomeUpdateTime);
		
		JLabel lblType = new JLabel("Type:");
		GridBagConstraints gbc_lblType = new GridBagConstraints();
		gbc_lblType.insets = new Insets(0, 0, 5, 5);
		gbc_lblType.anchor = GridBagConstraints.EAST;
		gbc_lblType.gridx = 0;
		gbc_lblType.gridy = 7;
		pRetailers.add(lblType, gbc_lblType);
		
		ddRetailerType = new JComboBox<Retailer.RetailerType>();
		GridBagConstraints gbc_ddRetailerType = new GridBagConstraints();
		gbc_ddRetailerType.insets = new Insets(0, 0, 5, 5);
		gbc_ddRetailerType.fill = GridBagConstraints.HORIZONTAL;
		gbc_ddRetailerType.gridx = 1;
		gbc_ddRetailerType.gridy = 7;
		pRetailers.add(ddRetailerType, gbc_ddRetailerType);
		
		JLabel lRetailerUpdateFrequency = new JLabel("Update Frequency (min):");
		GridBagConstraints gbc_lRetailerUpdateFrequency = new GridBagConstraints();
		gbc_lRetailerUpdateFrequency.anchor = GridBagConstraints.EAST;
		gbc_lRetailerUpdateFrequency.insets = new Insets(0, 0, 5, 5);
		gbc_lRetailerUpdateFrequency.gridx = 0;
		gbc_lRetailerUpdateFrequency.gridy = 8;
		pRetailers.add(lRetailerUpdateFrequency, gbc_lRetailerUpdateFrequency);
		
		JPanel pRetailerUpdateTime = new JPanel();
		GridBagConstraints gbc_pRetailerUpdateTime = new GridBagConstraints();
		gbc_pRetailerUpdateTime.insets = new Insets(0, 0, 5, 5);
		gbc_pRetailerUpdateTime.fill = GridBagConstraints.BOTH;
		gbc_pRetailerUpdateTime.gridx = 1;
		gbc_pRetailerUpdateTime.gridy = 8;
		pRetailers.add(pRetailerUpdateTime, gbc_pRetailerUpdateTime);
		
		spRetailerUpdateMinTime = new JSpinner();
		spRetailerUpdateMinTime.setModel(new SpinnerNumberModel(new Float(0.0f), new Float(0.0f), new Float(120.0f), new Float(0.5f)));
		spRetailerUpdateMinTime.setEnabled(false);
		pRetailerUpdateTime.add(spRetailerUpdateMinTime);
		
		spRetailerUpdateMaxTime = new JSpinner();
		spRetailerUpdateMaxTime.setModel(new SpinnerNumberModel(new Float(5.0f), new Float(0.0f), new Float(120.0f), new Float(0.5f)));
		pRetailerUpdateTime.add(spRetailerUpdateMaxTime);
		
		cbRangeRetailerUpdateTime = new JCheckBox("Range");
		GridBagConstraints gbc_cbRangeRetailerUpdateTime = new GridBagConstraints();
		gbc_cbRangeRetailerUpdateTime.insets = new Insets(0, 0, 5, 0);
		gbc_cbRangeRetailerUpdateTime.anchor = GridBagConstraints.WEST;
		gbc_cbRangeRetailerUpdateTime.gridx = 3;
		gbc_cbRangeRetailerUpdateTime.gridy = 8;
		pRetailers.add(cbRangeRetailerUpdateTime, gbc_cbRangeRetailerUpdateTime);
		
		// Setup default retailer settings
		for(int i = 0; i < 3; i++) {
			aRetailerNames[i] = "R"+(i+1);
			aRetailerTypes[i] = RetailerType.FIXED;
			aRetailerGenRates[i] = sRetailerGenRate.getValue();
			aRetailerPrices[i] = sRetailerPrice.getValue();
			aRetailerSupplies[i] = sRetailerSupply.getValue();
			aRetailerUpdateRangeCB[i] = false;
			aRetailerUpdateMinTimes[i] = (float)spRetailerUpdateMinTime.getValue();
			aRetailerUpdateMaxTimes[i] = (float)spRetailerUpdateMaxTime.getValue();
			
			aApplianceNames[i] = "A"+(i+1);
			aApplianceGenRates[i] = sApplianceGenRate.getValue();
			aApplianceUseRates[i] = sApplianceUseRate.getValue();
			aApplianceUpdateRangeCB[i] = false;
			aApplianceUpdateMinTimes[i] = (float)spApplianceUpdateMinTime.getValue();
			aApplianceUpdateMaxTimes[i] = (float)spApplianceUpdateMaxTime.getValue();
		}
		
		// Handle Slider events
		sHomeIncome.addChangeListener(new SliderListener(lHomeIncomeValue));
		sHomeSupply.addChangeListener(new SliderListener(lHomeSupplyValue));
		sHomeGenRate.addChangeListener(new SliderListener(lHomeGenValue));
		sHomeUseRate.addChangeListener(new SliderListener(lHomeUseValue));
		
		slRetailerGenRate = new SliderListener(lRetailerGenRateValue);
		slRetailerPrice = new SliderListener(lRetailerPriceValue);
		slRetailerSupply = new SliderListener(lRetailerSupplyValue);
		
		sRetailerGenRate.addChangeListener(slRetailerGenRate);
		sRetailerPrice.addChangeListener(slRetailerPrice);
		sRetailerSupply.addChangeListener(slRetailerSupply);
		
		slApplianceGenRate = new SliderListener(lApplianceGenRateValue);
		slApplianceUseRate = new SliderListener(lApplianceUseRateValue);
		
		sApplianceGenRate.addChangeListener(slApplianceGenRate);
		sApplianceUseRate.addChangeListener(slApplianceUseRate);
		
		// Handle CheckBox events
		cbRandHomeUseRate.addChangeListener(new CheckBoxListener(lHomeUseValue, sHomeUseRate));
		cbRandHomeGenRate.addChangeListener(new CheckBoxListener(lHomeGenValue, sHomeGenRate));
		cbRandHomeIncome.addChangeListener(new CheckBoxListener(lHomeIncomeValue, sHomeIncome));
		cbRandHomeSupply.addChangeListener(new CheckBoxListener(lHomeSupplyValue, sHomeSupply));
		cbRangeHomeTradeTime.addChangeListener(new RangeCheckBoxListener(spHomeTradeMinTime));
		cbRangeHomeUpdateTime.addChangeListener(new RangeCheckBoxListener(spHomeUpdateMinTime));
		
		cblRetailerGenRate = new CheckBoxListener(lRetailerGenRateValue, sRetailerGenRate);
		cblRetailerPrice = new CheckBoxListener(lRetailerPriceValue, sRetailerPrice);
		cblRetailerSupply = new CheckBoxListener(lRetailerSupplyValue, sRetailerSupply);

		cbRandRetailerGenRate.addChangeListener(cblRetailerGenRate);
		cbRandRetailerPrice.addChangeListener(cblRetailerPrice);
		cbRandRetailerSupply.addChangeListener(cblRetailerSupply);
		cbRangeRetailerUpdateTime.addChangeListener(new RangeCheckBoxListener(spRetailerUpdateMinTime));
		
		cblApplianceGenRate = new CheckBoxListener(lApplianceGenRateValue, sApplianceGenRate);
		cblApplianceUseRate = new CheckBoxListener(lApplianceUseRateValue, sApplianceUseRate);
		
		cbRandApplianceGenRate.addChangeListener(cblApplianceGenRate);
		cbRandApplianceUseRate.addChangeListener(cblApplianceUseRate);
		cbRangeApplianceUpdateTime.addChangeListener(new RangeCheckBoxListener(spApplianceUpdateMinTime));
		
		// Handle RadioButton events
		rbR1.addActionListener(new RetailerRadioButtonListener(0));
		rbR2.addActionListener(new RetailerRadioButtonListener(1));
		rbR3.addActionListener(new RetailerRadioButtonListener(2));
		
		rbA1.addActionListener(new ApplianceRadioButtonListener(0));
		rbA2.addActionListener(new ApplianceRadioButtonListener(1));
		rbA3.addActionListener(new ApplianceRadioButtonListener(2));
		
		// Handle Spinner events
		spApplianceUpdateMinTime.addChangeListener(new RangeSpinnerListener(spApplianceUpdateMaxTime, true));
		spHomeTradeMinTime.addChangeListener(new RangeSpinnerListener(spHomeTradeMaxTime, true));
		spHomeUpdateMinTime.addChangeListener(new RangeSpinnerListener(spHomeUpdateMaxTime, true));
		spRetailerUpdateMinTime.addChangeListener(new RangeSpinnerListener(spRetailerUpdateMaxTime, true));
		
		spApplianceUpdateMaxTime.addChangeListener(new RangeSpinnerListener(spApplianceUpdateMaxTime, false));
		spHomeTradeMaxTime.addChangeListener(new RangeSpinnerListener(spHomeTradeMinTime, false));
		spHomeUpdateMaxTime.addChangeListener(new RangeSpinnerListener(spHomeUpdateMinTime, false));
		spRetailerUpdateMaxTime.addChangeListener(new RangeSpinnerListener(spRetailerUpdateMinTime, false));
		
		
		for (RetailerType type : RetailerType.values()) {
			ddRetailerType.addItem(type);
		}
		
		JPanel pSetup = new JPanel();
		getContentPane().add(pSetup, BorderLayout.SOUTH);
		
		JButton bStart = new JButton("Run Configuration");
		pSetup.add(bStart);
		
		bStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				initalizeJade(false);
				setVisible(false);
				ProgramGUI.getInstance().setVisible(true);
			}
		});
		
		pack();
	} 
	
	// Random slider value
	int randomSliderValue(JSlider slider) {
		return (rnd.nextInt(slider.getMaximum()) + slider.getMinimum()) % slider.getMaximum(); 
	}
	
	float randomSpinnerValue(JSpinner sMin, JSpinner sMax) { 
		float min = (Float)sMin.getValue();
		float max = (Float)sMax.getValue();
		return (rnd.nextFloat() * (max - min) + min);
	}
	
	// Custom JSlider listener
	public class SliderListener implements ChangeListener {
		private JLabel label;
		
		SliderListener(JLabel label) {
			super();
			this.label = label;
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();
			label.setText(Integer.toString(source.getValue()));
		}
	}

	// Custom JCheckBox listener
	public class CheckBoxListener implements ChangeListener {
		private JSlider slider;
		private JLabel label;
		
		CheckBoxListener(JLabel label, JSlider slider) {
			super();
			this.label = label;
			this.slider = slider;
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			JCheckBox source = (JCheckBox)e.getSource();
			if (source.isSelected()) {
				slider.setEnabled(false);
				label.setText("??");
			} else {
				slider.setEnabled(true);
				label.setText(Integer.toString(slider.getValue()));
			}
		}
	}
	
	// Custom JCheckBox listener for 'range' style checkboxes
	public class RangeCheckBoxListener implements ChangeListener {
		private JSpinner spinner;
		
		RangeCheckBoxListener(JSpinner spinner) {
			this.spinner = spinner;
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			JCheckBox source = (JCheckBox)e.getSource();
			if(source.isSelected()) {
				spinner.setEnabled(true);
			} else {
				spinner.setEnabled(false);
			}
		}
	}
	
	// Custom JSpinner listener
	public class RangeSpinnerListener implements ChangeListener {
		private JSpinner spinner;
		private boolean max = false;
		
		RangeSpinnerListener(JSpinner spinner, boolean max) {
			this.spinner = spinner;
			this.max = max;
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			float cap = (float)spinner.getValue();
			JSpinner source = (JSpinner)e.getSource();
			
			if(max) {
				if((float)source.getValue() > (float)spinner.getValue()) {
					source.setValue(cap);
				}
			} else {
				if((float)source.getValue() < (float)spinner.getValue()) {
					source.setValue(cap);
				}
			}
		}
	}
	
	// Custom JRadioButton listener for retailers
	public class RetailerRadioButtonListener implements ActionListener {
		private int retailer;
		
		RetailerRadioButtonListener(int retailer) {
			super();
			this.retailer = retailer;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JRadioButton source = (JRadioButton)e.getSource();
			if (source.isSelected() && retailer != selectedRetailer) {
				try {
					saveRetailer();
					selectedRetailer = retailer;
					
					// Update to new retailer values
					fRetailerName.setText(aRetailerNames[retailer]);
					ddRetailerType.setSelectedItem(aRetailerTypes[retailer]);
					sRetailerGenRate.setValue(aRetailerGenRates[retailer]);
					lRetailerGenRateValue.setText(Integer.toString(aRetailerGenRates[retailer]));
					sRetailerPrice.setValue(aRetailerPrices[retailer]);
					lRetailerPriceValue.setText(Integer.toString(aRetailerPrices[retailer]));
					sRetailerSupply.setValue(aRetailerSupplies[retailer]);
					lRetailerSupplyValue.setText(Integer.toString(aRetailerSupplies[retailer]));
					spRetailerUpdateMinTime.setValue(aRetailerUpdateMinTimes[retailer]);
					spRetailerUpdateMaxTime.setValue(aRetailerUpdateMaxTimes[retailer]);
					cbRangeRetailerUpdateTime.setSelected(aRetailerUpdateRangeCB[retailer]);
					
					// Update listeners
					slRetailerGenRate = new SliderListener(lRetailerGenRateValue);
					slRetailerPrice = new SliderListener(lRetailerPriceValue);
					slRetailerSupply = new SliderListener(lRetailerSupplyValue);
					
					cblRetailerGenRate = new CheckBoxListener(lRetailerGenRateValue, sRetailerGenRate);
					cblRetailerPrice = new CheckBoxListener(lRetailerPriceValue, sRetailerPrice);
					cblRetailerSupply = new CheckBoxListener(lRetailerSupplyValue, sRetailerSupply);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	// Custom JRadioButton listener for appliances
	public class ApplianceRadioButtonListener implements ActionListener {
		private int appliance;
		
		ApplianceRadioButtonListener(int appliance) {
			super();
			this.appliance = appliance;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JRadioButton source = (JRadioButton)e.getSource();
			if (source.isSelected() && appliance != selectedAppliance) {
				try {
					saveAppliance();
					selectedAppliance = appliance;
					
					// Update to new appliance values
					fApplianceName.setText(aApplianceNames[appliance]);
					sApplianceGenRate.setValue(aApplianceGenRates[appliance]);
					lApplianceGenRateValue.setText(Integer.toString(aApplianceGenRates[appliance]));
					sApplianceUseRate.setValue(aApplianceUseRates[appliance]);
					lApplianceUseRateValue.setText(Integer.toString(aApplianceUseRates[appliance]));
					spApplianceUpdateMinTime.setValue(aApplianceUpdateMinTimes[appliance]);
					spApplianceUpdateMaxTime.setValue(aApplianceUpdateMaxTimes[appliance]);
					cbRangeApplianceUpdateTime.setSelected(aApplianceUpdateRangeCB[appliance]);
					
					// Update listeners
					slApplianceGenRate = new SliderListener(lRetailerGenRateValue);
					cblApplianceGenRate = new CheckBoxListener(lRetailerGenRateValue, sRetailerGenRate);
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	private void saveAppliance() {
		aApplianceNames[selectedAppliance] = fApplianceName.getText();
		aApplianceUpdateMinTimes[selectedAppliance] = (float)spApplianceUpdateMinTime.getValue();
		aApplianceUpdateMaxTimes[selectedAppliance] = (float)spApplianceUpdateMaxTime.getValue();
		
		if(cbRandApplianceGenRate.isSelected()) {
			cbRandApplianceGenRate.setSelected(false);
			aApplianceGenRates[selectedAppliance] = randomSliderValue(sApplianceGenRate);
		} else {
			aApplianceGenRates[selectedAppliance] = sApplianceGenRate.getValue();
		}
		
		if(cbRandApplianceUseRate.isSelected()) {
			cbRandApplianceUseRate.setSelected(false);
			aApplianceUseRates[selectedAppliance] = randomSliderValue(sApplianceUseRate);
		} else {
			aApplianceUseRates[selectedAppliance] = sApplianceUseRate.getValue();
		}
		
		if (cbRangeApplianceUpdateTime.isSelected()) {
			cbRangeApplianceUpdateTime.setSelected(false);
			aApplianceUpdateRangeCB[selectedAppliance] = true;
		} else {
			aApplianceUpdateRangeCB[selectedAppliance] = false;
		}
	}
	
	private void saveRetailer() {
		aRetailerNames[selectedRetailer] = fRetailerName.getText();
		aRetailerTypes[selectedRetailer] = (RetailerType)ddRetailerType.getSelectedItem();
		aRetailerUpdateMinTimes[selectedRetailer] = (float)spRetailerUpdateMinTime.getValue();
		aRetailerUpdateMaxTimes[selectedRetailer] = (float)spRetailerUpdateMaxTime.getValue();
		
		if(cbRandRetailerGenRate.isSelected()) {
			cbRandRetailerGenRate.setSelected(false);
			aRetailerGenRates[selectedRetailer] = randomSliderValue(sRetailerGenRate);
		} else {
			aRetailerGenRates[selectedRetailer] = sRetailerGenRate.getValue();
		}
		
		if(cbRandRetailerPrice.isSelected()) {
			cbRandRetailerPrice.setSelected(false);
			aRetailerPrices[selectedRetailer] = randomSliderValue(sRetailerPrice);
		} else {
			
			aRetailerPrices[selectedRetailer] = sRetailerPrice.getValue();
		}
		
		if(cbRandRetailerSupply.isSelected()) {
			cbRandRetailerSupply.setSelected(false);
			aRetailerSupplies[selectedRetailer] = randomSliderValue(sRetailerSupply);
		} else {
			aRetailerSupplies[selectedRetailer] = sRetailerSupply.getValue();
		}
		
		if (cbRangeRetailerUpdateTime.isSelected()) {
			cbRangeRetailerUpdateTime.setSelected(false);
			aRetailerUpdateRangeCB[selectedRetailer] = true;
		} else {
			aRetailerUpdateRangeCB[selectedRetailer] = false;
		}
	}
	
	private void initalizeJade(boolean bQuickStart) {
		Runtime rt = Runtime.instance();
		rt.setCloseVM(true);
		
		// Create the main container 
		Profile p = new ProfileImpl(null, 1200, "main");
		jade.wrapper.AgentContainer mc = rt.createMainContainer(p);
		
		// Create container for home agents
		ProfileImpl hp = new ProfileImpl(null, 1200, "home");
		hp.setParameter(Profile.CONTAINER_NAME, "Home-Container");
		jade.wrapper.AgentContainer hc = rt.createAgentContainer(hp);
		
		// Create container for retailer agents
		ProfileImpl rp = new ProfileImpl(null, 1200, "retailers");
		rp.setParameter(Profile.CONTAINER_NAME, "Retailer-Container");
		jade.wrapper.AgentContainer rc = rt.createAgentContainer(rp);
		
		// Create container for appliance agents
		ProfileImpl ap = new ProfileImpl(null, 1200, "appliances");
		ap.setParameter(Profile.CONTAINER_NAME, "Appliance-Container");
		jade.wrapper.AgentContainer ac = rt.createAgentContainer(ap);
	
		Object[] homeArguments = new Object[3];
		Object[][] retailerArguments = new Object[3][2];
		Object[][] applianceArguments = new Object[3][2];
		
		// Set up Home Agent
		home = new Home(fHomeName.getText(), sHomeGenRate.getValue(), sHomeUseRate.getValue(), 
				sHomeSupply.getValue(), sHomeIncome.getValue());
		homeArguments[0] = home;
		
		Object[] homeTradeData = new Object[] { 
			cbRangeHomeTradeTime.isSelected(), 
			(float)spHomeTradeMinTime.getValue(), 
			(float)spHomeTradeMaxTime.getValue()
		};
		
		Object[] homeUpdateData = new Object[] {
			cbRangeHomeTradeTime.isSelected(),
			(float)spHomeUpdateMinTime.getValue(),
			(float)spHomeUpdateMaxTime.getValue()
		};
		
		homeArguments[1] = homeTradeData;
		homeArguments[2] = homeUpdateData;
		
		// Set up Retailer Agents
		saveRetailer();
		for(int i = 0; i < 3; i++) {
			retailers[i] = new Retailer(aRetailerNames[i], aRetailerGenRates[i], 
					aRetailerPrices[i], aRetailerSupplies[i], aRetailerTypes[i]);
			retailerArguments[i][0] = retailers[i]; 
			
			Object[] retailerUpdateData = new Object[] {
				aRetailerUpdateRangeCB[i], aRetailerUpdateMinTimes[i], aRetailerUpdateMaxTimes[i]
			};
			retailerArguments[i][1] = retailerUpdateData;
		}
		
		// Set up Appliance Agents
		saveAppliance();
		for(int i = 0; i < 3; i++) {
			appliances[i] = new Appliance(aApplianceNames[i], 
					aApplianceGenRates[i], aApplianceUseRates[i]);
			applianceArguments[i][0] = appliances[i];
			
			Object[] applianceUpdateData = new Object[] {
				aApplianceUpdateRangeCB[i], aApplianceUpdateMinTimes[i], aApplianceUpdateMaxTimes[i]
			};
			applianceArguments[i][1] = applianceUpdateData;
		}
		
		// Set up broker agent
		broker = new Broker();
		Object[] brokerArguments = new Object[] {broker};
		
		// Create and start agents
		try {
			AgentController rma = mc.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
			rma.start();
			AgentController ba = mc.createNewAgent("Broker", "agents.BrokerAgent", brokerArguments);
			ba.start();
			
			AgentController ra, aa;
			for(int i = 0; i < 3; i++) {
				ra = rc.createNewAgent(aRetailerNames[i], "agents.RetailerAgent", retailerArguments[i]);
				ra.start();
				
				aa = ac.createNewAgent(aApplianceNames[i], "agents.ApplianceAgent", applianceArguments[i]);
				aa.start();
			}
			
			AgentController ha = hc.createNewAgent(fHomeName.getText(), "agents.HomeAgent", homeArguments);
			ha.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}