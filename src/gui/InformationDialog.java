package gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class InformationDialog extends JDialog{

	private static final long serialVersionUID = 1L;
	private JPanel contentPanel;
	private JLabel statusLabel;

	public InformationDialog(Frame owner, String title, String message){
		super(owner, title, false);
		contentPanel = new JPanel();
		this.setContentPane(contentPanel);
		this.statusLabel = new JLabel(message);

		this.contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.contentPanel.add(statusLabel);
		this.pack();

		if(owner!=null){
			Point ownerLocation = owner.getLocation();
			Dimension size = this.getSize(), ownerSize = owner.getSize();
			int x, y;
			x = ownerSize.width/2 - size.width/2 + ownerLocation.x;
			y = ownerSize.height/2 - size.height/2 + ownerLocation.y;
			this.setLocation(x, y);

		}
	}

	public void setMessage(String message){
		this.statusLabel.setText(message);
		this.pack();
	}
	
	public void destroy(){
		this.setVisible(false);
		this.contentPanel = null;
		this.statusLabel = null;
		super.dispose();
	}
}
