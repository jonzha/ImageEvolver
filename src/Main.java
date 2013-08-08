import javax.swing.JFrame;
import javax.swing.JLabel;

public class Main extends JFrame {
	JLabel statusbar;
	static ImageGenerator2 board;

	public Main() {

		board = new ImageGenerator2(true);
		add(board);
		setSize(5 * board.imgWidth, (int) (3.5 * board.imgHeight));
		setTitle("Evolution of the image");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// add(new GUIContainer(new Chromo[8]));
		// setSize(450, 500);
	}

	public static void main(String[] args) {

		Main game = new Main();
		game.setLocationRelativeTo(null);
		game.setVisible(true);
		board.start();
	}

}