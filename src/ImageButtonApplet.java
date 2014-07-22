import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

public class ImageButtonApplet extends Applet implements ActionListener,
		KeyListener {
	private Vector v;

	private Button b;
	private ImagePanel ip;
	private Image[][] images;
	private int imageRow, imageCol;
	private int numRows = 36;
	private int numCols = 9;

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

	public void actionPerformed(ActionEvent e) {
//		ip.setImage(0);
		ip.repaint();
		repaint();
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_UP:
			// System.out.println("up");

			if (imageRow != 0)
				imageRow--;
			printRowColumn();
			changeImage();
			break;

		case KeyEvent.VK_DOWN:
			// System.out.println("down");

			if (imageRow != (numRows - 1))
				imageRow++;
			printRowColumn();
			changeImage();
			break;

		case KeyEvent.VK_LEFT:
			// System.out.println("left");

			if (imageCol != 0)
				imageCol--;
			printRowColumn();
			changeImage();
			break;

		case KeyEvent.VK_RIGHT:
			// System.out.println("right");

			if (imageCol != (numCols - 1))
				imageCol++;
			printRowColumn();
			changeImage();
			break;

		}
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
	
	public void printRowColumn(){
		System.out.println("Image Row: " + imageRow + " Image Column: " + imageCol);
	}
	
	public void changeImage(){
		ip.setImage(images[imageRow][imageCol]);
		ip.repaint();
		repaint();
	}
}
