package ass;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import ass.file.player.AudioPlayer;
import ass.file.player.AudioVisualizer;
import ass.keyboard.action.interfaces.FileAction;
import ass.keyboard.action.interfaces.UIAction;
import ass.keyboard.macro.MacroEditor;
import ass.ui.CreditsUI;
import ass.ui.SettingsUI;
import constants.Constants;
import constants.icons.Icons;
import constants.property.Properties;
import logger.LogUI;
import logger.Logger;
import ui.BasicContainer;
import ui.MiddleOfTheScreen;
import ui.SplashScreen;
import javax.swing.JLabel;

public class ASS extends JFrame {

	private static final String TAG = Constants.SOFTWARE_NAME;

	public FileManager fileBro = new FileManager();

	private static AudioPlayer audioPlayer = new AudioPlayer();

	private MacroEditor macroEditor;

	private BasicContainer logger = new BasicContainer("Log", Icons.LOGGER.getImage(), new LogUI(), true);
	private BasicContainer settings = new BasicContainer("Settings", Icons.SETTINGS.getImage(), new SettingsUI(audioPlayer), false);
	private BasicContainer credits = new BasicContainer("Credits", Icons.ABOUT.getImage(), new CreditsUI(), true);

	public static AudioPlayer getAudioPlayer() {
		return audioPlayer;
	}

	/**
	 * Create the frame.
	 */
	public ASS() {

		UIAction.ASS = this;
		FileAction.ASS = this;

		macroEditor = new MacroEditor();

		macroEditor.macroLoader.registerWaitingForMacroChanges(fileBro);

		//Manually trigger it to populate fMan and toolBar
		macroEditor.macroLoader.tellMacroChanged();

		fileBro.registerWaitingForFileChanges(macroEditor.macroLoader);

		//

		System.setProperty("sun.awt.noerasebackground", "true"); //Suposed to reduce flicker on manual window resize

		setResizable(true);
		setBackground(Color.WHITE);
		setTitle(Constants.SOFTWARE_NAME);
		setBounds(100, 100, 655, 493);

		if (Properties.SIZE_WIDTH.isDefaultValue() && Properties.SIZE_HEIGH.isDefaultValue()) {
			setSize(new Dimension(824, 499));
		} else {
			setSize(new Dimension(Properties.SIZE_WIDTH.getValueAsInt(), Properties.SIZE_HEIGH.getValueAsInt()));

		}

		addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				Properties.SIZE_WIDTH.setNewValue(getWidth());
				Properties.SIZE_HEIGH.setNewValue(getHeight());
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentShown(ComponentEvent e) {
			}
		});

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocation(MiddleOfTheScreen.getMiddleOfScreenLocationFor(this));

		//

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {

				if (Properties.PROMPT_ON_EXIT.getValueAsBoolean()) {
					String ObjButtons[] = { "Yes", "No" };
					int PromptResult = JOptionPane.showOptionDialog(null, "Are you sure you want to exit?", "Exit?", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
							Icons.ABOUT.getImageIcon(), ObjButtons, ObjButtons[1]);
					if (PromptResult == JOptionPane.YES_OPTION) {
						System.exit(0);
					}
				} else {
					System.exit(0);
				}
			}
		});

		/** Menu bar */

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		/** File */

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		// File, Import

		JMenuItem mntmChangeFolder = new JMenuItem(new AbstractAction("Change sound folder...") {
			public void actionPerformed(ActionEvent e) {
				Logger.logInfo(TAG, "Selecting new sound library...");
				fileBro.changeRootFolder();
			}
		});

		mntmChangeFolder.setIcon(Icons.IMPORT.getImageIcon());
		mnFile.add(mntmChangeFolder);

		// File (Separator)

		mnFile.addSeparator();

		//File, Exit

		JMenuItem mnExit = new JMenuItem(new AbstractAction("Exit") {
			public void actionPerformed(ActionEvent e) {
				Logger.logInfo(TAG, "Exiting...");
				System.exit(0);
			}
		});
		mnExit.setIcon(Icons.EXIT.getImageIcon());
		mnFile.add(mnExit);

		//Edit

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		JMenuItem mntmMacros = new JMenuItem(new AbstractAction("Edit Macros") {
			public void actionPerformed(ActionEvent e) {
				showEditMacros(true, true);
			}

		});
		mntmMacros.setIcon(Icons.MACROS.getImageIcon());
		mnEdit.add(mntmMacros);

		//Edit, Settings

		JMenuItem mntmSettings = new JMenuItem(new AbstractAction("Settings") {
			public void actionPerformed(ActionEvent e) {
				showSettings(true, true);
			}

		});
		mntmSettings.setIcon(Icons.SETTINGS.getImageIcon());
		mnEdit.add(mntmSettings);

		//Help

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmConsole = new JMenuItem(new AbstractAction("Show Log") {
			public void actionPerformed(ActionEvent e) {
				showLogger(true, true);
			}
		});
		mntmConsole.setIcon(Icons.LOGGER.getImageIcon());
		mnHelp.add(mntmConsole);

		// Help : About

		JMenuItem mntmAbout = new JMenuItem(new AbstractAction("About") {
			public void actionPerformed(ActionEvent e) {
				showCredits(true, true);
			}
		});
		mntmAbout.setIcon(Icons.ABOUT.getImageIcon());
		mnHelp.add(mntmAbout);

		//getContentPane().add(AudioVisualiser., BorderLayout.SOUTH);

		/** End of menus */

		//

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setResizeWeight(1);
		splitPane.setDividerLocation(350);

		splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent pce) {
				Properties.HORIZONTAL_SPLITPANE_DIVIDERLOCATION.setNewValue((((Integer) pce.getNewValue()).intValue()) + "");
			}
		});
		BorderLayout borderLayout = (BorderLayout) fileBro.getLayout();
		borderLayout.setVgap(1);
		borderLayout.setHgap(1);

		//fMan
		splitPane.setTopComponent(fileBro);

		//

		JPanel container = new JPanel();

		splitPane.setBottomComponent(container);
		container.setLayout(new BorderLayout(0, 0));

		JPanel progressPanel = new JPanel();
		container.add(progressPanel, BorderLayout.SOUTH);
		progressPanel.setLayout(new BorderLayout(0, 0));

		progressPanel.add(Logger.getStatusField());

		container.add(AudioVisualizer.getVisualiser(), BorderLayout.CENTER);

		getContentPane().add(splitPane, BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e) {
				setIsListenningForInputs(true);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				setIsListenningForInputs(false);
			}

			@Override
			public void windowIconified(WindowEvent e) {
				setIsListenningForInputs(false);
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				setIsListenningForInputs(true);
			}

			@Override
			public void windowActivated(WindowEvent e) {
				setIsListenningForInputs(true);
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				setIsListenningForInputs(false);
			}

			@Override
			public void windowGainedFocus(WindowEvent e) {
				setIsListenningForInputs(true);
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				setIsListenningForInputs(false);

			}

			private void setIsListenningForInputs(boolean b) {
				macroEditor.globalKeyListener.isListenningForInputs = b;
			}
		});

		if (Properties.FIRST_LAUNCH.getValueAsBoolean()) {
			Properties.FIRST_LAUNCH.setNewValue(false);
			showCredits(true, true);
		}
	}

	/**
	 * @param forceState enforce new visibility state (if is set to false, component will switch visibility)
	 * @param newState new state of visiblity (if forceState set to false, newState is invalidated)
	 * @return
	 */
	public boolean showCredits(boolean forceState, boolean newState) {
		return toggleVisibility(credits, forceState, newState);
	}

	public boolean showEditMacros(boolean forceState, boolean newState) {
		return toggleVisibility(macroEditor, forceState, newState);
	}

	public boolean showSettings(boolean forceState, boolean newState) {
		return toggleVisibility(settings, forceState, newState);
	}

	public boolean showLogger(boolean forceState, boolean newState) {
		return toggleVisibility(logger, forceState, newState);
	}

	/**
	 * @param c Component to toggleVisibility on
	 * @param forceState If we should force the new visiblity state
	 * @param newState New state (true == set to visible)
	 * @return returns the new state of the component (true == now visible)
	 */
	boolean toggleVisibility(Component c, boolean forceState, boolean newState) {

		if (forceState) {
			c.setVisible(newState);
			return newState;
		} else {
			if (c.isVisible()) {
				c.setVisible(false);
				return false;
			} else {
				c.setVisible(true);
				return true;
			}
		}
	}

	/**
	 * @return true if its paused
	 */
	public boolean resumeOrPauseSound() {
		return audioPlayer.resumeOrPause();
	}

	/**
	 * The main entry of the program
	 */
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception weTried) {
			Logger.logError(TAG, "Error with look and feel", weTried);
		}

		//

		ASS ASS = new ASS();

		ArrayList<Image> images = new ArrayList<Image>();
		images.add(Icons.BIG_ICON.getImage());
		images.add(Icons.SMALL_ICON.getImage());
		ASS.setIconImages(images);

		ASS.setIconImage(Icons.BIG_ICON.getImage());

		System.out.println(Icons.SMALL_ICON.getImagePath());

		final String IMAGE_LOCATION = new File(".").getAbsolutePath() + "/res/splashScreen/";
		try {

			BufferedImage inImage = ImageIO.read(new File(IMAGE_LOCATION + "/BG_BLURRY.png"));
			BufferedImage outImage = ImageIO.read(new File(IMAGE_LOCATION + "/BG.png"));
			BufferedImage textImage = ImageIO.read(new File(IMAGE_LOCATION + "/TITLE.png"));
			Image icon = Icons.LOADING_BAR.getImage();

			new SplashScreen(icon, inImage, outImage, textImage, Constants.SOFTWARE_NAME, ASS);

		} catch (IOException e) {
			Logger.logError(TAG, "Error with SplashScreen!");
			e.printStackTrace();
			Logger.logError(TAG, "Starting without SplashScreen...");

			ASS.setVisible(true);
		}
	}

}
