package edu.chalmers.nimby.util;

/**
 * Used to handle the input of a {@link Pipe}.
 * @author Viktor Sjölind
 *
 * @param <T> The type of objects handled.
 */
public interface PipeIn<T> {
	
	/**
	 * Puts the provided object last in the pipe.
	 * @param putObject the object to put to pipe.
	 */
	void put(T putObject);
}
