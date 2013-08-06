import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

public class Oval implements Comparable<Oval> {
	int x, y, width, height;
	float transparency;
	Color color;
	float r;
	float g;
	float b;
	float order;

	public Oval(int x, int y, int width, int height, float r, float g, float b,
			float transparency, float order) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.transparency = transparency;
		this.r = r;
		this.g = g;
		this.b = b;
		this.color = new Color(r, g, b);
		this.order = order;
	}

	public void draw(Graphics2D g2d) {
		g2d.setColor(color);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				transparency));
		g2d.fillOval(x, y, width, height);
	}

	public Oval copy() {
		return new Oval(x, y, width, height, r, g, b, transparency, order);
	}

	public void recolor() {
		color = new Color(r, g, b);

	}

	public int compareTo(Oval o) {
		if (this == o) {
			return 0;
		} else if (this.order > o.order) {
			return -1;
		} else {
			return 1;
		}
	}
}
