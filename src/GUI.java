import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileFilter;

public class GUI extends JFrame implements ActionListener, Runnable {
	JButton open, start;
	JFileChooser fileChooser;
	BufferedImage image;
	ImageIcon ii;
	JPanel top;
	JLabel pic;
	JCheckBox sMButton;
	boolean smartMutate;
	int shapeType;
	JMenuBar menuBar;
	JMenu file;
	JMenuItem instructions, about, menu, statistics;
	SliderGUI numPoly, numPoints;
	JPanel container;
	static boolean started;

	public GUI() {
		// Setting initial image
		try {
			image = ImageIO.read(new File("monalisa.png"));
		} catch (IOException e) {
			System.out.println("MONA IS MISSING");
		}
		container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		container.add(addTop());
		container.add(addBottom());
		add(container);
		addMenuBar();
		setSize(500, 420);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public JPanel addTop() {
		top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
		JLabel title = new JLabel("Image Generator");
		title.setFont(new Font(null, Font.BOLD, 20));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel author = new JLabel("By Jon Zhang");
		author.setFont(new Font(null, Font.ITALIC, 15));
		author.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel selectedImg = new JLabel("Selected Image");
		selectedImg.setFont(new Font(null, Font.PLAIN, 10));
		selectedImg.setAlignmentX(Component.CENTER_ALIGNMENT);
		ii = new ImageIcon(this.getClass().getResource("monalisa.png"));
		pic = new JLabel(ii);
		pic.setAlignmentX(Component.CENTER_ALIGNMENT);

		// *** open file***//
		open = new JButton("Change Image");
		open.addActionListener(this);
		open.setAlignmentX(Component.CENTER_ALIGNMENT);

		top.add(title);
		top.add(author);
		top.add(selectedImg);
		top.add(pic);
		top.add(open);
		return top;
	}

	public JPanel addBottom() {
		// Panels
		JPanel bottom = new JPanel();
		JPanel sliders = new JPanel();
		JPanel otherStuff = new JPanel();

		// Layouts
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
		otherStuff.setLayout(new BoxLayout(otherStuff, BoxLayout.Y_AXIS));
		GridLayout gridLayout = new GridLayout(1, 2);
		sliders.setLayout(gridLayout);

		// Sliders
		numPoly = new SliderGUI("Number of Shapes", 20, 520, 100, 20, 50);
		numPoints = new SliderGUI("Number of vertices on Polygon", 3, 10, 1, 1,
				5);

		// Button
		start = new JButton("Start");
		start.addActionListener(this);

		sliders.add(numPoly);
		sliders.add(numPoints);
		otherStuff.add(shapeType());
		otherStuff.add(smartMutateBox());
		otherStuff.add(start);
		bottom.add(sliders);
		bottom.add(otherStuff);
		return bottom;
	}

	public void addMenuBar() {

		menuBar = new JMenuBar();

		file = new JMenu("File");
		menuBar.add(file);

		about = new JMenuItem("About");
		about.addActionListener(this);

		menu = new JMenuItem("Menu");
		menu.addActionListener(this);

		instructions = new JMenuItem("Instructions");
		instructions.addActionListener(this);

		statistics = new JMenuItem("Statistics");
		statistics.addActionListener(this);

		file.add(about);
		setJMenuBar(menuBar);

	}

	public JPanel shapeType() {
		JRadioButton polygon = new JRadioButton("Polygon", true);
		JRadioButton circle = new JRadioButton("Circle");

		polygon.setActionCommand("Polygon");
		circle.setActionCommand("Circle");

		polygon.addActionListener(this);
		circle.addActionListener(this);

		ButtonGroup group = new ButtonGroup();
		group.add(polygon);
		group.add(circle);

		JPanel shapePanel = new JPanel();
		shapePanel.add(polygon);
		shapePanel.add(circle);
		return shapePanel;
	}

	public JPanel smartMutateBox() {
		sMButton = new JCheckBox("Smart Mutate");
		sMButton.setSelected(true);

		JPanel smartMutate = new JPanel();
		smartMutate.setLayout(new BoxLayout(smartMutate, BoxLayout.PAGE_AXIS));

		smartMutate.add(sMButton);
		sMButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		sMButton.addItemListener(itemListener);
		return smartMutate;
	}

	ItemListener itemListener = new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();
			if (source == sMButton) {
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					smartMutate = false;
					System.out.println("Smart Mutate disabled!");
				} else {
					smartMutate = true;
				}
			}
		}
	};

	public void getImage(File img) {
		try {
			image = ImageIO.read(img);
			if (image.getHeight() > 100) {
				Image imagePreview = image.getScaledInstance(
						(int) (100.00 / (double) image.getHeight() * image
								.getWidth()), 100, Image.SCALE_DEFAULT);
				ii = new ImageIcon(imagePreview);

			} else {
				ii = new ImageIcon(image);

			}
			pic.setIcon(ii);

		} catch (IOException e) {
			System.out.println("IOEXCEPTION ENCOUNTERED!");
		}
	}

	public void start() {
		started = true;
		Thread generatorThread = new Thread(this);
		generatorThread.start();
	}

	public void startMain() {
		ImageGeneratorWGUI generator = new ImageGeneratorWGUI(image, shapeType,
				Integer.parseInt(numPoly.textField.getText()
						.replaceAll(",", "")), // Gets rid of annoying commas
				Integer.parseInt(numPoints.textField.getText()), smartMutate);
		add(generator);
		remove(container);
		validate();
		generator.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == open) {
			fileChooser = new JFileChooser();
			PictureFilter filter = new PictureFilter();
			filter.add("jpg");
			filter.add("gif");
			filter.add("png");
			filter.setDescription("Images");
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileFilter(filter);
			int result = fileChooser.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File img = fileChooser.getSelectedFile();
				getImage(img);
			} else {
				System.out.println("Cancelled by user");
			}
		} else if (e.getActionCommand() == "Polygon") {
			shapeType = 2;
			System.out.println("Shape is set to polygon");
		} else if (e.getActionCommand() == "Circle") {
			shapeType = 1;
			System.out.println("Shape is set to circle");
		} else if (e.getSource() == start) {
			start();
		}

	}

	class PictureFilter extends FileFilter {

		ArrayList<String> list = new ArrayList<String>();
		private String description;

		public void add(String type) {
			list.add(type);
		}

		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			} else if (f.isFile()) {
				Iterator iterator = list.iterator();
				while (iterator.hasNext()) {
					if (f.getName().endsWith((String) iterator.next())) {
						return true;
					}

				}
			}
			return false;

		}

		@Override
		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

	}

	public static void main(String[] args) {
		GUI game = new GUI();
	}

	@Override
	public void run() {
		startMain();
	}
}
