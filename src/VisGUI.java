import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.BoxLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.FlowLayout;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JSplitPane;
import javax.swing.JProgressBar;
import java.awt.GridLayout;
import net.miginfocom.swing.MigLayout;
import javax.swing.JSlider;
import java.awt.Panel;
import java.awt.Canvas;

public class VisGUI extends JFrame {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VisGUI frame = new VisGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VisGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 848, 588);
		getContentPane().setLayout(null);
		
		JLabel lblHome = new JLabel("Home");
		lblHome.setBounds(271, 112, 69, 20);
		getContentPane().add(lblHome);
		
		JLabel lblBroker = new JLabel("Broker");
		lblBroker.setBounds(467, 112, 69, 20);
		getContentPane().add(lblBroker);
		
		JLabel lblRetailers = new JLabel("Retailers");
		lblRetailers.setBounds(648, 112, 69, 20);
		getContentPane().add(lblRetailers);
		
		JLabel lblAppliances = new JLabel("Appliances");
		lblAppliances.setBounds(61, 112, 92, 20);
		getContentPane().add(lblAppliances);
		
		JSlider slider = new JSlider();
		slider.setBounds(98, 265, 200, 26);
		getContentPane().add(slider);
		
		Panel panel = new Panel();
		panel.setBounds(61, 178, 60, 60);
		getContentPane().add(panel);
		
		Canvas canvas = new Canvas();
		canvas.setBounds(277, 191, 100, 100);
		getContentPane().add(canvas);
	}
}
