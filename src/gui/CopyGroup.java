package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class CopyGroup extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel srcL, destL;
	private JTextField srcTF, destTF;
	private JButton srcSearchButton, destSearchButton;
	private JPanel srcPanel, destPanel, wrapperPanel;
	private final String label;
	private boolean enabled;

	public CopyGroup(String label){
		super();
		this.label = label;

		this.srcL = new JLabel("Source:");
		this.destL = new JLabel("Destination:");
		this.srcTF = new JTextField();
		this.destTF = new JTextField();
		this.srcSearchButton = new JButton("...");
		this.destSearchButton = new JButton("...");
		this.srcTF.setPreferredSize(new Dimension(250,this.srcTF.getPreferredSize().height));
		this.destTF.setPreferredSize(new Dimension(250,this.destTF.getPreferredSize().height));
		this.srcPanel = new JPanel();
		this.destPanel = new JPanel();
		this.wrapperPanel = new JPanel();
		this.srcPanel.setLayout(new BoxLayout(srcPanel, BoxLayout.X_AXIS));
		this.destPanel.setLayout(new BoxLayout(destPanel, BoxLayout.X_AXIS));
		this.wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
		this.srcSearchButton.setActionCommand("srcSearch");
		this.destSearchButton.setActionCommand("destSearch");
		this.srcSearchButton.addActionListener(new CopyListener(this));
		this.destSearchButton.addActionListener(new CopyListener(this));

		this.srcTF.setTransferHandler(new TextAndFileHandler());
		this.destTF.setTransferHandler(new TextAndFileHandler());
		
		this.srcPanel.add(srcL);
		this.srcPanel.add(Gap.createGap(Gap.X, 5));
		this.srcPanel.add(srcTF);
		this.srcPanel.add(Gap.createGap(Gap.X, 5));
		this.srcPanel.add(srcSearchButton);
		this.destPanel.add(destL);
		this.destPanel.add(Gap.createGap(Gap.X, 5));
		this.destPanel.add(destTF);
		this.destPanel.add(Gap.createGap(Gap.X, 5));
		this.destPanel.add(destSearchButton);

		this.wrapperPanel.add(srcPanel);
		this.wrapperPanel.add(Gap.createGap(Gap.Y, 5));
		this.wrapperPanel.add(destPanel);
		this.wrapperPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		this.add(wrapperPanel);
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), this.label,
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(null, Font.BOLD, 12), Color.black));
		this.enabled = true;
	}

	public void setOpaque(boolean b){
		if(this.destL != null)
			this.destL.setOpaque(b);
		if(this.srcL != null)
			this.srcL.setOpaque(b);
		if(this.destSearchButton != null)
			this.destSearchButton.setOpaque(b);
		if(this.srcSearchButton != null)
			this.srcSearchButton.setOpaque(b);
//		if(this.destTF != null)
//			this.destTF.setOpaque(b);
//		if(this.srcTF != null)
//			this.srcTF.setOpaque(b);
		if(this.wrapperPanel != null)
			this.wrapperPanel.setOpaque(b);
		if(this.srcPanel != null)
			this.srcPanel.setOpaque(b);
		if(this.destPanel != null)
			this.destPanel.setOpaque(b);
		super.setOpaque(b);
	}

	public void setEnabled(boolean b){
		this.enabled = b;
		this.destSearchButton.setEnabled(b);
		this.srcSearchButton.setEnabled(b);
		this.srcTF.setEnabled(b);
		this.destTF.setEnabled(b);
		this.srcL.setEnabled(b);
		this.destL.setEnabled(b);

		if(b){
			this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), this.label,
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(null, Font.BOLD, 12), Color.black));

		}else{
			this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.lightGray), this.label,
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(null, Font.BOLD, 12), Color.gray));

		}
	}

	public boolean isEnabled(){
		return this.enabled;
	}

	public JTextField getSourceTextField(){
		return this.srcTF;
	}

	public JTextField getDestinationTextField(){
		return this.destTF;
	}

	private class CopyListener implements ActionListener{
		
		private File lastSelectedSource, lastSelectedDest;

		private CopyGroup cg;

		public CopyListener(CopyGroup copyGroup){
			this.cg = copyGroup;
			this.lastSelectedDest = null;
			this.lastSelectedSource = null;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand()!=null && !e.getActionCommand().equals("")){
				if(e.getActionCommand().equals("srcSearch")){

					JFileChooser jfc = new JFileChooser();
					
					jfc.setFileHidingEnabled(false);
					jfc.setMultiSelectionEnabled(true);
					jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					if(this.lastSelectedSource == null){
						String[] selectedFilenames = cg.getSourceTextField().getText().split(";");
						
						if(selectedFilenames.length >= 1){
							File selectedFile = new File(selectedFilenames[0]);
							
							if(selectedFile.exists() && !selectedFile.isDirectory()){
								jfc.setSelectedFile(selectedFile);
							
							}else if(new File(selectedFile.getAbsolutePath()).exists()){
								jfc.setSelectedFile(new File(selectedFile.getAbsolutePath()+"/"));
								
							}
						}
						
					}else{
						jfc.setSelectedFile(lastSelectedSource);
					}

					int res = jfc.showOpenDialog(cg.getParent());

					// Code nach Finden einer Datei
					if(res == JFileChooser.APPROVE_OPTION){

						File[] selectedFiles = jfc.getSelectedFiles();
						String selString = "";
						
						if(selectedFiles.length >= 1){
							selString = selectedFiles[0].getAbsolutePath();
							this.lastSelectedSource = selectedFiles[0].getParentFile();
						}
						
						for(int i=1; i<selectedFiles.length; i++){
							
							selString += ";"+selectedFiles[i].getAbsolutePath();
						}
						
						cg.getSourceTextField().setText(selString);

					}else if(res == JFileChooser.CANCEL_OPTION){

					}else if(res == JFileChooser.ERROR_OPTION){

					}

				}else if(e.getActionCommand().equals("destSearch")){

					JFileChooser jfc = new JFileChooser();
					jfc.setFileHidingEnabled(false);
					jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					
					if(this.lastSelectedDest == null){
						File selectedFile;
						if(cg.getDestinationTextField().getText().equals("") && !cg.getSourceTextField().getText().equals("")){

							selectedFile = new File(new File(cg.getSourceTextField().getText()).getAbsolutePath()+File.pathSeparator);

						}else if(!cg.getDestinationTextField().getText().equals("")){

							selectedFile = new File(cg.getDestinationTextField().getText());

						}else{

							selectedFile = null;
						}

						if(selectedFile != null && selectedFile.exists()){
							jfc.setSelectedFile(selectedFile);

						}else if(selectedFile != null && new File(selectedFile.getAbsolutePath()).exists()){
							jfc.setSelectedFile(new File(selectedFile.getAbsolutePath()+File.pathSeparator));

						}
					}else{
						jfc.setSelectedFile(lastSelectedDest);
					}
					
					int res = jfc.showSaveDialog(cg.getParent());

					if(res == JFileChooser.APPROVE_OPTION){

						cg.getDestinationTextField().setText(jfc.getSelectedFile().getAbsolutePath());
						this.lastSelectedDest = jfc.getSelectedFile().getParentFile();

					}else if(res == JFileChooser.CANCEL_OPTION){

					}else if(res == JFileChooser.ERROR_OPTION){

					}

				}
			}
		}
	}
}
