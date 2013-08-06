import java.awt.image.BufferedImage;

public class Member {
	double fitness;
	Oval[] ovals;
	BufferedImage img;

	public Member(double fitness, Oval[] ovals, BufferedImage img) {
		this.fitness = fitness;
		this.ovals = ovals;
		this.img = img;
	}

	public Member copy() {
		return new Member(fitness, ovals, img);
	}
}
