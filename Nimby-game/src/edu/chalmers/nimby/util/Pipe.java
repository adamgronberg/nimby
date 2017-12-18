package edu.chalmers.nimby.util;

import java.util.LinkedList;
import java.util.List;

/**
 * A utility used to pipe resources with the possibility of one object handling the pipe input 
 * and another object handling the pipe output without them knowing of each other.
 * @author Viktor Sjölind
 *
 * @param <T> The type of objects handled in the pipe.
 */
public class Pipe<T> implements PipeIn<T>, PipeOut<T> {

	private List<T> pipe;
	
	/**
	 * Creates an empty pipe.
	 */
	public Pipe() {
		pipe = new LinkedList<T>();
	}
	
	@Override
	public final synchronized T pull() {
		if (pipe.isEmpty()) {
			return null;
		} else {
			return pipe.remove(0);
		}
	}
	
	@Override
	public final synchronized void put(final T putObject) {
		pipe.add(putObject);
	}
	
	@Override
	public final synchronized boolean hasNext() {
		return !pipe.isEmpty();
	}
}