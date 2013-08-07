import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImageGenerator2 extends JPanel {
	int population = 2;
	int numPoly = 400;
	int type = 1;
	int polyPoints = 3;
	BufferedImage orgImg;
	Member[] colony = new Member[population];
	BufferedImage startImage;
	Member[] newColony = new Member[population];
	int imgHeight;
	int imgWidth;
	int generation;
	Member parent;
	Member child;
	int frameWidth;
	int frameHeight;
	int targetImgX, startImgX, currentImgX;
	int improvements;
	boolean draw;
	double fitness;
	boolean smartMutate = true;
	int imgYs;

	// (1-current difference/maximum difference).

	public ImageGenerator2(boolean draw) {
		this.draw = draw;
		orgImg = null;
		startImage = null;
		improvements = 0;

		try {
			orgImg = ImageIO.read(new File("monalisa.png"));
			// Type 6 TYPE_4BYTE_ABGR

		} catch (IOException e) {
			System.out.println("File not found");
			System.exit(1);
		}
		imgWidth = orgImg.getWidth();
		imgHeight = orgImg.getHeight();
		// Initial population

		BufferedImage tempImg = new BufferedImage(100, 100,
				BufferedImage.TYPE_4BYTE_ABGR);
		parent = initializePop(tempImg);
		startImage = parent.copy().img;
		child = parent.copy();
		/*
		 * child = parent.copy(); child.mutate(); child.fitness(img);
		 * System.out.println("Paren fit " + parent.fitness);
		 * System.out.println("Child fit " + child.fitness);
		 */

	}

	public void start() {

		// Main loop
		while (true) {
			if (draw) {
				repaint();
			}
			if (fitness > 0.999) {
				break;
			}
			child = parent.copy();
			int[] instructions = child.mutate();
			child.fitness(orgImg);
			generation++;

			// System.out.println("Paren fit " + parent.fitness);
			// System.out.println("Child fit " + child.fitness);

			// System.out.println("Generation: " + generation);
			// System.out.println("Improvements: " + improvements);
			// System.out.printf("Fitness: %.3f%%", (fitness * 100));
			// System.out.println();
			// System.out.println();
			if (smartMutate) {
				while (child.fitness < parent.fitness) {
					parent = child.copy();
					improvements++;
					instructions = child.mutate2(instructions);
					child.fitness(orgImg);
					generation++;
					fitness = 1 - parent.fitness / initialFitness(startImage);
					repaint();
				}
			} else {
				if (child.fitness < parent.fitness) {
					parent = child.copy();
					improvements++;
					fitness = 1 - parent.fitness / initialFitness(startImage);
				}
			}

		}

		repaint();
	}

	private Member initializePop(BufferedImage old) {
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

		// Actual coding part
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

	private double initialFitness(BufferedImage member) {
		double fitness = 0;
		for (int y = 0; y < imgHeight; y++) {
			for (int x = 0; x < imgWidth; x++) {
				int orgImgPix = orgImg.getRGB(x, y);
				int red = (orgImgPix >> 16) & 0xff;
				int green = (orgImgPix >> 8) & 0xff;
				int blue = (orgImgPix) & 0xff;

				int newImgPix = member.getRGB(x, y);
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

	private void updateStats(Graphics g) {
		g.setFont(new Font(null, Font.BOLD, 12));
		g.drawString("Target Image", targetImgX, imgYs - 2);
		g.drawString("Original Image ", startImgX, imgYs - 2);
		g.drawString("Current Image", currentImgX, imgYs - 2);
		g.drawString("Stats", (int) (frameWidth / 2.5), imgYs + imgHeight + 20);
		g.setFont(new Font(null, Font.PLAIN, 12));
		g.drawString("Generation: " + generation, (int) (frameWidth / 2.5),
				imgYs + imgHeight + 35);
		g.drawString("Improvements: " + improvements, (int) (frameWidth / 2.5),
				imgYs + imgHeight + 50);
		NumberFormat formatter = new DecimalFormat("#0.000");
		g.drawString("Accuracy: " + formatter.format((fitness * 100)) + "%",
				(int) (frameWidth / 2.5), imgYs + imgHeight + 65);

		// g.drawString(", x, y);
	}

	private int randInt(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}

	private float randFlo(float min, float max) {
		Random r = new Random();
		return min + (max - min) * r.nextFloat();
	}

	public void paint(Graphics g) {
		super.paint(g);
		frameHeight = (int) getSize().getHeight();
		frameWidth = (int) getSize().getWidth();
		int spacing = (frameWidth - imgWidth * 3) / 4;
		targetImgX = spacing * 3 + imgWidth * 2;
		startImgX = spacing;
		currentImgX = spacing * 2 + imgWidth;
		imgYs = (int) (frameHeight / 2 - imgHeight / 2 - frameHeight * 0.1);
		g.drawImage(orgImg, targetImgX, imgYs, null);
		g.drawImage(startImage, startImgX, imgYs, null);
		g.drawImage(parent.img, currentImgX, imgYs, null);
		updateStats(g);

	}
}
