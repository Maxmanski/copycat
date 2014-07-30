package gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

public class TextAndFileHandler extends TransferHandler {

	private static final long serialVersionUID = 1L;

	@Override
	public int getSourceActions(JComponent c) {
	    return COPY_OR_MOVE;
	}

	@Override
	public Transferable createTransferable(JComponent c) {
		if(c instanceof JTextField){
		    return new StringSelection(((JTextField)c).getText());
		}
		else return null;
	}

	@Override
	public void exportDone(JComponent c, Transferable t, int action) {
	    if (action == MOVE && c instanceof JTextField) {
	    	String txt = ((JTextField)c).getText();
	    	int start = ((JTextField)c).getSelectionStart(), end = ((JTextField)c).getSelectionEnd();
	    	((JTextField)c).setText(txt.substring(0, start)+txt.substring(end));
	    }
	}
	
	@Override
	public boolean canImport(TransferHandler.TransferSupport support) {
		
		if(!support.getComponent().isEnabled()){
			return false;
		}
		
		if(!support.isDrop()){
			return true;
		}
		
	    // we only import Strings
	    if (!support.isDataFlavorSupported(DataFlavor.stringFlavor) &&
	    		!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
	        return false;
	    }

	    // check if the source actions (a bitwise-OR of supported actions)
	    // contains the COPY action
	    boolean copySupported = (COPY & support.getSourceDropActions()) == COPY;
	    
	    if (copySupported) {
	        return true;
	    }
	    
	    // COPY is not supported, so reject the transfer
	    return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public boolean importData(TransferHandler.TransferSupport support) {
		
		
		if(!canImport(support) || !(support.getComponent() instanceof JTextField) || !support.getComponent().isEnabled()){
			return false;
		}
		
		JTextField jTF = ((JTextField)support.getComponent());
		
		String text = null, oldTxt = jTF.getText();
		int start = jTF.getSelectionStart(), end = jTF.getSelectionEnd();
		List<File> files = null;
		List<DataFlavor> flavorList = new ArrayList<DataFlavor>();
		
		try{
			Collections.addAll(flavorList, support.getTransferable().getTransferDataFlavors());
			if(flavorList.contains(DataFlavor.javaFileListFlavor)){
				
				files = new ArrayList<File>((List<File>)support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
				text = "";

				if(files.size() >= 1){
					text = files.get(0).getAbsolutePath();
					for(int i=1; i<files.size(); i++){
						text += ";" + files.get(i).getAbsolutePath();
					}
				}

				jTF.setText(text);
				
			}else if(flavorList.contains(DataFlavor.stringFlavor)){
				text = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);

				jTF.setText(oldTxt.substring(0, start) + text + oldTxt.substring(end));
				jTF.setCaretPosition(start+text.length());
				
			}else{
				return false;
				
			}
		}catch(UnsupportedFlavorException ufe){
			return false;
		}catch(IOException ioe){
			return false;
		}catch(Exception e){
			return false;
		}
		
		
		return true;
	}
	
}
