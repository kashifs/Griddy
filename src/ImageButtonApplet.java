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

public class ImageButtonApplet extends Applet implements ActionListener,
		KeyListener {
	private Vector v;

	private Button b;
	private ImagePanel ip;
	private Image[][] images;
	private String[][] imageNames;
	private int imageRow, imageCol;
	private int numRows = 36;
	private int numCols = 9;
	private int numSamples;
	private int numParameters;

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

	public void init() {

		numSamples = 9;
		numParameters = 122;

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

		// System.out.println(fileName);

		images = new Image[numRows][numCols];
		imageCol = 0;
		imageRow = 0;

		v = new Vector(10);

		setLayout(new BorderLayout());
		images[0][0] = getImage(getCodeBase(), "/Users/kashif/Desktop/air1.jpg");
		images[0][1] = getImage(getCodeBase(), "/Users/kashif/Desktop/air2.jpg");
		images[0][2] = getImage(getCodeBase(), "/Users/kashif/Desktop/air3.jpg");
		images[0][2] = getImage(getCodeBase(), "/Users/kashif/Desktop/air4.jpg");

		ip = new ImagePanel(images[0][0]);
		add(ip, BorderLayout.CENTER);
		ip.addKeyListener(this);
		ip.requestFocus();
	}

	public void listFilesForFolder(final File folder) {
		// int i = 0;
		// int j = 0;
		// int k = 0;

		int numPDFs = 0;
		Vector<String> nameStrings = new Vector<String>();

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				if (fileEntry.getName().endsWith("pdf")) {
					System.out.println(fileEntry.getAbsolutePath());
					nameStrings.add(fileEntry.getAbsolutePath());
				}
			}
		}

		numPDFs = nameStrings.size();

		System.out.println("NumPDFs: " + numPDFs);
		System.out.println("NumParamaters: " + numParameters);
		System.out.println("Samples: " + numPDFs / numParameters);

		imageNames = new String[numParameters][(numPDFs / numParameters)];
		int index = 0;

		for (int j = 0; j < numPDFs / numParameters; j++)
			for (int i = 0; i < numParameters; i++) {
				imageNames[i][j] = nameStrings.get(index++);
			}
	}

	public void actionPerformed(ActionEvent e) {
		ip.repaint();
		repaint();
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_UP:
			// System.out.println("up");

			if (imageRow != 0) {
				imageRow--;
				printRowColumn();
				printImageName();
				try {
					changeImage();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			break;

		case KeyEvent.VK_DOWN:
			// System.out.println("down");

			if (imageRow != (numRows - 1)) {
				imageRow++;
				printRowColumn();
				printImageName();
				try {
					changeImage();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			break;

		case KeyEvent.VK_LEFT:
			// System.out.println("left");

			if (imageCol != 0) {
				imageCol--;
				printRowColumn();
				printImageName();
				try {
					changeImage();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			break;

		case KeyEvent.VK_RIGHT:
			// System.out.println("right");

			if (imageCol != (numCols - 1)) {
				imageCol++;
				printRowColumn();
				printImageName();
				try {
					changeImage();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
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
		// System.out.println("Image Row: " + imageRow + " Image Column: "
		// + imageCol);
	}

	public void printImageName() {
		System.out.println(imageNames[imageRow][imageCol]);
	}

	public static Image convertPDFToJPG(String src) {

		try {

			// load pdf file in the document object
			PDDocument doc = PDDocument.load(new FileInputStream(src));
			// Get all pages from document and store them in a list
			java.util.List pages = doc.getDocumentCatalog().getAllPages();
			// create iterator object so it is easy to access each page from the
			// list
			Iterator<PDPage> i = pages.iterator();
			int count = 1; // count variable used to separate each image file
			// Convert every page of the pdf document to a unique image file
			System.out.println("Please wait...");
			while (i.hasNext()) {
				PDPage page = i.next();
				BufferedImage bi = page.convertToImage();
				ImageIO.write(bi, "jpg", new File("/Users/kashif/Desktop/pdfimage_1.jpg"));
				count++;
			}
			System.out.println("Conversion complete");
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		return null;
	}

	public void changeImage() throws IOException {
		
		convertPDFToJPG("/Users/kashif/Desktop/0_control.fcs copy_CD45RA-_count.pdf");
		
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("/Users/kashif/Desktop/pdfimage_1.jpg"));
			ip.setImage(img);
			ip.repaint();
			repaint();
		} catch (IOException e) {
		}
		

	}

}
