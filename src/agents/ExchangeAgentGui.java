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

public class ExchangeAgentGui extends JFrame implements ActionListener, SupplierVocabulary {
	public ExchangeAgentGui() {
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JLabel lblHomeEnergyTrading = new JLabel("Home Energy Trading System");
		lblHomeEnergyTrading.setFont(new Font("Calibri", Font.BOLD, 32));
		lblHomeEnergyTrading.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(lblHomeEnergyTrading);
		
		JLabel lblNewLabel = new JLabel("Designed by James Martin, Nathan Harris & Jacyln Seychell ");
		lblNewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel.setFont(new Font("Calibri", Font.PLAIN, 24));
		panel.add(lblNewLabel);
		
		JSeparator separator = new JSeparator();
		panel.add(separator);
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(null);
		
		JButton btnStartJade = new JButton("Start Jade");
		btnStartJade.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				initalizeJade();
				btnStartJade.setEnabled(false);
			}
		});
		btnStartJade.setBounds(556, 147, 115, 29);
		panel_1.add(btnStartJade);
		
		this.setSize(1280, 720);
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
