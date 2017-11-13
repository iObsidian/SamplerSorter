package global.icons;

import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;

import global.logger.Logger;

public class Icons {

	private static final String TAG = "Icons";

	private static final String LOCATION = new File(".").getAbsolutePath() + "/res/icons/";

	public static final ImageIcon SOFTWARE;
	public static final ImageIcon ICON_CHOOSER;
	public static final ImageIcon SETTINGS;
	public static final ImageIcon IMPORT;
	public static final ImageIcon ABOUT;
	public static final ImageIcon CONSOLE;
	public static final ImageIcon MACROS;

	public static final ImageIcon EXIT; //JMenuItem in File - Exit
	public static final ImageIcon QUESTION;
	public static final ImageIcon FLOATING_LOADING_BAR;

	//public static final ImageIcon DOT; //Previously used by JMenu to show a component as selected (hacked)

	//Default action icons

	public static final String PLAY_ACTION;

	//Dimensions

	public static ImageDimension defaultDimensions = new ImageDimension(16, 16);
	public static ImageDimension softwareIconDimensions = new ImageDimension(64, 64);

	static {
		SOFTWARE = createImageIcon(LOCATION + "software_icon.png", softwareIconDimensions);
		SETTINGS = createImageIcon(LOCATION + "cog.png");
		IMPORT = createImageIcon(LOCATION + "folder-upload.png");
		ABOUT = createImageIcon(LOCATION + "info.png");
		EXIT = createImageIcon(LOCATION + "exit.png");
		CONSOLE = createImageIcon(LOCATION + "menu.png");
		MACROS = createImageIcon(LOCATION + "keyboard.png");
		//DOT = createImageIcon(LOCATION + "dot.png", new ImageDimension(10, 10));
		QUESTION = createImageIcon(LOCATION + "question_mark.png");

		FLOATING_LOADING_BAR = SOFTWARE;
		ICON_CHOOSER = createImageIcon(LOCATION + "question_mark.png");

		//Paths

		PLAY_ACTION = LOCATION + "question_mark.png";
	}

	private static ImageIcon createImageIcon(String path) {
		Logger.logInfo(TAG, "Getting icon " + path + ".");

		return scaleImage(new ImageIcon(path), defaultDimensions);
	}

	static ImageIcon createImageIcon(String path, ImageDimension d) {
		Logger.logInfo(TAG, "Getting icon " + path + ".");

		return scaleImage(new ImageIcon(path), d);
	}

	private static ImageIcon scaleImage(ImageIcon icon, ImageDimension d) {
		int nw = icon.getIconWidth();
		int nh = icon.getIconHeight();

		if (icon.getIconWidth() > d.getWidth()) {
			nw = d.getWidth();
			nh = (nw * icon.getIconHeight()) / icon.getIconWidth();
		}

		if (nh > d.getHeight()) {
			nh = d.getHeight();
			nw = (icon.getIconWidth() * nh) / icon.getIconHeight();
		}

		return new ImageIcon(icon.getImage().getScaledInstance(nw, nh, Image.SCALE_SMOOTH));
	}

}
