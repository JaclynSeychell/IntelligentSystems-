package agents;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;

import jade.core.*;
import jade.core.Runtime;
import jade.gui.*;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import ontologies.*;
import utility.Utility;

public class ExchangeAgentGui extends JFrame implements ActionListener, SupplierVocabulary {
	private JTextField fHomeName;
	private Random rnd = Utility.newRandom(hashCode());
	private int nHomeGenRate = 0;
	private int nHomeUseRate = 0;
	private int nHomeSupply = 0;
	private int nHomeIncome = 0;

	public ExchangeAgentGui() {
		JPanel pHeader = new JPanel();
		getContentPane().add(pHeader, BorderLayout.NORTH);
		pHeader.setLayout(new BoxLayout(pHeader, BoxLayout.Y_AXIS));
		
		JLabel lblHomeEnergyTrading = new JLabel("Home Energy Trading System");
		lblHomeEnergyTrading.setFont(new Font("Calibri", Font.BOLD, 32));
		lblHomeEnergyTrading.setAlignmentX(Component.CENTER_ALIGNMENT);
		pHeader.add(lblHomeEnergyTrading);
		
		JLabel lblNewLabel = new JLabel("Designed by James Martin, Nathan Harris & Jacyln Seychell ");
		lblNewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel.setFont(new Font("Calibri", Font.PLAIN, 24));
		pHeader.add(lblNewLabel);
		
		JSeparator separator = new JSeparator();
		pHeader.add(separator);
		
		JPanel pBody = new JPanel();
		getContentPane().add(pBody, BorderLayout.CENTER);
		pBody.setLayout(new GridLayout(1, 3, 0, 0));
		
		JPanel pHome = new JPanel();
		pBody.add(pHome);
		GridBagLayout gbl_pHome = new GridBagLayout();
		gbl_pHome.columnWidths = new int[]{0, 0};
		gbl_pHome.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_pHome.columnWeights = new double[]{1.0, 1.0};
		gbl_pHome.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		pHome.setLayout(gbl_pHome);
		
		JLabel lHomeTitle = new JLabel("Home Agent Parameters");
		GridBagConstraints gbc_lHomeTitle = new GridBagConstraints();
		gbc_lHomeTitle.gridwidth = 4;
		gbc_lHomeTitle.gridheight = 2;
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
		
		JSlider sHomeGenRate = new JSlider();
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
		lHomeGenValue.setText(Integer.toString(sHomeGenRate.getValue()));
		GridBagConstraints gbc_lHomeGenValue = new GridBagConstraints();
		gbc_lHomeGenValue.insets = new Insets(0, 0, 5, 5);
		gbc_lHomeGenValue.gridx = 2;
		gbc_lHomeGenValue.gridy = 3;
		
		pHome.add(lHomeGenValue, gbc_lHomeGenValue);
		
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
		
		JSlider sHomeUseRate = new JSlider();
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
		
		JLabel lHomeUseValue = new JLabel("New label");
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
		
		JSlider sHomeSupply = new JSlider();
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
		
		JLabel lHomeSupplyValue = new JLabel("New label");
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
		gbc_lHomeIncome.insets = new Insets(0, 0, 0, 5);
		gbc_lHomeIncome.gridx = 0;
		gbc_lHomeIncome.gridy = 6;
		pHome.add(lHomeIncome, gbc_lHomeIncome);
		
		JSlider sHomeIncome = new JSlider();
		sHomeIncome.setMinimum(10);
		sHomeIncome.setValue(55);
		sHomeIncome.setMajorTickSpacing(10);
		sHomeIncome.setPaintTicks(true);
		GridBagConstraints gbc_sHomeIncome = new GridBagConstraints();
		gbc_sHomeIncome.fill = GridBagConstraints.HORIZONTAL;
		gbc_sHomeIncome.insets = new Insets(0, 0, 0, 5);
		gbc_sHomeIncome.gridx = 1;
		gbc_sHomeIncome.gridy = 6;
		pHome.add(sHomeIncome, gbc_sHomeIncome);
		
		JLabel lHomeIncomeValue = new JLabel("");
		lHomeIncomeValue.setText(Integer.toString(sHomeIncome.getValue()));
		GridBagConstraints gbc_lHomeIncomeValue = new GridBagConstraints();
		gbc_lHomeIncomeValue.insets = new Insets(0, 0, 0, 5);
		gbc_lHomeIncomeValue.gridx = 2;
		gbc_lHomeIncomeValue.gridy = 6;
		pHome.add(lHomeIncomeValue, gbc_lHomeIncomeValue);
		
		JCheckBox cbRandHomeIncome = new JCheckBox("Randomize");
		GridBagConstraints gbc_cbRandHomeIncome = new GridBagConstraints();
		gbc_cbRandHomeIncome.gridx = 3;
		gbc_cbRandHomeIncome.gridy = 6;
		pHome.add(cbRandHomeIncome, gbc_cbRandHomeIncome);
		
		// Initalize params
		nHomeGenRate = sHomeGenRate.getValue();
		nHomeUseRate = sHomeUseRate.getValue();
		nHomeSupply = sHomeSupply.getValue();
		nHomeIncome = sHomeIncome.getValue();
		
		// Handle Slider events
		sHomeGenRate.addChangeListener(new SliderListener(lHomeGenValue, nHomeGenRate));
		sHomeUseRate.addChangeListener(new SliderListener(lHomeUseValue, nHomeUseRate));
		sHomeSupply.addChangeListener(new SliderListener(lHomeSupplyValue, nHomeSupply));
		sHomeIncome.addChangeListener(new SliderListener(lHomeIncomeValue, nHomeIncome));
		
		// Handle CheckBox events
		cbRandHomeGenRate.addChangeListener(new CheckBoxListener(lHomeGenValue, nHomeGenRate, sHomeGenRate));
		cbRandHomeUseRate.addChangeListener(new CheckBoxListener(lHomeUseValue, nHomeUseRate, sHomeUseRate));
		cbRandHomeSupply.addChangeListener(new CheckBoxListener(lHomeSupplyValue, nHomeSupply, sHomeSupply));
		cbRandHomeIncome.addChangeListener(new CheckBoxListener(lHomeIncomeValue, nHomeIncome, sHomeIncome));
		
		JPanel panel = new JPanel();
		pBody.add(panel);
		
		setSize(1280, 720);
	} 
	
	// Custom slider listener
	public class SliderListener implements ChangeListener {
		private JLabel labelUpdate;
		private int valueUpdate;
		
		SliderListener(JLabel labelUpdate, int valueUpdate) {
			super();
			this.labelUpdate = labelUpdate;
			this.valueUpdate = valueUpdate;
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();
			labelUpdate.setText(Integer.toString(source.getValue()));
			valueUpdate = source.getValue();
		}
	}
	
	// Custom checkbox listener
	public class CheckBoxListener implements ChangeListener {
		private JSlider sliderUpdate;
		private JLabel labelUpdate;
		private int valueUpdate;
		
		CheckBoxListener(JLabel labelUpdate, int valueUpdate, JSlider sliderUpdate) {
			super();
			this.labelUpdate = labelUpdate;
			this.valueUpdate = valueUpdate;
			this.sliderUpdate = sliderUpdate;
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			JCheckBox source = (JCheckBox)e.getSource();
			if (source.isSelected()) {
				sliderUpdate.setEnabled(false);
				valueUpdate = (rnd.nextInt(sliderUpdate.getMaximum()) + sliderUpdate.getMinimum()) % sliderUpdate.getMaximum();
				labelUpdate.setText("??");
			} else {
				sliderUpdate.setEnabled(true);
				labelUpdate.setText(Integer.toString(sliderUpdate.getValue()));
			}
		}
	}
	
	private void initalizeJade() {
		Runtime rt = Runtime.instance();
		rt.setCloseVM(true);
		
		// Create the main container 
		Profile p = new ProfileImpl(null, 1200, "main");
		jade.wrapper.AgentContainer mc = rt.createMainContainer(p);
		
		// Create container for home agents
		ProfileImpl hp = new ProfileImpl(null, 1200, "homes");
		jade.wrapper.AgentContainer hc = rt.createAgentContainer(hp);
		
		// Create container for retailer agents
		ProfileImpl rp = new ProfileImpl(null, 1200, "retailers");
		jade.wrapper.AgentContainer rc = rt.createAgentContainer(rp);
		
		// Create and start agents
		try {
			AgentController rma = mc.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
			rma.start();
			AgentController ba = mc.createNewAgent("Broker", "agents.BrokerAgent", new Object[0]);
			ba.start();
			AgentController ha = hc.createNewAgent("Home", "agents.HomeAgent", new Object[0]);
			ha.start();
			
			AgentController ra;
			for(int i = 0; i < 3; i++) {
				ra = rc.createNewAgent("Retailer" + i, "agents.RetailerAgent", new Object[0]);
				ra.start();
			}
			
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		ExchangeAgentGui gui = new ExchangeAgentGui();
		gui.setVisible(true);
	}
}
