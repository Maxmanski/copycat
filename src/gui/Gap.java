package gui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;

public class Gap {
	
	public static final int X=0, Y=1;
	
	private Gap(){
	}
	
	public static Component createGap(int axis, int width){
		if(axis==Gap.X){
			return Box.createRigidArea(new Dimension(width, 0));
		}else if(axis==Gap.Y){
			return Box.createRigidArea(new Dimension(0, width));
		}else{
			return null;
		}
	}
}
