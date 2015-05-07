package phonics;

public class Rectangle2D {

	double x;
	double y;
	double width;
	double height;

	public Rectangle2D(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public boolean contains(double x0, double y0) {
		return (x0 >= x && x0 <= x + width) && (y0 >= y && y0 <= y + height);
	}

}
