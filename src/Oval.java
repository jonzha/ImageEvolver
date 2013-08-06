import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

public class Oval {
	int x, y, width, height;
	float transparency;
	Color color;
	float r;
	float g;
	float b;

	public Oval(int x, int y, int width, int height, float r, float g, float b,
			float transparency) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.transparency = transparency;
		this.color = new Color(r, g, b);
	}

	public void draw(Graphics2D g2d) {
		g2d.setColor(color);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				transparency));
		g2d.fillOval(x, y, width, height);
	}
}
