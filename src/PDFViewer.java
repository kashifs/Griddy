// PDFViewer.java

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

import javax.swing.*;

import com.sun.pdfview.*;

public class PDFViewer extends JFrame {
	static Image image;

	public PDFViewer(String title) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		JLabel label = new JLabel(new ImageIcon(image));
		label.setVerticalAlignment(JLabel.TOP);

		setContentPane(new JScrollPane(label));

		pack();
		setVisible(true);
	}

	public static void main(final String[] args) throws IOException {
		if (args.length < 1 || args.length > 2) {
			System.err.println("usage: java PDFViewer pdfspec [pagenum]");
			return;
		}

		int pagenum = (args.length == 1) ? 1 : Integer.parseInt(args[1]);
		if (pagenum < 1)
			pagenum = 1;

		RandomAccessFile raf = new RandomAccessFile(new File(args[0]), "r");
		FileChannel fc = raf.getChannel();
		ByteBuffer buf = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		PDFFile pdfFile = new PDFFile(buf);

		int numpages = pdfFile.getNumPages();
		System.out.println("Number of pages = " + numpages);
		if (pagenum > numpages)
			pagenum = numpages;

		PDFPage page = pdfFile.getPage(pagenum);

		Rectangle2D r2d = page.getBBox();

		double width = r2d.getWidth();
		double height = r2d.getHeight();
		width /= 144.0;
		height /= 144.0;
		int res = Toolkit.getDefaultToolkit().getScreenResolution();
		width *= res;
		height *= res;

		image = page.getImage((int) width, (int) height, r2d, null, true, true);


		Runnable r2 = new Runnable() {

			public void run() {
				PDFViewer viewer = new PDFViewer("PDF Viewer: " + args[0]);

				// JFrame frame = new JFrame();
				JPanel panel = new JPanel();

				// frame.getContentPane().add(panel);

				viewer.getContentPane().add(panel);

				panel.addKeyListener(new KeyListener() {

					public void keyTyped(KeyEvent e) {
					}

					public void keyReleased(KeyEvent e) {
					}

					public void keyPressed(KeyEvent e) {
						// System.out.println("Pressed " + e.getKeyChar());
						// System.out.println("Code " + e.getID());

						if (e.getKeyCode() == KeyEvent.VK_UP) {
							System.out.println("up");
						}
						if (e.getKeyCode() == KeyEvent.VK_DOWN) {
							System.out.println("DOWN");
						}
						if (e.getKeyCode() == KeyEvent.VK_LEFT) {
							System.out.println("LEFT");
						}
						if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
							System.out.println("RIGHT");
						}
					}
				});

				panel.setFocusable(true);
				panel.requestFocusInWindow();
			}

		};
		// EventQueue.invokeLater(r);
		EventQueue.invokeLater(r2);
	}
}