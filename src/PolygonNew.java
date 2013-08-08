import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

public class PolygonNew extends Polygon implements Comparable<PolygonNew> {
	float transparency;
	Color color;
	float r;
	float g;
	float b;
	float order;

	public PolygonNew(int[] xPoints, int[] yPoints, int nPoints, float r,
			float g, float b, float transparency, float order) {
		this.xpoints = xPoints;
		this.ypoints = yPoints;
		this.npoints = nPoints;
		this.r = r;
		this.g = g;
		this.b = b;
		this.color = new Color(r, g, b);
		this.transparency = transparency;
		this.order = order;
	}

	public void draw(Graphics2D g2d) {
		g2d.setColor(color);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				transparency));
		g2d.fillPolygon(this);
	}

	public void recolor() {
		color = new Color(r, g, b);

	}

	public boolean compareArray(int[] one, int[] two) {
		for (int i = 0; i < one.length; i++) {
			if (one[i] != two[i]) {
				return false;
			}
		}
		return true;
	}

	public int compareTo(PolygonNew o) {
		if (compareArray(o.xpoints, xpoints)
				&& compareArray(ypoints, o.ypoints) && npoints == o.npoints
				&& r == o.r && g == o.g && b == o.b
				&& transparency == o.transparency && order == o.order) {
			return 0;
		} else if (this.order > o.order) {
			return -1;
		} else {
			return 1;
		}
	}

	// I CAN'T BELIEVE I SPENT THAT MUCH TIME TRYING TO FIX THIS STUPID POLYGON
	// THING AND THE MISTAKE WAS HERE. I SHOULD HAVE KNOWN. NEVER RETURN A
	// REFERENCE TO AN ARRAY. ALWAYS MAKE A COPY METHOD OMFG.
	public int[] copyXPoints() {
		int[] ret = new int[xpoints.length];
		for (int i = 0; i < xpoints.length; i++) {
			ret[i] = xpoints[i];
		}
		return ret;
	}

	public int[] copyYPoints() {
		int[] ret = new int[ypoints.length];
		for (int i = 0; i < ypoints.length; i++) {
			ret[i] = ypoints[i];
		}
		return ret;
	}

	public PolygonNew copy() {
		return new PolygonNew(copyXPoints(), copyYPoints(), npoints, r, g, b,
				transparency, order);
	}

}
