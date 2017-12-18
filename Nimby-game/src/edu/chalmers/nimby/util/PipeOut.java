package edu.chalmers.nimby.util;

/**
 * Used to handle the output of a {@link Pipe}.
 * @author Viktor Sjölind
 *
 * @param <T> The type of objects handled.
 */
public interface PipeOut<T> {

	/**
	 * Pulls the first object from the pipe, removing it from the pipe.
	 * @return the first object from the pipe.
	 */
	T pull();
	
	/**
	 * @return True if there is more in the pipe, else false.
	 */
	boolean hasNext();
}
