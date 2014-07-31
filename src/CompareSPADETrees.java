import javax.swing.*;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class CompareSPADETrees extends JFrame implements KeyListener {

	private static final long serialVersionUID = 1L;

	private static String[][] imageNames;
	private int imageRow, imageCol;
	private static int numSamples;

	private static int numParameters;
	private static PDDocument doc;
	private static File selectedFile;

	private static JLabel label1 = null;
	private static JFrame frame = null;

	private static int IMAGE_QUALITY = 200;

	private static boolean isNotNumber(String input) {
		try {
			Integer.parseInt(input);
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	public void showImage() {

		BufferedImage resized = null;

		try {
			if (doc != null)
				doc.close();

			doc = PDDocument.load(imageNames[imageRow][imageCol]);
			java.util.List pages = doc.getDocumentCatalog().getAllPages();

			PDPage page = (PDPage) pages.get(0);
			BufferedImage image = page.convertToImage(
					BufferedImage.TYPE_INT_RGB, IMAGE_QUALITY);
			resized = resize(image, 800, 800);
			// ip.setImage(resized);

		} catch (IOException e) {
			printRowColumn();
			printImageName();
		}

		ImageIcon image = new ImageIcon(resized);

		if (label1 == null) {
			label1 = new JLabel(" ", image, JLabel.CENTER);
		} else {
			label1.setIcon(image);
		}

		if (frame == null) {
			frame = new JFrame("SPADE Tree Analysis");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(900, 900);
			frame.setResizable(false);
			frame.setLocationRelativeTo(null);

			frame.addKeyListener(this);
			frame.requestFocus();
		}

		frame.getContentPane().add(label1);

		frame.validate();
		frame.setVisible(true);
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_UP:

			if (imageRow != 0) {
				imageRow--;
				this.showImage();
			}

			break;

		case KeyEvent.VK_DOWN:

			if (imageRow != (numParameters - 1)) {
				imageRow++;
				this.showImage();
			}
			break;

		case KeyEvent.VK_LEFT:

			if (imageCol != 0) {
				imageCol--;
				this.showImage();
			}
			break;

		case KeyEvent.VK_RIGHT:

			if (imageCol != (numSamples - 1)) {
				imageCol++;
				this.showImage();
			}
			break;

		}
	}

	private void printRowColumn() {
		System.out.println("Image Row: " + imageRow + " Image Column: "
				+ imageCol);
	}

	private void printImageName() {
		System.out.println(imageNames[imageRow][imageCol]);
	}

	public void keyReleased(KeyEvent e) {
	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH)
			throws IOException {
		return Thumbnails.of(img).size(newW, newH).asBufferedImage();
	}

	public static void main(String[] args) {
		CompareSPADETrees show1 = new CompareSPADETrees();

		// Get Folder///////////////////////////////////////
		JFileChooser folderChooser = new JFileChooser();
		File curDirectory = new File(System.getProperty("user.home"));
		folderChooser.setCurrentDirectory(curDirectory);
		folderChooser.setDialogTitle("Where are the files located?");
		folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int result = folderChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = folderChooser.getSelectedFile();
		}

		// List Files for folder////////////////////////////
		Vector<String> nameStrings = new Vector<String>();

		for (final File fileEntry : selectedFile.listFiles()) {
			if (fileEntry.getName().endsWith("pdf")) {
				nameStrings.add(fileEntry.getAbsolutePath());
			}
		}

		String sampleSize;
		do {
			sampleSize = JOptionPane.showInputDialog(null,
					"How many samples do you have?", "# of samples",
					JOptionPane.QUESTION_MESSAGE);
		} while (isNotNumber(sampleSize));

		numSamples = Integer.parseInt(sampleSize);

		int numPDFs = nameStrings.size();
		numParameters = numPDFs / numSamples;

		imageNames = new String[numParameters][numSamples];

		int index = 0;
		for (int j = 0; j < numSamples; j++) {
			for (int i = 0; i < numParameters; i++) {
				imageNames[i][j] = nameStrings.get(index++);
			}
		}
		
		show1.showImage();
	}

}