import javax.swing.*;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CompareSPADETrees extends JFrame implements KeyListener {

	private static final long serialVersionUID = 1L;

	private static final int MEDIAN_METRIC = 0;
	private static final int CVS_METRIC = 1;
	private static final int FOLD_METRIC = 2;
	private static final int RAW_MEDIAN_METRIC = 3;
	private static final int RAW_FOLD_METRIC = 4;
	private static final int EXTRA_METRIC = 5;

	private static final int KEYCODE_1 = 49;
	private static final int KEYCODE_2 = 50;
	private static final int KEYCODE_3 = 51;
	private static final int KEYCODE_4 = 52;
	private static final int KEYCODE_5 = 53;
	private static final int KEYCODE_6 = 54;

	private static final int NTHREADS = 4;

	private static final int UP_POSITION = 0;
	private static final int DOWN_POSITION = 1;
	private static final int LEFT_POSITION = 2;
	private static final int RIGHT_POSITION = 3;

	private static int position = 0;

	private static ImageIcon[] images = new ImageIcon[4];

	private static String[][][] imageNames;
	private static String[][] rawMedianNames, rawFoldNames, medianNames,
			cvsNames, foldNames, extraNames;
	private static int metricNum, imageRow, imageCol;

	private static int numSamples;

	private static PDDocument doc;
	private static File selectedFile;

	private static JLabel label = null;
	private static JFrame frame = null;

	private static int IMAGE_QUALITY = 200;

	private static PDPage page;
	private static BufferedImage resized, image;

	private static boolean isNotNumber(String input) {
		try {
			Integer.parseInt(input);
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	private static void populateImages() {

		String[][] sameMetric = imageNames[metricNum];

		ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);

		if (imageRow != 0) {
			Runnable upWorker = new CacheImage(
					sameMetric[imageRow - 1][imageCol], images, UP_POSITION);
			executor.execute(upWorker);
		}

		if (imageRow != (imageNames[metricNum].length - 1)) {
			Runnable downWorker = new CacheImage(
					sameMetric[imageRow + 1][imageCol], images, DOWN_POSITION);
			executor.execute(downWorker);
		}

		if (imageCol != 0) {
			Runnable leftWorker = new CacheImage(
					sameMetric[imageRow][imageCol - 1], images, LEFT_POSITION);
			executor.execute(leftWorker);
		}

		if (imageCol != numSamples - 1) {
			Runnable rightWorker = new CacheImage(
					sameMetric[imageRow][imageCol + 1], images, RIGHT_POSITION);
			executor.execute(rightWorker);
		}

		executor.shutdown();
	}

	public synchronized void showImage() {

		resized = null;
		ImageIcon iconImage = null;
		if (images[position] == null) { //'images == null'??
			try {
				System.out.println("It was null");
				doc = PDDocument
						.load(imageNames[metricNum][imageRow][imageCol]);

				java.util.List pages = doc.getDocumentCatalog().getAllPages();

				page = (PDPage) pages.get(0);
				image = page.convertToImage(BufferedImage.TYPE_INT_RGB,
						IMAGE_QUALITY);
				resized = resize(image, 800, 800);

			} catch (IOException e) {
				printMetricRowColumn();
				printImageName();
			}

			iconImage = new ImageIcon(resized);
		} else {
			System.out.println("it was not null");
			iconImage = images[position];
		}

		if (label == null) {
			label = new JLabel(null, iconImage, JLabel.CENTER);
		} else {
			label.setIcon(iconImage);
		}

		if (frame == null) {
			frame = new JFrame("SPADE Tree Analysis");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(900, 900);
			frame.setLocationRelativeTo(null);

			frame.addKeyListener(this);
			frame.requestFocus();
		}

		frame.getContentPane().add(label);

		frame.validate();
		frame.setVisible(true);

		try {
			if (doc != null)
				doc.close();
		} catch (IOException e) {
			System.out.println("Error closing document.");
		}

		populateImages();
	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH)
			throws IOException {
		return Thumbnails.of(img).size(newW, newH).asBufferedImage();
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		switch (keyCode) {
		case KeyEvent.VK_UP:

			if (imageRow != 0) {
				imageRow--;
				position = UP_POSITION;
				showImage();
			}
			break;

		case KeyEvent.VK_DOWN:

			int numParameters = imageNames[metricNum].length;
			if (imageRow != (numParameters - 1)) {
				imageRow++;
				position = DOWN_POSITION;
				showImage();
			}
			break;

		case KeyEvent.VK_LEFT:

			if (imageCol != 0) {
				imageCol--;
				position = LEFT_POSITION;
				showImage();
			}
			break;

		case KeyEvent.VK_RIGHT:

			if (imageCol != (numSamples - 1)) {
				imageCol++;
				position = RIGHT_POSITION;
				showImage();
			}
			break;

		case KEYCODE_1:

			metricNum = MEDIAN_METRIC;
			showImage();
			break;

		case KEYCODE_2:

			metricNum = CVS_METRIC;
			showImage();
			break;

		case KEYCODE_3:

			metricNum = FOLD_METRIC;
			showImage();
			break;

		case KEYCODE_4:

			metricNum = RAW_MEDIAN_METRIC;
			showImage();
			break;

		case KEYCODE_5:

			metricNum = RAW_FOLD_METRIC;
			showImage();
			break;

		case KEYCODE_6:

			metricNum = EXTRA_METRIC;
			showImage();
			break;

		}
	}

	private static void printMetricRowColumn() {
		System.out.println("Metric Number: " + metricNum);
		System.out.println("Image Row: " + imageRow);
		System.out.println("Image Column: " + imageCol);
	}

	private static void printImageName() {
		System.out.println("Image Filename: "
				+ imageNames[metricNum][imageRow][imageCol]);
	}

	public void keyReleased(KeyEvent e) {
	}

	private static void promptFolder() {
		JFileChooser folderChooser = new JFileChooser();
		folderChooser.setDialogTitle("Where are the files located?");
		folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int result = folderChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = folderChooser.getSelectedFile();
		}
	}

	private static void promptSampleSize() {

		String sampleSize;
		do {
			sampleSize = JOptionPane.showInputDialog(null,
					"How many samples do you have?", "# of samples",
					JOptionPane.QUESTION_MESSAGE);
		} while (isNotNumber(sampleSize));

		numSamples = Integer.parseInt(sampleSize);
	}

	private static void printMemoryStatistics() {
		int mb = 1024 * 1024;

		// Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();

		System.out.println("##### Heap utilization statistics [MB] #####");

		// Print used memory
		System.out.println("Used Memory:"
				+ (runtime.totalMemory() - runtime.freeMemory()) / mb);

		// Print free memory
		System.out.println("Free Memory:" + runtime.freeMemory() / mb);

		// Print total available memory
		System.out.println("Total Memory:" + runtime.totalMemory() / mb);

		// Print Maximum available memory
		System.out.println("Max Memory:" + runtime.maxMemory() / mb);
	}

	public static void main(String[] args) throws IOException {
		promptFolder();

		numSamples = 0;
		imageRow = 0;
		imageCol = 0;
		metricNum = 0;

		// List Files for folder////////////////////////////
		Vector<String> rawFoldStrings = new Vector<String>();
		Vector<String> rawMedianStrings = new Vector<String>();
		Vector<String> medianStrings = new Vector<String>();
		Vector<String> cvsStrings = new Vector<String>();
		Vector<String> foldStrings = new Vector<String>();
		Vector<String> extraStrings = new Vector<String>();

		for (final File fileEntry : selectedFile.listFiles()) {
			String fileName = fileEntry.getAbsolutePath();

			if (fileName.contains("_raw_medians")) {
				rawMedianStrings.add(fileName);
			} else if (fileName.contains("_raw_fold")) {
				rawFoldStrings.add(fileName);
			} else if (fileName.contains("_medians")) {
				medianStrings.add(fileName);
			} else if (fileName.contains("_cvs")) {
				cvsStrings.add(fileName);
			} else if (fileName.contains("_fold")) {
				foldStrings.add(fileName);
			} else {
				extraStrings.add(fileName);
			}

			if (fileName.endsWith("_count.pdf")) {
				numSamples++;
			}
		}

		int numRawMedianParams = rawMedianStrings.size() / numSamples;
		int numRawFoldParams = rawFoldStrings.size() / numSamples;
		int numMedianParams = medianStrings.size() / numSamples;
		int numCVSParams = cvsStrings.size() / numSamples;
		int numFoldParams = foldStrings.size() / numSamples;
		int numExtraParams = extraStrings.size() / numSamples;

		rawMedianNames = new String[numRawMedianParams][numSamples];
		rawFoldNames = new String[numRawFoldParams][numSamples];
		medianNames = new String[numMedianParams][numSamples];
		cvsNames = new String[numCVSParams][numSamples];
		foldNames = new String[numFoldParams][numSamples];
		extraNames = new String[numExtraParams][numSamples];

		int index = 0;

		for (int j = 0; j < numSamples; j++) {
			for (int i = 0; i < numRawMedianParams; i++) {
				rawMedianNames[i][j] = rawMedianStrings.get(index++);
			}
		}

		index = 0;
		for (int j = 0; j < numSamples; j++) {
			for (int i = 0; i < numRawFoldParams; i++) {
				rawFoldNames[i][j] = rawFoldStrings.get(index++);
			}
		}

		index = 0;
		for (int j = 0; j < numSamples; j++) {
			for (int i = 0; i < numMedianParams; i++) {
				medianNames[i][j] = medianStrings.get(index++);
			}
		}

		index = 0;
		for (int j = 0; j < numSamples; j++) {
			for (int i = 0; i < numCVSParams; i++) {
				cvsNames[i][j] = cvsStrings.get(index++);
			}
		}

		index = 0;
		for (int j = 0; j < numSamples; j++) {
			for (int i = 0; i < numFoldParams; i++) {
				foldNames[i][j] = foldStrings.get(index++);
			}
		}

		index = 0;
		for (int j = 0; j < numSamples; j++) {
			for (int i = 0; i < numExtraParams; i++) {
				extraNames[i][j] = extraStrings.get(index++);
			}
		}

		imageNames = new String[6][][];
		imageNames[0] = medianNames;
		imageNames[1] = cvsNames;
		imageNames[2] = foldNames;
		imageNames[3] = rawMedianNames;
		imageNames[4] = rawFoldNames;
		imageNames[5] = extraNames;

		CompareSPADETrees show = new CompareSPADETrees();
		show.showImage();
	}

}