package gui;

import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class CommandGroup extends JPanel {

	private static final long serialVersionUID = 1L;

	private JButton okButton, cancelButton;
	private JPanel wrapperPanel;
	
	public CommandGroup(ActionListener listener){
		super();
		this.wrapperPanel = new JPanel();
		this.wrapperPanel.setLayout(new BoxLayout(this.wrapperPanel, BoxLayout.X_AXIS));
		this.wrapperPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		okButton.setActionCommand("ok");
		cancelButton.setActionCommand("cancel");
		okButton.addActionListener(listener);
		cancelButton.addActionListener(listener);
		
		this.wrapperPanel.add(okButton);
		this.wrapperPanel.add(Gap.createGap(Gap.X, 10));
		this.wrapperPanel.add(cancelButton);
		
		this.add(this.wrapperPanel);
	}
	
	public void setOpaque(boolean b){
		if(this.wrapperPanel != null)
			this.wrapperPanel.setOpaque(b);
		if(this.okButton != null)
			this.okButton.setOpaque(b);
		if(this.cancelButton != null)
			this.cancelButton.setOpaque(b);
		
		super.setOpaque(b);
	}
	
	public void setEnabled(boolean b){
		this.okButton.setEnabled(b);
		this.cancelButton.setEnabled(b);
		
	}
}
