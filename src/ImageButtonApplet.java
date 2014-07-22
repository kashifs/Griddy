import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

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
//		System.out.println("Image Row: " + imageRow + " Image Column: "
//				+ imageCol);
	}

	public void printImageName() {
		System.out.println(imageNames[imageRow][imageCol]);
	}

	public void changeImage() throws IOException {
		RandomAccessFile raf = new RandomAccessFile(new File(imageNames[imageRow][imageCol]), "r");
		FileChannel fc = raf.getChannel();
		ByteBuffer buf = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		PDFFile pdfFile = new PDFFile(buf);


		PDFPage page = pdfFile.getPage(0);

		Rectangle2D r2d = page.getBBox();

		double width = r2d.getWidth();
		double height = r2d.getHeight();
		width /= 144.0;
		height /= 144.0;
		int res = Toolkit.getDefaultToolkit().getScreenResolution();
		width *= res;
		height *= res;

		Image image = page.getImage((int) width, (int) height, r2d, null, true, true);

		
//		ip.setImage(images[imageRow][imageCol]);
		ip.setImage(image);
		ip.repaint();
		repaint();
	}
	
	
}
