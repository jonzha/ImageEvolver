import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.Random;

public class Member {
	double fitness;
	Oval[] ovals;
	PolygonNew[] polygons;
	BufferedImage img;
	int type;

	public Member(double fitness, Object[] shape, BufferedImage img, int type) {
		this.fitness = fitness;
		this.type = type;
		if (type == 1) {
			this.ovals = (Oval[]) shape;
		} else {
			this.polygons = (PolygonNew[]) shape;
		}
		this.img = img;
	}

	// Returns a copy of the member
	// 1 is oval 2 is polygon
	public Member copy() {
		if (type == 1) {
			Oval[] newOvals = new Oval[ovals.length];
			for (int i = 0; i < ovals.length; i++) {
				newOvals[i] = ovals[i].copy();
			}
			return new Member(fitness, newOvals, deepCopy(img), 1);
		} else {
			return new Member(fitness, polygons, deepCopy(img), 2);
		}
	}

	// Redraws the image
	public void redraw() {
		int w = img.getWidth();
		int h = img.getHeight();

		img = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g2d = img.createGraphics();
		// g2d.drawImage(old, 0, 0, null);

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

	}

	// Recalculates the fitness based on original image
	public void fitness(BufferedImage original) {
		fitness = 0;
		for (int y = 0; y < original.getHeight(); y++) {
			for (int x = 0; x < original.getWidth(); x++) {
				int orgImg = original.getRGB(x, y);
				int red = (orgImg >> 16) & 0xff;
				int green = (orgImg >> 8) & 0xff;
				int blue = (orgImg) & 0xff;

				int newImg = img.getRGB(x, y);
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

	}

	// Mutates the given member
	public int[] mutate() {
		int gene = randInt(1, 7);
		int mutateAmt = randInt(-10, 10);
		int shapeNum;
		int polygonPoint = 0;
		int xMoveAmt = randInt(-img.getWidth(), img.getWidth()) / 3;
		int yMoveAmt = randInt(-img.getHeight(), img.getHeight()) / 3;
		if (type == 1) {
			shapeNum = randInt(0, ovals.length - 1);
		} else {
			shapeNum = randInt(0, polygons.length - 1);
		}
		float randr = randFlo(-0.2f, 0.2f);
		float randg = randFlo(-0.2f, 0.2f);
		float randb = randFlo(-0.2f, 0.2f);
		// ===Oval mutation===
		if (type == 1) {
			switch (gene) {
			case 1:
				ovals[shapeNum].x += xMoveAmt;
				break;
			case 2:
				ovals[shapeNum].y += yMoveAmt;
				break;
			case 3:
				ovals[shapeNum].width += mutateAmt;
				break;
			case 4:
				ovals[shapeNum].height += mutateAmt;
				break;
			case 5:
				if (ovals[shapeNum].r + randr >= 0
						&& ovals[shapeNum].r + randr <= 1) {
					ovals[shapeNum].r += randr;
				}
				if (ovals[shapeNum].g + randg >= 0
						&& ovals[shapeNum].g + randg <= 1) {
					ovals[shapeNum].g += randg;
				}
				if (ovals[shapeNum].b + randb >= 0
						&& ovals[shapeNum].b + randb <= 1) {
					ovals[shapeNum].b += randb;
				}
				ovals[shapeNum].recolor();

				break;
			case 6:
				if (ovals[shapeNum].transparency + randr > 0
						&& ovals[shapeNum].transparency + randr < 1) {
					ovals[shapeNum].transparency += randr;
				}
				break;
			case 7:
				ovals[shapeNum].order += mutateAmt;
				Arrays.sort(ovals);
				break;
			}
		}
		// ====Polygon Mutation====
		else {
			polygonPoint = randInt(0, polygons[shapeNum].xpoints.length - 1);
			randr = randFlo(-0.2f, 0.2f);
			randg = randFlo(-0.2f, 0.2f);
			randb = randFlo(-0.2f, 0.2f);

			switch (gene) {
			case 1: // one x coordinate of one point
				polygons[shapeNum].xpoints[polygonPoint] += xMoveAmt;
				break;
			case 2: // one y coordinate of one point
				polygons[shapeNum].ypoints[polygonPoint] += yMoveAmt;
				break;
			case 3: // x coordinates of whole shape
				for (int i = 0; i < polygons[shapeNum].xpoints.length; i++) {
					polygons[shapeNum].xpoints[i] += mutateAmt;
				}
				break;
			case 4: // y coordinates of whole shape
				for (int i = 0; i < polygons[shapeNum].ypoints.length; i++) {
					polygons[shapeNum].ypoints[i] += mutateAmt;
				}
				break;
			case 5: // color
				if (polygons[shapeNum].r + randr >= 0
						&& polygons[shapeNum].r + randr <= 1) {
					polygons[shapeNum].r += randr;
				}
				if (polygons[shapeNum].g + randg >= 0
						&& polygons[shapeNum].g + randg <= 1) {
					polygons[shapeNum].g += randg;

				}
				if (polygons[shapeNum].b + randb >= 0
						&& polygons[shapeNum].b + randb <= 1) {
					polygons[shapeNum].b += randb;

				}
				polygons[shapeNum].recolor();

				break;
			case 6: // transparency
				if (polygons[shapeNum].transparency + randr > 0
						&& polygons[shapeNum].transparency + randr < 1) {
					polygons[shapeNum].transparency += randr;
				}
				break;
			case 7: // order
				polygons[shapeNum].order += mutateAmt;
				Arrays.sort(polygons);
				break;

			// If variable amount of points add a case statement here for that
			}
		}

		redraw();
		fitness(img);
		switch (gene) {
		case 1:
			if (type == 1) {
				return new int[] { gene, shapeNum, xMoveAmt };
			} else {
				return new int[] { gene, shapeNum, polygonPoint, xMoveAmt };
			}
		case 2:
			if (type == 1) {
				return new int[] { gene, shapeNum, yMoveAmt };
			} else {
				return new int[] { gene, shapeNum, polygonPoint, yMoveAmt };
			}
		case 3:
			return new int[] { gene, shapeNum, mutateAmt };
		case 4:
			return new int[] { gene, shapeNum, mutateAmt };
		case 5:
			return new int[] { gene, shapeNum, (int) (randr * 100),
					(int) (randg * 100), (int) (randb * 100) };
		case 6:
			return new int[] { gene, shapeNum, (int) (randr * 100) };
		case 7:
			return new int[] { gene, shapeNum, mutateAmt };

		}
		return null;
	}

	public int[] mutate2(int[] instructions) {
		int gene = instructions[0];
		int mutateAmt = randInt(-10, 10);
		int shapeNum = instructions[1];

		// ===Oval mutation===
		if (type == 1) {
			switch (gene) {
			case 1:
				if (instructions[2] < 0) {
					ovals[shapeNum].x += randInt(instructions[2], -1);
				} else {
					ovals[shapeNum].x += randInt(1, instructions[2]);
				}
				break;
			case 2:
				if (instructions[2] < 0) {
					ovals[shapeNum].y += randInt(instructions[2], -1);
				} else {
					ovals[shapeNum].y += randInt(1, instructions[2]);
				}
				break;
			case 3:
				if (instructions[2] < 0) {
					ovals[shapeNum].width += randInt(instructions[2], -1);
				} else {
					ovals[shapeNum].width += randInt(1, instructions[2]);
				}
				break;
			case 4:
				if (instructions[2] < 0) {
					ovals[shapeNum].height += randInt(instructions[2], -1);
				} else {
					ovals[shapeNum].height += randInt(1, instructions[2]);
				}
				break;
			case 5:

				float randr;
				if (instructions[2] < 0) {
					randr = randInt(instructions[2], -1) / 100;
				} else {
					randr = randInt(1, instructions[2]) / 100;

				}
				if (ovals[shapeNum].r + randr >= 0
						&& ovals[shapeNum].r + randr <= 1) {
					ovals[shapeNum].r += randr;
				}

				float randg;
				if (instructions[3] < 0) {
					randg = randInt(instructions[3], -1) / 100;
				} else {
					randg = randInt(1, instructions[3]) / 100;
				}
				if (ovals[shapeNum].g + randg >= 0
						&& ovals[shapeNum].g + randg <= 1) {
					ovals[shapeNum].g += randg;
				}

				float randb;
				if (instructions[4] < 0) {
					randb = randInt(instructions[4], -1) / 100;
				} else {
					randb = randInt(1, instructions[4]) / 100;
				}
				if (ovals[shapeNum].b + randb >= 0
						&& ovals[shapeNum].b + randb <= 1) {
					ovals[shapeNum].b += randb;
				}
				ovals[shapeNum].recolor();

				break;
			case 6:
				float transparencyValue;
				if (instructions[2] < 0) {
					transparencyValue = randInt(instructions[2], -1) / 100;
				} else {
					transparencyValue = randInt(1, instructions[2]) / 100;

				}
				if (ovals[shapeNum].transparency + transparencyValue > 0
						&& ovals[shapeNum].transparency + transparencyValue < 1) {
					ovals[shapeNum].transparency += transparencyValue;
				}
				break;
			case 7:
				if (instructions[2] < 0) {
					ovals[shapeNum].order += randInt(instructions[2], -1);
				} else {
					ovals[shapeNum].order += randInt(1, instructions[2]);
				}
				Arrays.sort(ovals);
				break;
			}
		}
		// ====Polygon Mutation====
		else {
			int polygonPoint;

			switch (gene) {
			case 1: // one x coordinate of one point
				polygonPoint = instructions[2];
				if (instructions[2] < 0) {
					polygons[shapeNum].xpoints[polygonPoint] += randInt(
							instructions[3], -1);
				} else {
					polygons[shapeNum].xpoints[polygonPoint] += randInt(1,
							instructions[3]);
				}
				break;
			case 2: // one y coordinate of one point
				polygonPoint = instructions[2];
				if (instructions[2] < 0) {
					polygons[shapeNum].ypoints[polygonPoint] += randInt(
							instructions[3], -1);
				} else {
					polygons[shapeNum].ypoints[polygonPoint] += randInt(1,
							instructions[3]);
				}
				break;
			case 3: // x coordinates of whole shape
				for (int i = 0; i < polygons[shapeNum].xpoints.length; i++) {
					polygons[shapeNum].xpoints[i] += mutateAmt;
				}
				break;
			case 4: // y coordinates of whole shape
				for (int i = 0; i < polygons[shapeNum].ypoints.length; i++) {
					polygons[shapeNum].ypoints[i] += mutateAmt;
				}
				break;
			case 5: // color
				if (polygons[shapeNum].r + randr >= 0
						&& polygons[shapeNum].r + randr <= 1) {
					polygons[shapeNum].r += randr;
				}
				if (polygons[shapeNum].g + randg >= 0
						&& polygons[shapeNum].g + randg <= 1) {
					polygons[shapeNum].g += randg;

				}
				if (polygons[shapeNum].b + randb >= 0
						&& polygons[shapeNum].b + randb <= 1) {
					polygons[shapeNum].b += randb;

				}
				polygons[shapeNum].recolor();

				break;
			case 6: // transparency
				if (polygons[shapeNum].transparency + randr > 0
						&& polygons[shapeNum].transparency + randr < 1) {
					polygons[shapeNum].transparency += randr;
				}
				break;
			case 7: // order
				polygons[shapeNum].order += mutateAmt;
				Arrays.sort(polygons);
				break;

			// If variable amount of points add a case statement here for that
			}
		}

		redraw();
		fitness(img);
		switch (gene) {
		case 1:
			return new int[] { gene, xMoveAmt };
		case 2:
			return new int[] { gene, yMoveAmt };
		case 3:
			return new int[] { gene, mutateAmt };
		case 4:
			return new int[] { gene, mutateAmt };
		case 5:
			return new int[] { gene, (int) (randr * 100), (int) (randg * 100),
					(int) (randb * 100) };
		case 6:
			return new int[] { gene, (int) (randr * 100) };
		case 7:
			return new int[] { gene, mutateAmt };

		}
		return null;
	}

	BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public int randInt(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}

	public float randFlo(float min, float max) {
		Random r = new Random();
		return min + (max - min) * r.nextFloat();
	}
}
