package samplerSorter.action.editable;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import samplerSorter.logger.Logger;

/**
 * See ActionEditor
 */
public class EditPropertyPanel extends JPanel {

	private static final String TAG = "EditPropertyPanel";

	private static final int WIDTH = 450;
	private static final int HEIGHT = 44;

	//

	private EditeableProperty propertyToEdit;

	//

	public EditPropertyPanel(EditeableProperty property) {

		propertyToEdit = property;

		setSize(new Dimension(WIDTH, HEIGHT)); //default dimensions
		setLayout(new FlowLayout(FlowLayout.LEFT)); //default layout

		if (propertyToEdit.ID == EditeableProperty.BOOLEAN_ID) { //JCHECKBOX

			setLayout(null);

			JCheckBox chckbxNewCheckBox = new JCheckBox(propertyToEdit.prefix);
			chckbxNewCheckBox.setBounds(8, 9, 434, 25);
			chckbxNewCheckBox.setSelected((Boolean) propertyToEdit.getValue());
			add(chckbxNewCheckBox);

			ActionListener act = new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					Logger.logInfo(TAG, "setNewValue : boolean");
					propertyToEdit.setValue(chckbxNewCheckBox.isSelected());
				}
			};
			chckbxNewCheckBox.addActionListener(act);

		} else if (propertyToEdit.ID == EditeableProperty.STRING_ID) { //JTEXTFIELD

			JLabel lblPrefix = new JLabel(propertyToEdit.prefix);
			lblPrefix.setBounds(12, 16, 56, 16);
			add(lblPrefix);

			JTextField stringInputField = new JTextField();
			stringInputField.setEnabled(true);
			stringInputField.setEditable(true);
			stringInputField.setBounds(80, 13, 135, 22);
			stringInputField.setColumns(10);
			add(stringInputField);

		} else if (propertyToEdit.ID == EditeableProperty.INT_ID) { //JTEXTFIELD that only accepts ints

			setLayout(new FlowLayout(FlowLayout.LEFT));

			JLabel lblPrefix = new JLabel(propertyToEdit.prefix);
			lblPrefix.setBounds(12, 16, 56, 16);
			add(lblPrefix);

			JTextField intInputField = new JTextField();
			intInputField.setEnabled(true);
			intInputField.setEditable(true);
			intInputField.setBounds(80, 13, 135, 22);
			intInputField.setColumns(10);
			add(intInputField);

		} else if (propertyToEdit.ID == EditeableProperty.FILE_CHOOSER_ID) { //JFILECHOOSER

			//Select a single file/folder

			JFileChooser chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(false);
			chooser.setPreferredSize(new Dimension(800, 600));
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			JLabel lblPrefix = new JLabel(propertyToEdit.prefix);
			add(lblPrefix);

			JButton btnOpenFileBrowser = new JButton("Browse files");
			btnOpenFileBrowser.setToolTipText("Open file browser");
			btnOpenFileBrowser.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
						System.out.println("getSelectedFile() : " + chooser.getSelectedFile());

						String directory = chooser.getCurrentDirectory().toString();

						Logger.logInfo(TAG, "setNewValue : file");
						propertyToEdit.setValue(chooser.getSelectedFile());
					}
				}
			});
			add(btnOpenFileBrowser);

		} else {
			Logger.logInfo("EditPropertyPanel", "Unimplemented propertyToEdit");
		}

	}
}
