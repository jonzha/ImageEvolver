import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class GUI extends JFrame implements ActionListener, Runnable {
	/**
	 * File: GUI.java Author: Jon Zhang Date created: August 2013 Date last
	 * modified: August 10, 2013
	 */
	private static final long serialVersionUID = 2253447554945502308L;
	JButton open, start;
	JFileChooser fileChooser;
	BufferedImage image;
	ImageIcon ii;
	JPanel top;
	JLabel pic;
	JCheckBox sMButton;
	boolean smartMutate;
	int shapeType = 2;
	JMenuBar menuBar;
	JMenu file;
	JMenuItem about, info;
	SliderGUI numPoly, numPoints;
	JPanel container;

	public GUI() {
		// Setting initial image

		try {
			image = ImageIO
					.read(getClass().getResource("/images/monalisa.png"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		JLabel title = new JLabel("Image Generator 1.0");
		title.setFont(new Font(null, Font.BOLD, 20));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel author = new JLabel("By Jon Zhang");
		author.setFont(new Font(null, Font.ITALIC, 15));
		author.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel selectedImg = new JLabel("Selected Image");
		selectedImg.setFont(new Font(null, Font.PLAIN, 10));
		selectedImg.setAlignmentX(Component.CENTER_ALIGNMENT);
		ii = new ImageIcon(this.getClass().getResource("/images/monalisa.png"));
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

		JMenu help;
		help = new JMenu("Help");
		menuBar.add(help);

		about = new JMenuItem("About");
		about.addActionListener(this);

		info = new JMenuItem("Info");
		info.addActionListener(this);

		file.add(about);
		help.add(info);
		setJMenuBar(menuBar);

	}

	private void about() {
		JOptionPane.showMessageDialog(null, "Version 1.0 \n Jon Zhang 2013",
				"About", 1);
	}

	private void info() {
		JOptionPane
				.showMessageDialog(
						null,
						" Number of shapes- This determines how many shapes will be used to create the image. The more shapes that are used the more accurate the final image will be and the more time the rendering will take. \n Number of vertices on polygon- This determines the number of vertices each polygon will have if the polygon option is selected. \n Smart Mutate- Having this box checked will apply the Smart Mutate algorithm, which will optimize the rendering process.",
						"Info", 1);
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
			if (image.getHeight() > 150) {
				image = resize(
						(int) (150.00 / (double) image.getHeight() * image
								.getWidth()),
						150, image);
			}
			if (image.getWidth() > 300) {
				image = resize(300,
						(int) (300.00 / (double) image.getWidth() * image
								.getHeight()), image);
			}
			pic.setIcon(ii);

		} catch (IOException e) {
			System.out.println("IOEXCEPTION ENCOUNTERED!");
		}
	}

	public void start() {
		Thread generatorThread = new Thread(this);
		generatorThread.start();
	}

	public void startMain() {
		int numShapes = Integer.parseInt(numPoly.textField.getText()
				.replaceAll(",", ""));
		int polyPoints = Integer.parseInt(numPoints.textField.getText());
		String[] options = new String[] { "Change value", "Continue anyway" };

		if (numShapes > 150 && shapeType == 2) {
			int result = JOptionPane
					.showOptionDialog(
							null,
							"Numbers of polygons over 150 are not recommended and will cause the program to work extremely slowly.",
							"Warning", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.INFORMATION_MESSAGE, null, options,
							options[0]);
			if (result == JOptionPane.OK_OPTION) {
				return;
			}
		} else if (numShapes < 100 && shapeType == 1) {
			int result = JOptionPane
					.showOptionDialog(
							null,
							"Numbers of circles less than 100 are not recommended and will not create an accurate depiction.",
							"Warning", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.INFORMATION_MESSAGE, null, options,
							options[0]);
			if (result == JOptionPane.OK_OPTION) {
				return;
			}
		}
		ImageGeneratorWGUI generator = new ImageGeneratorWGUI(image, shapeType,
				numShapes, polyPoints, smartMutate);
		add(generator);
		setSize(4 * image.getWidth(), (int) (2.5 * image.getHeight()) + 30);
		remove(container);
		validate();
		generator.start();
	}

	public BufferedImage resize(int newWidth, int newHeight,
			BufferedImage original) {
		BufferedImage resized = new BufferedImage(newWidth, newHeight,
				original.getType());
		Graphics2D g = resized.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(original, 0, 0, newWidth, newHeight, 0, 0,
				original.getWidth(), original.getHeight(), null);
		g.dispose();
		return resized;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == open) {
			fileChooser = new JFileChooser();

			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileFilter(new PictureFilter());
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
		} else if (e.getSource() == about) {
			about();
		} else if (e.getSource() == info) {
			info();
		}

	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		GUI game = new GUI();
	}

	@Override
	public void run() {
		startMain();
	}
}
