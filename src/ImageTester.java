import javax.swing.JFrame;

public class ImageTester extends JFrame {
	static ImageGenerator2 board;

	public ImageTester() {

		setTitle("Evolution of the image");
		board = new ImageGenerator2();
		add(board);
		setSize(5 * board.imgWidth, (int) (3.5 * board.imgHeight));

		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		ImageTester game = new ImageTester();
		game.setLocationRelativeTo(null);
		game.setVisible(true);
		board.start();
	}

}