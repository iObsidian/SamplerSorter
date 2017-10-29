package samplerSorter.action.type.sound.impl;

import java.io.File;
import java.util.ArrayList;

import samplerSorter.GUI.Sorter;
import samplerSorter.action.editable.EditeableProperty;
import samplerSorter.action.type.Action;
import samplerSorter.logger.Logger;

public class TestAction implements Action {

	@Override
	public String toString() {
		return "TestAction";
	}

	private String TAG = "RenameAction";

	Sorter sorter;

	private EditeableProperty<Boolean> booleanTest = new EditeableProperty<>(true, "Prompt on rename");
	private EditeableProperty<String> stringTest = new EditeableProperty<>("Hey", "New name");
	private EditeableProperty<Integer> integerTest = new EditeableProperty<>(1, "Value");
	private EditeableProperty<File> fileTest = new EditeableProperty<>(new File("."), "File");

	public void init(File f) {

	}

	@Override
	public ArrayList<EditeableProperty> getEditeableProperties() {
		ArrayList<EditeableProperty> editeableProperties = new ArrayList<EditeableProperty>();
		editeableProperties.add(booleanTest);
		editeableProperties.add(stringTest);
		editeableProperties.add(integerTest);
		editeableProperties.add(fileTest);

		return editeableProperties;
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub

	}

}
