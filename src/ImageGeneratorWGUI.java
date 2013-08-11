/**
 * File: ImageGeneratorWGUI.java
 * Author: Jon Zhang
 * Date created: August 2013
 * Date last modified: August 10, 2013
 */
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ImageGeneratorWGUI extends JPanel implements ActionListener {

	// ***GUI***//
	int numPoly = 100;
	int type = 2; // 1 for circle, 2 for polygon
	int polyPoints = 25;
	boolean smartMutate = true;
	String fileName = "monalisa.png";
	// ***GUI***//

	int population = 2;
	BufferedImage targetImg;
	Member[] colony = new Member[population];
	BufferedImage startImage;
	int imgHeight;
	int imgWidth;
	int generation; // AKA number of changes attempted
	Member parent;
	Member child;
	int frameWidth;
	int frameHeight;
	int targetImgX, startImgX, currentImgX;
	int improvements;// AKA number of beneficial changes
	double fitness;
	int imgYs;
	JButton save;
	long startTime;

	public ImageGeneratorWGUI(BufferedImage targetImg, int type, int numPoly,
			int polyPoints, boolean smartMutate) {
		// Initialize stuff
		this.type = type;
		this.targetImg = targetImg;
		this.numPoly = numPoly;
		this.polyPoints = polyPoints;
		this.smartMutate = smartMutate;
		startImage = null;
		improvements = 0;
		save();
		imgWidth = targetImg.getWidth();
		imgHeight = targetImg.getHeight();

		// Initial population
		parent = initializePop();
		startImage = parent.copy().img;
		child = parent.copy();
	}

	public void start() {
		startTime = System.nanoTime();
		while (true) {

			if (fitness > 0.999) {
				break;
			}
			child = parent.copy();

			int[] instructions = child.mutate();
			child.fitness(targetImg);
			repaint();

			generation++;

			// System.out.println("Paren fit " + parent.fitness);
			// System.out.println("Child fit " + child.fitness);

			// System.out.println("Generation: " + generation);
			// System.out.println("Improvements: " + improvements);
			// System.out.printf("Fitness: %.3f%%", (fitness * 100));
			// System.out.println();
			// System.out.println();

			if (smartMutate) {
				// This loop allows for the continual reprocessing of the chosen
				// gene. In other words, the chosen gene for the chosen shape is
				// mutated again and again until it reaches it's optimal value.
				while (child.fitness < parent.fitness) {

					parent = child.copy();
					improvements++;
					instructions = child.mutate2(instructions);
					repaint();
					child.fitness(targetImg);
					generation++;
					fitness = 1 - parent.fitness / initialFitness(startImage);
					repaint();
				}
			} else {
				// Otherwise if smartMutate is disabled just go the normal route
				if (child.fitness < parent.fitness) {
					parent = child.copy();
					improvements++;
					fitness = 1 - parent.fitness / initialFitness(startImage);
				}
			}

		}

		repaint();
	}

	// Used to create the initial parent
	private Member initializePop() {
		Oval[] ovals = null;
		PolygonNew[] polygons = null;
		if (type == 1) {
			ovals = generateOvals(numPoly);
			Arrays.sort(ovals);
		} else {
			polygons = generatePolygons(numPoly);
			Arrays.sort(polygons);
		}
		BufferedImage img = new BufferedImage(imgWidth, imgHeight,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = img.createGraphics();

		// Draws the image
		if (type == 1) {
			for (int i = 0; i < ovals.length; i++) {
				ovals[i].draw(g2d);
			}
		} else {
			for (int i = 0; i < polygons.length; i++) {
				polygons[i].draw(g2d);
			}
		}
		g2d.dispose();
		if (type == 1) {
			return new Member(initialFitness(img), ovals, img, type);
		} else {
			return new Member(initialFitness(img), polygons, img, type);
		}
	}

	// Generates the initial set of polygons with randomized attributes.
	private PolygonNew[] generatePolygons(int numPoly) {
		PolygonNew[] polygons = new PolygonNew[numPoly];
		for (int i = 0; i < numPoly; i++) {
			Random rand = new Random();
			// *****NUMBER OF POINTS*****//
			int points = polyPoints;
			// generate x coords and y coords
			int[] xpoints = new int[points];
			int[] ypoints = new int[points];
			for (int j = 0; j < points; j++) {
				xpoints[j] = randInt(0, imgWidth);
				ypoints[j] = randInt(0, imgHeight);
			}
			polygons[i] = new PolygonNew(xpoints, ypoints, points,
					rand.nextFloat(), rand.nextFloat(), rand.nextFloat(),
					randFlo(0, 1), randFlo(0, 100));
		}
		return polygons;
	}

	// Generates the initial set of circles with randomized attributes.
	private Oval[] generateOvals(int numPoly) {
		Oval[] ovals = new Oval[numPoly];
		for (int i = 0; i < numPoly; i++) {
			Random rand = new Random();

			ovals[i] = new Oval(randInt(0, imgWidth), randInt(0, imgHeight),
					randInt(1, imgWidth), randInt(1, imgHeight),
					rand.nextFloat(), rand.nextFloat(), rand.nextFloat(),
					randFlo(0, 1), randFlo(0, 100));
		}
		return ovals;
	}

	// Calculates the fitness based off of the provided image. This is used to
	// create the initial parent.
	private double initialFitness(BufferedImage img) {
		double fitness = 0;
		for (int y = 0; y < imgHeight; y++) {
			for (int x = 0; x < imgWidth; x++) {
				int targetImgPix = targetImg.getRGB(x, y);
				int red = (targetImgPix >> 16) & 0xff;
				int green = (targetImgPix >> 8) & 0xff;
				int blue = (targetImgPix) & 0xff;

				int newImgPix = img.getRGB(x, y);
				int red2 = (newImgPix >> 16) & 0xff;
				int green2 = (newImgPix >> 8) & 0xff;
				int blue2 = (newImgPix) & 0xff;

				int redChange = red - red2;
				int greenChange = green - green2;
				int blueChange = blue - blue2;

				fitness += redChange * redChange + greenChange * greenChange
						+ blueChange * blueChange;
			}
		}
		return fitness;
	}

	// Adds the save button
	private void save() {
		JPanel savePanel = new JPanel();
		save = new JButton("Save current image");
		save.addActionListener(this);
		savePanel.add(save);
		add(savePanel);
	}

	private int randInt(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}

	private float randFlo(float min, float max) {
		Random r = new Random();
		return min + (max - min) * r.nextFloat();
	}

	private String calculateTimeElapsed() {
		long currentTime = System.nanoTime();
		long elapsedTime = currentTime - startTime;
		long day = TimeUnit.NANOSECONDS.toDays(elapsedTime);
		long hr = TimeUnit.NANOSECONDS.toHours(elapsedTime
				- TimeUnit.DAYS.toMillis((day)));
		long min = TimeUnit.NANOSECONDS.toMinutes(elapsedTime
				- TimeUnit.HOURS.toMillis(hr));
		long sec = TimeUnit.NANOSECONDS.toSeconds(elapsedTime
				- TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
		long ms = TimeUnit.NANOSECONDS.toMillis(elapsedTime
				- TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min)
				- TimeUnit.SECONDS.toMillis(sec));
		return String.format("%02d:%02d:%02d:%02d.%03d", day, hr, min, sec, ms);
	}

	// Paint method used to update stats
	private void updateStats(Graphics g) {
		g.setFont(new Font(null, Font.BOLD, 12));
		g.drawString("Target Image", targetImgX, imgYs - 2);
		g.drawString("Original Image ", startImgX, imgYs - 2);
		g.drawString("Current Image", currentImgX, imgYs - 2);
		// g.drawString("Live Image", (int) (frameWidth / 2.5), imgYs +
		// imgHeight
		// + 20);
		g.drawString("Stats", (int) (frameWidth / 2.5), imgYs + imgHeight + 20);

		g.setFont(new Font(null, Font.PLAIN, 12));
		g.drawString("Attempted changes: " + generation,
				(int) (frameWidth / 2.5), imgYs + imgHeight + 35);
		g.drawString("Succesful changes: " + improvements,
				(int) (frameWidth / 2.5), imgYs + imgHeight + 50);
		NumberFormat formatter = new DecimalFormat("#0.000");
		g.drawString("Accuracy: " + formatter.format((fitness * 100)) + "%",
				(int) (frameWidth / 2.5), imgYs + imgHeight + 65);
		g.drawString("Time elapsed: " + calculateTimeElapsed(),
				(int) (frameWidth / 2.5), imgYs + imgHeight + 80);

		// g.drawString(", x, y);
	}

	public void paint(Graphics g) {
		super.paint(g);
		Toolkit.getDefaultToolkit().sync();

		frameHeight = (int) getSize().getHeight();
		frameWidth = (int) getSize().getWidth();
		int spacing = (frameWidth - imgWidth * 3) / 4;
		targetImgX = spacing * 3 + imgWidth * 2;
		startImgX = spacing;
		currentImgX = spacing * 2 + imgWidth;
		imgYs = (int) frameHeight - (frameHeight - imgHeight / 2);
		g.drawImage(targetImg, targetImgX, imgYs, null);
		g.drawImage(startImage, startImgX, imgYs, null);
		g.drawImage(parent.img, currentImgX, imgYs, null);
		updateStats(g);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// All that long code down there? It's all to save the image.
		if (e.getSource() == save) {
			JFileChooser saver = new JFileChooser() {
				// Override modified based off of version on thepcwizard.com
				// This handles overwriting existing files
				@Override
				public void approveSelection() {
					String[] options = new String[] { "Replace", "Cancel" };

					File f;
					if (!getFileFilter().accept(getSelectedFile())) {
						f = new File(getSelectedFile().getAbsolutePath()
								+ ".png");
					} else {
						f = getSelectedFile();
					}
					if (f.exists()) {
						int result = JOptionPane
								.showOptionDialog(
										this,
										"File already exists. Replace the existing file?",
										"File already exists",
										JOptionPane.OK_CANCEL_OPTION,
										JOptionPane.WARNING_MESSAGE, null,
										options, options[1]);
						switch (result) {
						case JOptionPane.NO_OPTION:
							return;
						case JOptionPane.OK_OPTION:
							super.approveSelection();
							return;
						case JOptionPane.CLOSED_OPTION:
							return;

						}
					}
					super.approveSelection();
				}
			};

			saver.setAcceptAllFileFilterUsed(false);
			saver.setFileFilter(new PictureFilter());
			int result = saver.showSaveDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File file = saver.getSelectedFile();
				if (!saver.getFileFilter().accept(file)) {
					file = new File(file.getAbsolutePath() + ".png");
				}

				try {
					ImageIO.write(parent.img, "png", file);
					JOptionPane.showMessageDialog(null, "Image saved", "Saved",
							JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(null,
							"Error. Something went wrong while saving.",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
