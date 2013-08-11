import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImageGeneratorOld extends JPanel {
	int population = 200;
	BufferedImage img;

	Member[] colony = new Member[population];
	BufferedImage bestFit;
	int numPoly = 300;
	double highestFit;
	double crossChance = 0.8;
	double mutationChance = 0.5;
	Member[] newColony = new Member[population];
	int imgHeight;
	int imgWidth;
	int generation;
	BufferedImage test;
	int type;

	public ImageGeneratorOld() {
		img = null;
		bestFit = null;
		test = null;
		highestFit = Double.MAX_VALUE;

		try {
			img = ImageIO.read(new File("MonaLisa.png"));
			test = ImageIO.read(new File("MonaLisa.png"));
			// Type 6 TYPE_4BYTE_ABGR

		} catch (IOException e) {
			System.out.println("File not found");
		}
		System.out.println("Test imgae fitness " + fitness(test));
		imgWidth = img.getWidth();
		imgHeight = img.getHeight();
		// Initial population
		for (int i = 0; i < population; i++) {
			BufferedImage tempImg = new BufferedImage(100, 100,
					BufferedImage.TYPE_4BYTE_ABGR);
			colony[i] = initializePop(tempImg);
			if (colony[i].fitness < highestFit) {
				highestFit = colony[i].fitness;
				bestFit = colony[i].img;
			}
		}
		// Main loop
		while (highestFit > 5E7) {
			if (generation == 10) {
				break;
			}
			System.out.println("Highest fit " + highestFit);
			System.out.println("Generation: " + generation);
			repaint();
			repopulate();
			for (int i = 0; i < population; i++) {

				colony[i].fitness = fitness(colony[i].img);
				if (colony[i].fitness > highestFit) {
					highestFit = colony[i].fitness;
					bestFit = colony[i].img;
				}
			}
			generation++;
		}
		repaint();
		System.out.println("blah");

	}

	private Member initializePop(BufferedImage old) {
		Oval[] ovals = generateOvals(numPoly);

		BufferedImage img = new BufferedImage(imgWidth, imgHeight,
				BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g2d = img.createGraphics();
		// g2d.drawImage(old, 0, 0, null);

		// Actual coding part
		for (int i = 0; i < ovals.length; i++) {
			ovals[i].draw(g2d);
		}
		g2d.dispose();
		return new Member(fitness(img), ovals, img, type);
	}

	private BufferedImage redraw(BufferedImage old, Oval[] ovals) {

		int w = old.getWidth();
		int h = old.getHeight();

		BufferedImage img = new BufferedImage(w, h,
				BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g2d = img.createGraphics();
		// g2d.drawImage(old, 0, 0, null);

		// Actual coding part
		for (int i = 0; i < ovals.length; i++) {
			ovals[i].draw(g2d);
		}
		g2d.dispose();
		return img;
	}

	public Oval[] generateOvals(int numPoly) {
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

	public double fitness(BufferedImage member) {
		double fitness = 0;
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				int orgImg = img.getRGB(x, y);
				int red = (orgImg >> 16) & 0xff;
				int green = (orgImg >> 8) & 0xff;
				int blue = (orgImg) & 0xff;

				int newImg = member.getRGB(x, y);
				int red2 = (newImg >> 16) & 0xff;
				int green2 = (newImg >> 8) & 0xff;
				int blue2 = (newImg) & 0xff;

				int redChange = red - red2;
				int greenChange = green - green2;
				int blueChange = blue - blue2;

				fitness += redChange * redChange + greenChange * greenChange
						+ blueChange * blueChange;
			}
		}
		return fitness;
	}

	// Roulette selection
	public Member[] selectParents() {
		Member[] parents = new Member[2];
		double sum = 0;
		for (int i = 0; i < population; i++) {
			sum += (1 - colony[i].fitness);
		}
		for (int o = 0; o < 2; o++) {
			double pick = randDoub(0, sum);
			double parent = 0;
			for (int i = 0; i < population; i++) {
				parent += colony[i].fitness;
				// System.out.println("Score to beat: " + pick);
				// System.out.println("Parent score: " + parent);
				// System.out.println();
				if (parent > pick) {
					parents[o] = colony[i].copy();
					break;
				}
			}
		}
		return parents;
	}

	public Member breed(Member[] parents) {
		Oval[] ovals = new Oval[numPoly];
		if (randDoub(0, 1) < crossChance) {
			for (int i = 0; i < numPoly; i++) {
				// ---Mutation---//
				if (randDoub(0, 1) < mutationChance) {
					Random rand = new Random();
					ovals[i] = new Oval(randInt(0, imgWidth), randInt(0,
							imgHeight), randInt(1, imgWidth), randInt(1,
							imgHeight), rand.nextFloat(), rand.nextFloat(),
							rand.nextFloat(), randFlo(0, 1), randFlo(0, 100));
				}
				// ---End Mutation---//
				else {
					ovals[i] = parents[randInt(0, 1)].ovals[i];
				}
			}
			BufferedImage img = new BufferedImage(100, 100,
					BufferedImage.TYPE_4BYTE_ABGR);
			img = redraw(img, ovals);

			return new Member(fitness(img), ovals, img, type);
		} else {
			return parents[0];
		}
	}

	public void repopulate() {
		for (int i = 0; i < population; i++) {
			newColony[i] = breed(selectParents());
		}
		for (int i = 0; i < population; i++) {
			colony[i] = newColony[i];
		}
	}

	public double randDoub(double min, double max) {
		Random r = new Random();
		return min + (max - min) * r.nextDouble();
	}

	public int randInt(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}

	public float randFlo(float min, float max) {
		Random r = new Random();
		return min + (max - min) * r.nextFloat();
	}

	public void paint(Graphics g) {
		g.drawImage(img, 100, 100, null);
		g.drawImage(bestFit, 100, 200, null);
	}
}
