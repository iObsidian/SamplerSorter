package ass.macro.action.type.file;

import java.io.File;

import ass.macro.action.Action;

public abstract class FileAction implements Action, Cloneable {

	public abstract void perform(File p);

	public abstract boolean canBePerformedOnMultipleFiles();

	@Override
	public FileAction clone() throws CloneNotSupportedException {
		return (FileAction) super.clone();
	}

}