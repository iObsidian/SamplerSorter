package ass;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alde.commons.logger.LoggerPanel;
import alde.commons.util.SplashScreen;
import alde.commons.util.sound.AudioPlayer;
import alde.commons.util.sound.AudioVisualizer;
import alde.commons.util.window.UtilityJFrame;
import ass.action.interfaces.FileAction;
import ass.action.interfaces.UIAction;
import ass.keyboard.macro.MacroEditor;
import ass.ui.CreditsUI;
import ass.ui.SettingsUI;
import constants.icons.iconChooser.Icons;
import constants.icons.iconChooser.IconsLibrary;
import constants.icons.iconChooser.UserIcon;
import constants.property.Properties;
import ui.BasicContainer;

public class ASS extends UtilityJFrame {

	private static final String SOFTWARE_NAME = "AudioSampleSorter";

	static Logger log = LoggerFactory.getLogger(ASS.class);

	public FileManager fileManager = new FileManager();

	private static AudioPlayer audioPlayer = new AudioPlayer();

	private MacroEditor macroEditor;

	private BasicContainer logger = new BasicContainer("Logger", Icons.LOGGER.getImage(), LoggerPanel.get());
	private BasicContainer settings = new BasicContainer("Settings", Icons.SETTINGS.getImage(), new SettingsUI(audioPlayer));
	private BasicContainer credits = new BasicContainer("Credits", Icons.ABOUT.getImage(), new CreditsUI());

	private BasicContainer mainView;

	public static AudioPlayer getAudioPlayer() {
		return audioPlayer;
	}

	/**
	 * Create the frame.
	 * <p>
	 * (Hidden by default)
	 */
	public ASS() {

		UIAction.ASS = this;
		FileAction.ASS = this;

		macroEditor = new MacroEditor();

		macroEditor.macroLoader.registerListeningForMacroChanges(fileManager::macroChanged);

		//Manually trigger it to populate fMan and toolBar
		macroEditor.macroLoader.tellMacroChanged();

		fileManager.registerWaitingForFileChanges(macroEditor.macroLoader::filesChanged);

		System.setProperty("sun.awt.noerasebackground", "true"); //Supposed to reduce flicker on manual window resize (AFAIK doesnt work)

		setResizable(true);
		setBackground(Color.WHITE);
		setTitle(ASS.SOFTWARE_NAME);
		setBounds(100, 100, 655, 493);

		if (Properties.SIZE_WIDTH.isDefaultValue() && Properties.SIZE_HEIGHT.isDefaultValue()) {
			setSize(new Dimension(824, 499));
		} else {
			setSize(new Dimension(Properties.SIZE_WIDTH.getValueAsInt(), Properties.SIZE_HEIGHT.getValueAsInt()));
		}

		// Save width and height on resize
		addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) { //TODO remake this so it doesnt stop screen resizing
				Properties.SIZE_WIDTH.setValue(getWidth());
				Properties.SIZE_HEIGHT.setValue(getHeight());
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

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				close();
			}
		});

		//

		/** Menu bar */

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		/** File */

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		// Import

		JMenuItem mntmChangeFolder = new JMenuItem(new AbstractAction("Set library root folder...") {
			public void actionPerformed(ActionEvent e) {
				log.info("Selecting new sound library...");
				fileManager.changeRootFolder();
			}
		});

		mntmChangeFolder.setIcon(Icons.IMPORT.getImageIcon());
		mnFile.add(mntmChangeFolder);

		// Separator

		mnFile.addSeparator();

		// Exit

		JMenuItem mnExit = new JMenuItem(new AbstractAction("Exit") {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		mnExit.setIcon(Icons.EXIT.getImageIcon());
		mnFile.add(mnExit);

		/* Edit */

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		JMenuItem mntmMacros = new JMenuItem(new AbstractAction("Edit Macros") {
			public void actionPerformed(ActionEvent e) {
				showEditMacros();
			}

		});
		mntmMacros.setIcon(Icons.MACROS.getImageIcon());
		mnEdit.add(mntmMacros);

		// Settings

		JMenuItem mntmSettings = new JMenuItem(new AbstractAction("Settings") {
			public void actionPerformed(ActionEvent e) {
				showSettings();
			}

		});
		mntmSettings.setIcon(Icons.SETTINGS.getImageIcon());
		mnEdit.add(mntmSettings);

		/* Help */

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmConsole = new JMenuItem(new AbstractAction("Show Log") {
			public void actionPerformed(ActionEvent e) {
				showLogger();
			}

		});
		mntmConsole.setIcon(Icons.LOGGER.getImageIcon());
		mnHelp.add(mntmConsole);

		// About

		JMenuItem mntmAbout = new JMenuItem(new AbstractAction("About") {
			public void actionPerformed(ActionEvent e) {
				showCredits();
			}

		});
		mntmAbout.setIcon(Icons.ABOUT.getImageIcon());
		mnHelp.add(mntmAbout);

		// getContentPane().add(AudioVisualiser., BorderLayout.SOUTH);

		// SHOW UI BUTTONS

		menuBar.add(Box.createHorizontalGlue());

		JButton showMenu = new JButton("Menu");
		showMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showMainView();
			}

		});
		menuBar.add(showMenu);

		JButton showConsole = new JButton("Console");
		showConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showLogger();
			}

		});
		menuBar.add(showConsole);

		/** End of menus */

		//

		JSplitPane viewPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		viewPane.setResizeWeight(1);
		viewPane.setDividerLocation(350);

		// Divider moved
		viewPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, pce -> Properties.HORIZONTAL_SPLITPANE_DIVIDERLOCATION.setValue((((Integer) pce.getNewValue()).intValue()) + ""));
		BorderLayout borderLayout = (BorderLayout) fileManager.getLayout();
		borderLayout.setVgap(1);
		borderLayout.setHgap(1);

		//fMan
		viewPane.setTopComponent(fileManager);

		//

		JPanel progressContainer = new JPanel();

		viewPane.setBottomComponent(progressContainer);
		progressContainer.setLayout(new BorderLayout(0, 0));

		JPanel progressPanel = new JPanel();
		progressContainer.add(progressPanel, BorderLayout.SOUTH);
		progressPanel.setLayout(new BorderLayout(0, 0));

		//progressPanel.add(Logger.getStatusField());

		progressContainer.add(AudioVisualizer.getVisualiser(), BorderLayout.CENTER);

		mainView = new BasicContainer(ASS.SOFTWARE_NAME, Icons.DEFAULT_ICON.getImage(), viewPane);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e) {
				setIsListenningForInputs(true);
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
			public void windowGainedFocus(WindowEvent e) {
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
			public void windowDeactivated(WindowEvent e) {
				setIsListenningForInputs(false);
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				setIsListenningForInputs(false);
			}

			private void setIsListenningForInputs(boolean b) {
				macroEditor.macroEditorUI.isListenningForKeyInputs = b;
			}
		});

		if (Properties.FIRST_LAUNCH.getValueAsBoolean()) {
			Properties.FIRST_LAUNCH.setValue(false);
			showCredits();
		}

		showMainView();
	}

	private void close() {
		log.info("Exiting...");

		if (Properties.PROMPT_ON_EXIT.getValueAsBoolean()) {
			String ObjButtons[] = { "Yes", "No" };
			int PromptResult = JOptionPane.showOptionDialog(null, "Are you sure you want to exit?", "Exit?", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
			if (PromptResult == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		} else {
			System.exit(0);
		}
	}

	private void show(BasicContainer viewPane2) {
		getContentPane().removeAll();
		getContentPane().add(viewPane2.getContent(), BorderLayout.CENTER);
		setTitle(viewPane2.getTitle());
		setIconImage(viewPane2.getIconImage());

		revalidate();
		repaint();
	}

	private void showMainView() {
		show(mainView);
	}

	/**
	 * @return true if its paused
	 */
	public boolean resumeOrPauseSound() {
		return audioPlayer.resumeOrPause();
	}

	public void showEditMacros() {
		macroEditor.showMacroEditUI();
	}

	public void showSettings() {
		show(settings);
	}

	public void showLogger() {
		show(logger);
	}

	public void showCredits() {
		show(credits);
	}

	/**
	 * The main entry of the program
	 */
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception weTried) {
			log.error("Error with look and feel", weTried);
		}

		//

		ASS ASS = new ASS();

		ASS.setIconImages(getStaticIconImages());

		final String IMAGE_LOCATION = new File(".").getAbsolutePath() + "/res/splashScreen/";
		try {

			BufferedImage inImage = ImageIO.read(new File(IMAGE_LOCATION + "/BG_BLURRY.png"));
			BufferedImage outImage = ImageIO.read(new File(IMAGE_LOCATION + "/BG.png"));
			BufferedImage textImage = ImageIO.read(new File(IMAGE_LOCATION + "/TITLE.png"));

			SplashScreen s = new SplashScreen(inImage, outImage, textImage);
			s.setRunnableAfterClose(new Runnable() {
				public void run() {
					ASS.setVisible(true);
				}
			});
			s.setVisible(true);

		} catch (IOException e) {
			log.error("Error with SplashScreen!");
			e.printStackTrace();
			log.error("Starting without SplashScreen...");

			ASS.setVisible(true);
		}
	}

	public static List<? extends Image> getStaticIconImages() {
		ArrayList<Image> images = new ArrayList<>();
		UserIcon HUGE = new UserIcon(IconsLibrary.LOCATION_OF_ICONS + "icon_huge.png");
		UserIcon SMALL = new UserIcon(IconsLibrary.LOCATION_OF_ICONS + "icon_small.png");

		images.add(HUGE.getImage());
		images.add(SMALL.getImage());

		return images;
	}

}
