package ui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alde.commons.util.window.UtilityJFrame;

public class BasicContainer extends UtilityJFrame {

	static Logger log = LoggerFactory.getLogger(BasicContainer.class);

	/**
	 * Panels contained in the Container need to be static. (Irrelevant to any other components)
	 * The Container setBounds(); to the content's bounds 
	 */
	public BasicContainer(String title, Image iconImage, JPanel content, boolean isResizeable) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception weTried) {
			log.error("BasicContainer (" + title + ")", "Error with look and feel", weTried);
		}

		setIconImage(iconImage);
		setResizable(isResizeable);
		setTitle(title);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		if (content != null) {
			setBounds(content.getBounds());
			setContentPane(content);
		}

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});

		pack();

	}

	public BasicContainer(String title, Image iconImage, JComponent component, boolean isResizeable) {
		this(title, iconImage, generatePanel(component), isResizeable);
	}

	private static JPanel generatePanel(JComponent component) {

		JScrollPane jscp = new JScrollPane();
		jscp.setViewportView(component);

		JPanel p = new JPanel(new BorderLayout());
		p.add(jscp, BorderLayout.CENTER);

		return p;
	}

}
