import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

public class ImageButtonApplet extends Applet implements ActionListener,
		KeyListener {
	private Vector v;
	private int numOfImages;
	private int currentImage;
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
		numOfImages = 0;
		currentImage = 0;
		setLayout(new BorderLayout());
		Image tempImage = getImage(getCodeBase(),
				"/Users/kashif/Desktop/air1.jpg");
		ip = new ImagePanel(tempImage);
		v.insertElementAt(tempImage, numOfImages);
		numOfImages++;
		tempImage = getImage(getCodeBase(), "/Users/kashif/Desktop/air2.jpg");
		v.insertElementAt(tempImage, numOfImages);
		numOfImages++;
		tempImage = getImage(getCodeBase(), "/Users/kashif/Desktop/air3.jpg");
		v.insertElementAt(tempImage, numOfImages);
		numOfImages++;
		add(ip, BorderLayout.CENTER);
		b = new Button("Next!");
		b.addActionListener(this);
		ip.addKeyListener(this);
		Panel p = new Panel();
		p.add(b);
		add(p, BorderLayout.SOUTH);
		ip.requestFocus();
	}

	public void actionPerformed(ActionEvent e) {
		currentImage = (currentImage + 1) % numOfImages;
		Image i = (Image) v.elementAt(currentImage);
		ip.setImage(i);
		ip.repaint();
		repaint();
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_UP:
			System.out.println("up");
			
			if(imageRow != 0)
				imageRow--;		
			break;
			
		case KeyEvent.VK_DOWN:
			System.out.println("down");
			
			if(imageRow != (numRows - 1))
				imageRow++;
			break;
			
		case KeyEvent.VK_LEFT:
			System.out.println("left");
			
			if(imageCol != 0)
				imageCol--;
			break;
			
		case KeyEvent.VK_RIGHT:
			System.out.println("right");
			
			if(imageCol != (numCols - 1))
				imageCol++;
			break;
			
		}
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
