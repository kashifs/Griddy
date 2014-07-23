import java.applet.*;
import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class ImageButtonApplet extends Applet implements KeyListener {

	private ImagePanel ip;
	private String[][] imageNames;
	private int imageRow, imageCol;
	private int numRows = 36;
	private int numCols = 9;
	private int numParameters;
	private static PDDocument doc;

	private class ImagePanel extends Panel {
		private Image i;

		ImagePanel(Image im) {
			super();
			setImage(im);
		}

		public void setImage(Image im) {
			MediaTracker mt = new MediaTracker(this);
			mt.addImage(im, 0);
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
		folderChooser.setCurrentDirectory(new File(System
				.getProperty("user.home")));
		folderChooser.setDialogTitle("Where are the files located?");
		folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int result = folderChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = folderChooser.getSelectedFile();
			System.out.println("SELECTED FILE: " + selectedFile.toString());
			listFilesForFolder(selectedFile);
		}
	}

	public void init() {
		numParameters = 122;

		chooseFolder();

		imageCol = 0;
		imageRow = 0;

		setLayout(new BorderLayout());

		Image img = getImage(getCodeBase(), "/Users/kashif/Desktop/air1.jpg");
		ip = new ImagePanel(img);
		add(ip, BorderLayout.CENTER);
		ip.addKeyListener(this);
		ip.requestFocus();
	}

	public void listFilesForFolder(final File folder) {
		Vector<String> nameStrings = new Vector<String>();

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				if (fileEntry.getName().endsWith("pdf")) {
					// System.out.println(fileEntry.getAbsolutePath());
					nameStrings.add(fileEntry.getAbsolutePath());
				}
			}
		}

		int numPDFs = nameStrings.size();
		int numSamples = numPDFs / numParameters;

		// System.out.println("NumPDFs: " + numPDFs);
		// System.out.println("NumParamaters: " + numParameters);
		// System.out.println("Samples: " + numPDFs / numParameters);

		imageNames = new String[numParameters][(numSamples)];
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

			if (imageRow != 0) {
				imageRow--;
				printRowColumn();
				printImageName();
				try {
					changeImage();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			break;

		case KeyEvent.VK_DOWN:

			if (imageRow != (numRows - 1)) {
				imageRow++;
				printRowColumn();
				printImageName();
				try {
					changeImage();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			break;

		case KeyEvent.VK_LEFT:

			if (imageCol != 0) {
				imageCol--;
				printRowColumn();
				printImageName();
				try {
					changeImage();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			break;

		case KeyEvent.VK_RIGHT:

			if (imageCol != (numCols - 1)) {
				imageCol++;
				printRowColumn();
				printImageName();
				try {
					changeImage();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			break;

		}
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void printRowColumn() {
		System.out.println("Image Row: " + imageRow + " Image Column: "
				+ imageCol);
	}

	public void printImageName() {
		System.out.println(imageNames[imageRow][imageCol]);
	}


	public void changeImage() throws IOException {
		if(doc != null)
			doc.close();
		
//		convertPDFToJPG(imageNames[imageRow][imageCol]);

		
		try {
			// load pdf file in the document object
			doc = PDDocument.load(new FileInputStream(imageNames[imageRow][imageCol]));
			// Get all pages from document and store them in a list
			java.util.List pages = doc.getDocumentCatalog().getAllPages();

			PDPage page = (PDPage) pages.get(0);

			

			ip.setImage(page.convertToImage());
			ip.repaint();
			repaint();
		} catch (IOException e) {
		}

	}

}
