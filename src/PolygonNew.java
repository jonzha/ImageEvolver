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

	public int compareTo(PolygonNew o) {
		if (this == o) {
			return 0;
		} else if (this.order > o.order) {
			return -1;
		} else {
			return 1;
		}
	}

	public PolygonNew copy() {
		return new PolygonNew(xpoints, ypoints, npoints, r, g, b, transparency,
				order);
	}

}
