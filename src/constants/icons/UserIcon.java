package constants.icons;

import java.awt.Image;
import java.io.Serializable;

import javax.swing.ImageIcon;

/**
 * An icon stored on the user's disk
 */
public class UserIcon implements Serializable {
	private static final long serialVersionUID = 1L;

	private String imagePath;
	private ImageIcon imageIcon;

	//Default dimensions (images are stored in memory with this resolution)
	private static final int DEFAULT_HEIGHT = 16;
	private static final int DEFAULT_WIDTH = 16;

	public UserIcon(String pathOfImage) {
		this(pathOfImage, DEFAULT_HEIGHT, DEFAULT_WIDTH);
	}

	private UserIcon(String pathOfImage, int height, int width) {
		this.imageIcon = Icons.createImageIcon(pathOfImage, height, width);
		this.imagePath = pathOfImage;
	}

	public String getImagePath() {
		return imagePath;
	}

	public Image getImage() {
		return imageIcon.getImage();
	}

	public ImageIcon getImageIcon() {
		return imageIcon;
	}

	public boolean containsString(String searchForValue, boolean ignoreCase) {

		if (ignoreCase) {
			return (getImagePath().toLowerCase().contains(searchForValue.toLowerCase()));
		} else {
			return (getImagePath().contains(searchForValue));
		}
		
		
		

	}

}