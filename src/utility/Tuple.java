package utility;

public class Tuple<X, Y> {
	public final X x;
	public final Y y;
	
	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}
	
	public X first() {
		return x;
	}
	
	public Y last() {
		return y;
	}
	
	@Override
	public String toString() {
		return "X:" + x + " Y:" + y;
	}
}
