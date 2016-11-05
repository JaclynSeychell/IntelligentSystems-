package utility;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;

public class CircularFifoQueue<T> extends AbstractQueue<T> implements Iterable<T> {
	private int maxSize;
	private LinkedList<T> elements = new LinkedList<T>();
	
	public CircularFifoQueue(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public int getMaxSize() {
		return maxSize;
	}
	
	public boolean isFull() {
		return elements.size() >= maxSize;
	}
	
	@Override
	public boolean add(T t) {
		if(isFull()) {
			elements.removeFirst();
		}
		elements.add(t);
		return true;
	}

	@Override
	public Iterator<T> iterator() {
		return elements.iterator();
	}

	@Override
	public int size() {
		return elements.size();
	}

	@Override
	public boolean offer(T t) {
		return add(t);
	}

	@Override
	public T poll() {
		return elements.removeFirst();
	}

	@Override
	public T peek() {
		return elements.getFirst();
	}
}
