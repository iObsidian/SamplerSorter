package property;

import global.logger.Logger;

public class Property {

	private static final String TAG = "Property";

	private PropertyFileManager e; //property file manager used to save and get values on updates

	private String key; //name in Properties
	private String value; //current value
	private String defaultValue;

	public Property(String keyName, String defaultValue, PropertyFileManager e) {
		this.key = keyName;
		this.defaultValue = defaultValue;
		this.value = e.getPropertyValue(keyName, defaultValue);
		this.e = e;
	}

	public String getValue() {
		return value;
	}

	//String (default)
	public String setNewValue(String value) {
		e.savePropertyValue(key, value);
		this.value = value;
		
		return value;
	}

	//Integer (converted to String)
	public int setNewValue(int i) {
		e.savePropertyValue(key, i + "");
		this.value = i + "";
		
		return i;
	}

	//Boolean (converted to String)
	public boolean setNewValue(Boolean value) {
		String booleanStringValue = Boolean.toString(value).toUpperCase();
		e.savePropertyValue(key, booleanStringValue);
		this.value = booleanStringValue;
		
		return value;
	}

	public boolean getValueAsBoolean() {
		switch (value) {
			case Properties.TRUE:
				return true;
			case Properties.FALSE:
				return false;
			default:
				Logger.logError(TAG, "isTrue() on " + key + " for value " + value
						+ " is impossible. (Boolean string values are case sensitive!)");
				return false;
		}

		//getValue().equals(Properties.TRUE)

		// TODO Auto-generated method stub

	}

	public int getValueAsInt() {

		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			Logger.logError(TAG, "Could not get value as int (value : " + value + ", key : " + key
					+ "), attempting with default value", e);
		}

		try {
			return Integer.parseInt(defaultValue);
		} catch (Exception e) {
			//@formatter:off
			Logger.logError(TAG, "Could not get default value as int (value : " + defaultValue + ", key : " + key + ")", e);
			//@formatter:on
		}

		return 0;
	}
}
