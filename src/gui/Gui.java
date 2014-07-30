package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class Gui {
	private JFrame frame;
	private JLabel workingLabel;
	private DeleteGroup deleteGroup;
	private CopyGroup copyGroup;
	private CommandGroup commandGroup;
	private CopyCatAction action;
	private ButtonGroup operationGroup;
	private JRadioButton rDelete, rCopy, rCut;
	private boolean copyActive, cutActive, deleteActive;

	public Gui(String title, String version, String author, String date){
		this.action = CopyCatAction.None;
		
		frame = new JFrame(title + " v"+version+" - "+author+" ("+date+")");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		JPanel panel = new JPanel();
		frame.setContentPane(panel);
		OperationListener op = new OperationListener(this);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		workingLabel = new JLabel("Working Mode: ");
		
		panel.setOpaque(true);
		try{
			ArrayList<LookAndFeelInfo> installedLookAndFeels = new ArrayList<LookAndFeelInfo>();
			LookAndFeelInfo nimbus = null, windows = null;
			for(LookAndFeelInfo info: UIManager.getInstalledLookAndFeels()){
				installedLookAndFeels.add(info);
				if(info.getName().equalsIgnoreCase("nimbus")){
					nimbus = info;
				}else if(info.getName().equalsIgnoreCase("windows")){
					windows = info;
				}
			}
			
			// vorzugsweise: Windows
			// dann: Nimbus
			// falls beides nicht vorhanden: nimm einfach irgendetwas
			UIManager.setLookAndFeel((windows!=null)?windows.getClassName() :
				((nimbus!=null) ? nimbus.getClassName() : 
				installedLookAndFeels.get(((int)(Math.random()*installedLookAndFeels.size()))).getClassName()
				));
			
			List<Image> icons = new ArrayList<Image>();
			icons.add(new ImageIcon(ClassLoader.getSystemResource("img/icon16.png")).getImage());
			icons.add(new ImageIcon(ClassLoader.getSystemResource("img/icon32.png")).getImage());
			icons.add(new ImageIcon(ClassLoader.getSystemResource("img/icon64.png")).getImage());
			icons.add(new ImageIcon(ClassLoader.getSystemResource("img/icon128.png")).getImage());
			frame.setIconImages(icons);
			
		}catch(Exception e){
		}

		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.X_AXIS));
		copyGroup = new CopyGroup("Copy / Cut");
		deleteGroup = new DeleteGroup("Delete");
		commandGroup = new CommandGroup(op);

		rDelete = new JRadioButton("Delete");
		rCopy = new JRadioButton("Copy");
		rCut = new JRadioButton("Cut");
		rDelete.setActionCommand("delete");
		rCopy.setActionCommand("copy");
		rCut.setActionCommand("cut");
		rDelete.addActionListener(op);
		rCopy.addActionListener(op);
		rCut.addActionListener(op);
		rDelete.setOpaque(false);
		rCopy.setOpaque(false);
		rCut.setOpaque(false);
		op=null;

		operationGroup = new ButtonGroup();
		operationGroup.add(rDelete);
		operationGroup.add(rCopy);
		operationGroup.add(rCut);

		radioPanel.add(workingLabel);
		radioPanel.add(Gap.createGap(Gap.X, 5));
		radioPanel.add(rCopy);
		radioPanel.add(Gap.createGap(Gap.X, 5));
		radioPanel.add(rCut);
		radioPanel.add(Gap.createGap(Gap.X, 5));
		radioPanel.add(rDelete);
		radioPanel.setOpaque(false);
		
		copyGroup.setOpaque(false);
		deleteGroup.setOpaque(false);
		commandGroup.setOpaque(false);

		panel.add(radioPanel);
		panel.add(Gap.createGap(Gap.Y, 10));
		panel.add(copyGroup);
		panel.add(Gap.createGap(Gap.Y, 10));
		panel.add(deleteGroup);
		panel.add(Gap.createGap(Gap.Y, 10));
		panel.add(commandGroup);

		rCopy.setSelected(true);
		deleteGroup.setEnabled(false);
		this.copyActive = true;
		this.deleteActive=false;
		this.cutActive = false;

		frame.pack();
		frame.setVisible(true);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(), frameSize = frame.getSize();
		frame.setLocation(screenSize.width/2 - frameSize.width/2, screenSize.height/2 - frameSize.height/2);
	}
	
	public void setBusy(boolean b){
		b = !b;
		this.commandGroup.setEnabled(b);
		this.rDelete.setEnabled(b);
		this.rCut.setEnabled(b);
		this.rCopy.setEnabled(b);
		
		if(b){
			if(this.copyActive || this.cutActive){
				this.copyGroup.setEnabled(true);
				this.deleteGroup.setEnabled(false);
			}else if(this.deleteActive){
				this.copyGroup.setEnabled(false);
				this.deleteGroup.setEnabled(true);				
			}
			this.workingLabel.setForeground(Color.black);
		}else{
			this.copyGroup.setEnabled(false);
			this.deleteGroup.setEnabled(false);
			this.workingLabel.setForeground(Color.gray);
		}
	}

	public String getSourceFileName(){
		if(deleteActive && !deleteGroup.getFileTextField().getText().equals("")){

			return deleteGroup.getFileTextField().getText();

		}else if(copyActive && !copyGroup.getSourceTextField().getText().equals("")){

			return copyGroup.getSourceTextField().getText();

		}else if(cutActive && !copyGroup.getSourceTextField().getText().equals("")){

			return copyGroup.getSourceTextField().getText();

		}
		return null;
	}

	public String getDestinationFileName(){
		if(copyActive && !copyGroup.getDestinationTextField().getText().equals("")){

			return copyGroup.getDestinationTextField().getText();

		}else if(cutActive && !copyGroup.getDestinationTextField().getText().equals("")){

			return copyGroup.getDestinationTextField().getText();

		}
		return null;
	}

	public CommandGroup getCommandGroup(){
		return this.commandGroup;
	}

	public DeleteGroup getDeleteGroup(){
		return this.deleteGroup;
	}

	public CopyGroup getCopyGroup(){
		return this.copyGroup;
	}

	public CopyCatAction getAction(){
		return this.action;
	}
	
	public JFrame getFrame(){
		return this.frame;
	}

	public CopyCatAction waitForAction(){
		while(this.action.equals(CopyCatAction.None)){

		}

		CopyCatAction tmp = this.action;
		this.action = CopyCatAction.None;
		return tmp;
	}

	private class OperationListener implements ActionListener{

		private Gui g;

		public OperationListener(Gui gui){
			this.g = gui;
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			if(ae.getActionCommand()!=null && !ae.getActionCommand().equals("")){
				if(ae.getActionCommand().equals("delete")){

					g.getDeleteGroup().setEnabled(true);
					g.getCopyGroup().setEnabled(false);
					g.copyActive = false;
					g.deleteActive=true;
					g.cutActive = false;

				}else if(ae.getActionCommand().equals("copy")){

					g.getDeleteGroup().setEnabled(false);
					g.getCopyGroup().setEnabled(true);
					g.copyActive = true;
					g.deleteActive=false;
					g.cutActive = false;

				}else if(ae.getActionCommand().equals("cut")){

					g.getDeleteGroup().setEnabled(false);
					g.getCopyGroup().setEnabled(true);
					g.copyActive = false;
					g.deleteActive=false;
					g.cutActive = true;

				}else if(ae.getActionCommand().equals("ok")){
					
					String tmpSrc = g.getSourceFileName(),
							tmpDest = g.getDestinationFileName();
					
					String[] strSrc = tmpSrc==null?new String[0]:tmpSrc.split(";"),
							strDest = tmpDest==null?new String[0]:tmpDest.split(";");
					
					boolean inputAvailable = strSrc.length>0 && 
							(strDest.length>0 || g.operationGroup.getSelection().getActionCommand().equals("delete")),
							
							filesExist = true, filesOnly=true, dirsOnly=true;
					
					File tmp;
					
					for(String s: strSrc){
						tmp = new File(s);
						if(!tmp.exists()){
							filesExist = false;
						}else{
							if(tmp.isDirectory()){
								filesOnly = false;
							}else{
								dirsOnly = false;
							}
						}
					}
					
					if(inputAvailable && filesExist &&
							((filesOnly || dirsOnly) || g.operationGroup.getSelection().getActionCommand().equals("delete"))){
						
						if(g.operationGroup.getSelection().getActionCommand().equals("delete")){

							g.action = CopyCatAction.DeleteAction;

						}else if(g.operationGroup.getSelection().getActionCommand().equals("copy")){

							g.action = CopyCatAction.CopyAction;

						}else if(g.operationGroup.getSelection().getActionCommand().equals("cut")){

							g.action = CopyCatAction.CutAction;

						}
					}else{
						String msg="There was something wrong with your input!";
						
						if(!inputAvailable){
							msg = "Please make sure you have selected all necessary files (source and destination)!";
							
						}else if(!filesExist){
							msg = "One or more of your selected files doesn't seem to exist, please check!";
							
						}else if(!(filesOnly || dirsOnly)){
							msg = "Please don't mix the selection of files and directories as source!";
							
						}
						
						JOptionPane.showMessageDialog(g.getFrame(), msg, "Error", JOptionPane.ERROR_MESSAGE);
					}

				}else if(ae.getActionCommand().equals("cancel")){
					System.exit(0);
				}
			}
		}

	}
}
