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

public class DeleteGroup extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField fileTF;
	private JLabel fileL;
	private JPanel wrapperPanel;
	private JButton searchButton;
	private final String label;
	private boolean enabled;

	public DeleteGroup(String label){
		super();
		this.label = label;
		this.wrapperPanel = new JPanel();
		this.wrapperPanel.setLayout(new BoxLayout(this.wrapperPanel, BoxLayout.X_AXIS));

		this.fileTF = new JTextField();
		this.fileTF.setPreferredSize(new Dimension(300, this.fileTF.getPreferredSize().height));
		this.fileL = new JLabel("File:");
		this.searchButton = new JButton("...");
		this.searchButton.setActionCommand("search");
		this.searchButton.addActionListener(new DeleteListener(this));

		this.fileTF.setTransferHandler(new TextAndFileHandler());
		
		this.wrapperPanel.add(fileL);
		this.wrapperPanel.add(Gap.createGap(Gap.X, 5));
		this.wrapperPanel.add(fileTF);
		this.wrapperPanel.add(Gap.createGap(Gap.X, 5));
		this.wrapperPanel.add(searchButton);
		this.add(this.wrapperPanel);

		this.wrapperPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), this.label,
				TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(null, Font.BOLD, 12), Color.black));
		this.enabled = true;

	}

	public void setOpaque(boolean b){
		if(this.fileL != null)
			this.fileL.setOpaque(b);
//		if(this.fileTF != null)
//			this.fileTF.setOpaque(b);
		if(this.wrapperPanel != null)
			this.wrapperPanel.setOpaque(b);
		if(this.searchButton != null)
			this.searchButton.setOpaque(b);

		super.setOpaque(b);
	}

	public void setEnabled(boolean b){
		this.enabled = b;
		this.fileTF.setEnabled(b);
		this.fileL.setEnabled(b);
		this.searchButton.setEnabled(b);

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

	public JTextField getFileTextField(){
		return this.fileTF;
	}

	private class DeleteListener implements ActionListener{

		private File lastSelectedFile;
		private DeleteGroup dg;

		public DeleteListener(DeleteGroup deleteGroup){
			this.dg = deleteGroup;
			this.lastSelectedFile = null;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand()!=null && !e.getActionCommand().equals("")){
				if(e.getActionCommand().equals("search")){

					JFileChooser jfc = new JFileChooser();
					jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					jfc.setMultiSelectionEnabled(true);
					
					if(this.lastSelectedFile == null){
						String selectedFilenames[] = dg.getFileTextField().getText().split(";");

						if(selectedFilenames.length >= 1){

							File selectedFile = new File(dg.getFileTextField().getText());

							if(selectedFile.exists() && !selectedFile.isDirectory()){
								jfc.setSelectedFile(selectedFile);

							}else if(new File(selectedFile.getAbsolutePath()).exists()){
								jfc.setSelectedFile(new File(selectedFile.getAbsolutePath()+"/"));

							}

						}
					}else{
						jfc.setSelectedFile(lastSelectedFile);
					}

					int res = jfc.showOpenDialog(dg.getParent());

					// Code nach dem Auswählen einer Datei
					if(res == JFileChooser.APPROVE_OPTION){

						File[] selectedFiles = jfc.getSelectedFiles();
						String selString = "";
						
						if(selectedFiles.length >= 1){
							selString = selectedFiles[0].getAbsolutePath();
							this.lastSelectedFile = selectedFiles[0].getParentFile();
						}
						
						for(int i=1; i<selectedFiles.length; i++){
							
							selString += ";"+selectedFiles[i].getAbsolutePath();
						}
						
						dg.getFileTextField().setText(selString);

					}else if(res == JFileChooser.CANCEL_OPTION){

					}else if(res == JFileChooser.ERROR_OPTION){

					}

				}
			}
		}

	}

}
