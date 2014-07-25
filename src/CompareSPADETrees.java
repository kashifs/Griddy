import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class CompareSPADETrees extends Applet implements KeyListener {

	private static final long serialVersionUID = 1L;

	private ImagePanel ip;
	private String[][] imageNames;
	private int imageRow, imageCol;
	private int numSamples, numParameters;
	private static PDDocument doc;
	private File selectedFile;

	private static int IMAGE_QUALITY = 200;

	private boolean arrowPressed = false;

	private class ImagePanel extends Panel {

		private static final long serialVersionUID = 1L;

		private Image i;

		ImagePanel(Image im) {
			super();
			setImage(im);
		}

		public void setImage(Image im) {
			MediaTracker mt = new MediaTracker(this);
			mt.addImage(im, 0, 500, 500);

			try {
				mt.waitForAll();
			} catch (InterruptedException x) {
				System.err.println("Error loading image!");
			}
			i = im;
		}

		public Dimension getPreferredSize() {
			int w = i.getWidth(this);
			int h = i.getHeight(this);
			System.out.println("" + w + " " + h);
			return (new Dimension(w, h));
		}

		public void paint(Graphics g) {
			if (i != null) {
				g.drawImage(i, 0, 0, this);
			}
		}
	}

	private void chooseFolder() {
		JFileChooser folderChooser = new JFileChooser();
		File curDirectory = new File(System.getProperty("user.home"));
		folderChooser.setCurrentDirectory(curDirectory);
		folderChooser.setDialogTitle("Where are the files located?");
		folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int result = folderChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = folderChooser.getSelectedFile();
		}
	}

	private boolean isNotNumber(String input) {
		try {
			Integer.parseInt(input);
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	private void askNumSamples() {
		String sampleSize;
		do {
			sampleSize = JOptionPane.showInputDialog(null,
					"How many samples do you have?", "# of samples",
					JOptionPane.QUESTION_MESSAGE);
		} while (isNotNumber(sampleSize));

		numSamples = Integer.parseInt(sampleSize);
	}

	public void init() {
		this.setSize(800, 800);

		chooseFolder();
		askNumSamples();
		listFilesForFolder();

		imageCol = 0;
		imageRow = 0;

		setLayout(new BorderLayout());

		Image img = getImage(getCodeBase(),
				"/Users/kashif/Projects/Gastro/Griddy/images/arrows.png");

		ip = new ImagePanel(img);

		add(ip, BorderLayout.CENTER);
		ip.addKeyListener(this);
		ip.requestFocus();
	}

	public void listFilesForFolder() {
		Vector<String> nameStrings = new Vector<String>();

		for (final File fileEntry : selectedFile.listFiles()) {
			if (fileEntry.getName().endsWith("pdf")) {
				nameStrings.add(fileEntry.getAbsolutePath());
			}
		}

		int numPDFs = nameStrings.size();
		numParameters = numPDFs / numSamples;

		imageNames = new String[numParameters][numSamples];

		int index = 0;
		for (int j = 0; j < numSamples; j++)
			for (int i = 0; i < numParameters; i++) {
				imageNames[i][j] = nameStrings.get(index++);
			}
	}



	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_UP:
			if (!arrowPressed) {
				arrowPressed = true;
				changeImage();
				break;
			}

			if (imageRow != 0) {
				imageRow--;
				changeImage();
			}
			break;

		case KeyEvent.VK_DOWN:
			if (!arrowPressed) {
				arrowPressed = true;
				changeImage();
				break;
			}

			if (imageRow != (numParameters - 1)) {
				imageRow++;
				changeImage();
			}
			break;

		case KeyEvent.VK_LEFT:
			if (!arrowPressed) {
				arrowPressed = true;
				changeImage();
				break;
			}

			if (imageCol != 0) {
				imageCol--;
				changeImage();
			}
			break;

		case KeyEvent.VK_RIGHT:
			if (!arrowPressed) {
				arrowPressed = true;
				changeImage();
				break;
			}

			if (imageCol != (numSamples - 1)) {
				imageCol++;
				changeImage();
			}
			break;

		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	private void printRowColumn() {
		System.out.println("Image Row: " + imageRow + " Image Column: "
				+ imageCol);
	}

	private void printImageName() {
		System.out.println(imageNames[imageRow][imageCol]);
	}

	public void changeImage() {
		try {
			if (doc != null)
				doc.close();

			doc = PDDocument.load(imageNames[imageRow][imageCol]);
			java.util.List pages = doc.getDocumentCatalog().getAllPages();

			// PDPage page = (PDPage)pages.get(0);
			// BufferedImage image =
			// page.convertToImage(BufferedImage.TYPE_INT_RGB, 200);
			// BufferedImage resized = resize(image, 800, 800);
			// ip.setImage(resized);

			// combine above calls to make faster
			ip.setImage(resize(((PDPage) pages.get(0)).convertToImage(
					BufferedImage.TYPE_INT_RGB, IMAGE_QUALITY), 800, 800));
			ip.repaint();
			repaint();
		} catch (IOException e) {
			printRowColumn();
			printImageName();
		}

	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH)
			throws IOException {
		return Thumbnails.of(img).size(newW, newH).asBufferedImage();
	}

}
