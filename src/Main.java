import javax.swing.JLabel;

public class Main {
	JLabel statusbar;
	static ImageGenerator2 board;
	static GUI gui;

	public Main() {/*
					 * JPanel container = new JPanel(); OpenFile open = new
					 * OpenFile();
					 * 
					 * container.setLayout(new BoxLayout(container,
					 * BoxLayout.Y_AXIS));
					 * 
					 * setTitle("Evolution of the image"); container.add(open);
					 * add(container); if (open.image != null) { board = new
					 * ImageGenerator2(open.image); } else { board = new
					 * ImageGenerator2(); } container.add(board); setSize(5 *
					 * board.imgWidth, (int) (3.5 * board.imgHeight));
					 */
		gui = new GUI();
		gui.setVisible(true);
	}

	public static void main(String[] args) {
		Main game = new Main();
		gui.start();
		// board.start();
	}

}