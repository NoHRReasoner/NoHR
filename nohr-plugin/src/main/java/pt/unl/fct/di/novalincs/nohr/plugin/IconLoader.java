package pt.unl.fct.di.novalincs.nohr.plugin;

import javax.swing.ImageIcon;
import javax.swing.plaf.synth.SynthSpinnerUI;

public class IconLoader {
	
	/** 
	 * Returns an ImageIcon, or null if the path was invalid. 
	 */
	public static ImageIcon getImageIcon(String path) {
		java.net.URL imgURL = IconLoader.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			return null;
		}
	}
}
