package main;
import gui.CopyCatAction;
import gui.Gui;
import gui.InformationDialog;
import gui.SearchType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;



public class Main {

	private static final String version = "1.4.5";
	private static final String author = "Max Moser";
	private static final String title = "CopyCat";
	private static final String date = "2014-07-30";
	private static List<String> errorMessages;

	
	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args){

		boolean success = true, startedWithArgs;
		String[] srcFilenames;
		String origSourceFileText="", origDestinationFileText="";
		String destFilePath="", destFileName="", destFileExt="";
		
		List<Integer> resultList = new ArrayList<Integer>();
		ArrayList<File> fileList = new ArrayList<File>();
		ArrayList<String> subPathList = new ArrayList<String>();
		
		CopyCatAction action = null;
		Gui g = null;
		InformationDialog dialog = null;
		
		Main.errorMessages = new ArrayList<String>();

		startedWithArgs = (args.length > 0);
		
		if(startedWithArgs){
			if(args[0].equalsIgnoreCase("-copy")  && args.length == 3){
				action = CopyCatAction.CopyAction;
				origSourceFileText = args[1];
				origDestinationFileText = args[2];

			}else if(args[0].equalsIgnoreCase("-cut")  && args.length == 3){
				action = CopyCatAction.CutAction;
				origSourceFileText = args[1];
				origDestinationFileText = args[2];

			}else if(args[0].equalsIgnoreCase("-delete") && args.length == 2){
				action = CopyCatAction.DeleteAction;
				origSourceFileText = args[1];
				origDestinationFileText = null;

			}else{
				String helpMsg = "Usage:\n copycat [-copy SRC_FILE DEST_FILE | -cut SRC_FILE DEST_FILE | -delete FILE]\n\n"
						+ "  Please specify only one file as source file and one as destination file.\n"
						+ "  The specified file paths must be absolute!";
				System.err.println(helpMsg);
				System.exit(1);
			}
			
			if(!new File(origSourceFileText).exists() || !new File(origSourceFileText).isAbsolute()){
				System.err.println("The specified source file is invalid!");
				success = false;
			}
			
			if(origDestinationFileText!=null && (origDestinationFileText.contains(";") 
					|| !new File(origDestinationFileText).isAbsolute())){
				System.err.println("The specified destination file is invalid!");
				success = false;
			}
			
			if(!success){
				System.exit(1);
			}
		}

		{
			if(!startedWithArgs){
				g = new Gui(Main.title, Main.version, Main.author, Main.date);
			}
			
			do{
				Main.errorMessages.clear();
				fileList = new ArrayList<File>();
				subPathList = new ArrayList<String>();
				
				if(!startedWithArgs){
					action = g.waitForAction();
					origSourceFileText = g.getSourceFileName();
					origDestinationFileText = g.getDestinationFileName();
				}
				srcFilenames = origSourceFileText.split(";");
				int index = -1;

				if(!action.equals(CopyCatAction.DeleteAction)){

					destFilePath = origDestinationFileText;
					destFileName = new File(destFilePath).getName();
					destFilePath = destFilePath.substring(0, destFilePath.lastIndexOf(destFileName));

					destFileExt = "";
					index = destFileName.lastIndexOf(".");

					if(destFileName != null && index >= 0){
						destFileExt = destFileName;
						destFileName = destFileName.substring(0, index);
						destFileExt = destFileExt.substring(index);
					}
					
				}

				if(!startedWithArgs){
					g.setBusy(true);
					dialog = new InformationDialog(g.getFrame(), "Working...", "Operation in progress (Preparing files)...");
					dialog.setVisible(true);
				}

				ArrayList<File> tmpList = new ArrayList<File>();
				ArrayList<String> tmpList2 = new ArrayList<String>();

				for(int i=0; i<srcFilenames.length; i++){
					tmpList = new ArrayList<File>();
					tmpList2 = new ArrayList<String>();

					index = Math.max(srcFilenames[i].lastIndexOf("/"), srcFilenames[i].lastIndexOf("\\"));
					index = (index<0)?srcFilenames[i].length():index;

					Main.addFile(tmpList, tmpList2, srcFilenames[i].substring(0, index), new File(srcFilenames[i]), SearchType.DFS);

					fileList.addAll(tmpList);
					subPathList.addAll(tmpList2);

				}

				srcFilenames = new String[fileList.size()];

				for(int i=0; i < fileList.size(); i++){
					srcFilenames[i] = fileList.get(i).getAbsolutePath();
				}

				String[] destFileFullNames = new String[srcFilenames.length];
				
				// KOPIEREN
				for(int i=0; i < srcFilenames.length; i++){
					
					destFileFullNames[i] = "";

					if(action.equals(CopyCatAction.DeleteAction)){
						break;
					}

					File destF = new File(origDestinationFileText);
					File srcF = new File(srcFilenames[i]);
					if(i==0 && !destF.exists() && srcF.isDirectory()){
						if(!destF.mkdir()){
							success = false;
							break;
						}
					}else if(i==0 && srcF.isDirectory() && destF.exists() && destF.isDirectory()){
						File tmpFile = new File(destF.getAbsolutePath() + srcF.getName());
						
						if(!tmpFile.exists()){
							if(!tmpFile.mkdir()){
								success = false;
								break;
							}
						}
					}
					
					// wenn DestinationFileName == Ordner, verschiebe Datei hinein
					if(!destF.isDirectory()){
						// Case: Datei

						if(i==0){
							destFileFullNames[i] = destFilePath + subPathList.get(i) + destFileName + destFileExt;

						}else{
							destFileFullNames[i] = destFilePath + subPathList.get(i) + destFileName + i + destFileExt;
						}
						
					}else{
						// Case: Ordner

						index = srcFilenames[i].lastIndexOf(File.separator);
						
						destFileFullNames[i] = (index >= 0)?
								origDestinationFileText+subPathList.get(i)+srcFilenames[i].substring(index) :
									origDestinationFileText+subPathList.get(i)+"/file"+i+DynamicText.translate("_$DATE$");
								
					}
					
					if(!startedWithArgs){
						dialog.setMessage("Operation in progress (Copying File "+(i+1)+" out of "+srcFilenames.length+")..."
								+ (Main.errorMessages.isEmpty()?"":" - Warning: There have been errors so far!"));
						dialog.repaint();
						
					}

					if(action.equals(CopyCatAction.CopyAction) || action.equals(CopyCatAction.CutAction)){
						
						int res = Main.copy(srcFilenames[i], destFileFullNames[i]);
						resultList.add(res);
						success = success && (res!=0);
						
					}
				}
				
				// LÖSCHEN
				if(success){
					fileList = (ArrayList<File>)Main.rearrangeDirsToEndAndInvertTheirOrder(fileList);
					srcFilenames = new String[fileList.size()];
	
					for(int i=0; i<fileList.size(); i++){
						srcFilenames[i] = fileList.get(i).getAbsolutePath();
					}
	
					for(int i=0; i<srcFilenames.length; i++){
	
						if(action.equals(CopyCatAction.CopyAction)){
							break;
						}else if(action.equals(CopyCatAction.CutAction) && resultList.contains(2)){
							break;
						}
	
						if(!startedWithArgs){
							dialog.setMessage("Operation in progress (Deleting File "+(i+1)+" out of "+srcFilenames.length+")...");
							dialog.repaint();
							
						}
	
						if(action.equals(CopyCatAction.DeleteAction)){
							
							success = success && Main.delete(srcFilenames[i]);
							
						}else if(action.equals(CopyCatAction.CutAction)){
	
							success = success && Main.delete(srcFilenames[i]);
	
						}
					}
				}
				if(!startedWithArgs){
					dialog.destroy();

					if(success){

						JOptionPane.showMessageDialog(g.getFrame(), "Operation successfully finished!", "Success", JOptionPane.PLAIN_MESSAGE);
					}else{
						String errMsg = errorMessages.isEmpty()?null:"";
						
						for(int i=0; i<Math.min(Main.errorMessages.size(), 10); i++){
							errMsg += Main.errorMessages.get(i)+"\n";
						}
						
						if(Main.errorMessages.size() > 10){
							errMsg +="...";
						}
						
						if(errMsg != null && errMsg.endsWith("\n")){
							errMsg = errMsg.substring(0, errMsg.length()-1);
						}
						
						JOptionPane.showMessageDialog(g.getFrame(), "At least one operation failed!\n\n"
								+ (errMsg==null?"Most likely caused by insufficient privileges":errMsg),
										"Failure", JOptionPane.ERROR_MESSAGE);
					}
				}


				if(!startedWithArgs){
					g.setBusy(false);
				}
			}while(!startedWithArgs);
		}

		if(success){
			System.exit(0);

		}else{
			System.exit(1);
		}

	}

	/**
	 * Kopiert die angegebene SrcFile und speichert sie als angegebene DestFile wieder ab.
	 * Sollte die SrcFile ein Ordner gewesen sein, so wird die DestFile auch als Ordner angelegt.
	 * 
	 * @param srcFileName
	 * @param destFileName
	 * @return 1, falls die Operation ohne Fehler verlaufen ist <br>
	 * 			2, falls keine Kopier-Operation nötig gewesen ist<br>
	 * 			0 im Fall eines Fehlers
	 */
	private static int copy(String srcFileName, String destFileName){

		if(srcFileName==null || destFileName==null){
			return 0;
		}

		if(new File(srcFileName).equals(new File(destFileName))){
			return 2;
		}else if(new File(srcFileName).getParentFile().equals(new File(destFileName))){
			return 3;
		}

		boolean success = true;

		String src = srcFileName, dest = destFileName;

		File srcFile = new File(src), destFile = new File(dest);

		FileOutputStream fos = null;
		FileInputStream fis = null;
		
		try{
			if(srcFile.isDirectory()){
				if(!destFile.exists()){
					success = destFile.mkdir();
				}

			}else{
				fos = new FileOutputStream(destFile);
				fis = new FileInputStream(srcFile);

				byte data[] = new byte[4096];
				int amountRead;

				while((amountRead = fis.read(data)) != -1){
					fos.write(data, 0, amountRead);
				}
			}

		}catch(IOException ex){
			
			success = false;
			System.err.println(ex.getMessage());
			Main.errorMessages.add(ex.getMessage());
		}finally{
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
		
		return success?1:0;
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	private static boolean delete(String fileName){
		if(fileName==null){
			return false;
		}

		File file = new File(fileName);

		return file.delete() || !file.exists();
	}

	/**
	 * 
	 * @param fileList
	 * @param diffList
	 * @param baseDir
	 * @param f
	 * @param type
	 */
	private synchronized static void addFile(List<File> fileList, List<String> diffList, String baseDir, File f, SearchType type){
		fileList.add(f);

		baseDir = new File(baseDir).getAbsolutePath();

		String dir = f.getAbsolutePath();
		int index = dir.lastIndexOf(File.separator);
		index = (index<0)?0:index;

		dir = dir.substring(0, index);
		dir = dir.substring((dir.indexOf(baseDir) >= 0)?(dir.indexOf(baseDir)+baseDir.length()):
			dir.length());

		diffList.add(dir);
		
		if(f.isDirectory() && f.listFiles() != null){
			if(!baseDir.endsWith(File.separator)){
				baseDir += File.separator;
			}

			if(type.equals(SearchType.DFS)){

				for(File file: f.listFiles()){
					if(file.isDirectory()){
						addFile(fileList, diffList, baseDir, file, type);
					}
				}

				for(File file: f.listFiles()){
					if(!file.isDirectory()){
						addFile(fileList, diffList, baseDir, file, type);
					}
				}

			}else if(type.equals(SearchType.BFS)){

				for(File file: f.listFiles()){
					if(!file.isDirectory()){
						addFile(fileList, diffList, baseDir, file, type);
					}
				}

				for(File file: f.listFiles()){
					if(file.isDirectory()){
						addFile(fileList, diffList, baseDir, file, type);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param l
	 * @return
	 */
	private static List<File> rearrangeDirsToEndAndInvertTheirOrder(List<File> l){
		List<File> dirs = new ArrayList<File>(), newList = new ArrayList<File>();

		for(File f: l){
			if(f.isDirectory()){
				dirs.add(f);
			}else{
				newList.add(f);
			}
		}

		for(int i=dirs.size()-1; i>=0; i--){
			newList.add(dirs.get(i));
		}

		return newList;
	}
}
