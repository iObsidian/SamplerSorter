package constants.property;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import constants.property.Property;
import constants.property.PropertyFileManager;
import logger.Logger;

public class Properties {

	private static PropertyFileManager e = new PropertyFileManager("ass.properties");

	// Booleans

	public static final String TRUE = "TRUE";
	public static final String FALSE = "FALSE";

	public static Property ROOT_FOLDER = new Property("ROOT_FOLDER", "C:\\", e);

	//Checked before launching the UI, if set to TRUE will open Credits
	public static Property FIRST_LAUNCH = new Property("FIRST_LAUNCH", TRUE, e);

	//Prompt on close, are you sure to exit?
	public static Property PROMPT_ON_EXIT = new Property("PROMPT_ON_EXIT", TRUE, e);

	//Prompt on close, are you sure to exit?
	public static Property SIZE_WIDTH = new Property("SIZE_X", TRUE, e);
	//Prompt on close, are you sure to exit?
	public static Property SIZE_HEIGH = new Property("SIZE_Y", TRUE, e);

	//RunSS UI checkBox 'include subfolders'
	public static Property INCLUDE_SUBFOLDERS = new Property("INCLUDE_SUBFOLDERS", TRUE, e);

	//Set in Settings and checked by RunSS to know if .wav or .mp3 should be displayed in SoundPanel
	public static Property DISPLAY_SOUND_SUFFIXES = new Property("DISPLAY_SOUND_SUFFIXES", TRUE, e);

	//Used by FileInformation to know wiether of not to display file date
	public static Property DISPLAY_FILE_DATE = new Property("DISPLAY_FILE_DATE", FALSE, e);

	//Used by Settings to adjust volume
	public static Property MAIN_VOLUME_SLIDER_VALUE = new Property("MAIN_VOLUME_SLIDER_VALUE", "20", e);

	//Main pan slider value
	public static Property MAIN_PAN_SLIDER_VALUE = new Property("MAIN_PAN_SLIDER_VALUE", "50", e);

	//RunSS PlayOnClickChbkx and on SoundPanel click, to check if the sound should be played
	public static Property PLAY_ON_CLICK = new Property("PLAY_ON_CLICK", TRUE, e);

	//Used by RunSS onJFileChooser to reset the location where the jfilechooser was
	public static Property SPECTRUM_ANALYZER_STATUS = new Property("SPECTRUM_ANALYZER_STATUS", "oscillo", e);

	public static Property HORIZONTAL_SPLITPANE_DIVIDERLOCATION = new Property("SPLITPANE_DIVIDERLOCATION", "200", e);

	public static Property LIBRARY_LOCATION = new Property("LIBRAIRY_LOCATION", "C:\\", e);

}


class PropertyFileManager {

	private final static boolean DEBUG = false;

	private String TAG = "PropertyFileManager";

	private String fileName = "null";

	public PropertyFileManager(String fileName) {
		this.fileName = fileName;

		Logger.logInfo(TAG, "Restoring properties from " + fileName + ".");
	}

	public void savePropertyValue(String key, String value) {

		if (DEBUG) {
			Logger.logInfo(TAG, "Saving property " + key + " with value " + value + ".");
		}

		createFileIfDoesNotExist();

		PropertiesConfiguration config;
		try {
			config = new PropertiesConfiguration(fileName);
			config.setProperty(key, value);
			config.save();
		} catch (ConfigurationException e) {
			Logger.logError(TAG, "Error while setting property " + key + " from " + fileName + ".", e);
			e.printStackTrace();
		}
	}

	public String getPropertyValue(String key, String defaultValue) {

		if (DEBUG) {
			Logger.logInfo(TAG, "Getting property " + key + ".");
		}

		createFileIfDoesNotExist();

		try {
			PropertiesConfiguration config = new PropertiesConfiguration(fileName);
			String value = (String) config.getProperty(key);

			if (value == null) { //file might be empty
				return defaultValue;
			} else {
				return value;
			}

		} catch (ConfigurationException e) {
			Logger.logError(TAG, "Could not get property value for " + key + ", returning default value. ", e);
			e.printStackTrace();
		}
		return defaultValue;
	}

	private void createFileIfDoesNotExist() {
		try {
			new File(fileName).createNewFile(); // if file already exists will do nothing 
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}