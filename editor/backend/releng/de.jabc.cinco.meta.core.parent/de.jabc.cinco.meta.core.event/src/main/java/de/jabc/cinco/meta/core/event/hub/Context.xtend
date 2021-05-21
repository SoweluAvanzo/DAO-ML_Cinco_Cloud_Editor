package de.jabc.cinco.meta.core.event.hub

/**
 * An interface for context definitions.
 * <p>
 * Use the {@link Context#matches(Context) matches(Context)} method (or its
 * syntactic sugar {@linkplain Context#operator_spaceship(Context) <=>}) for a
 * semantic comparison.
 * <p>
 * {@link CompositeContext} is an implementation for this interface.
 * <p>
 * <b>Implementing this interface:</b>
 * <p>
 * <ul>
 * <li>{@link Context#matches(Context) matches(Context)}</li>
 * <ul>
 * <li>Returns a boolean weather or not this Context matches another Context.</li>
 * <li>Contexts, that are considered {@linkplain Object#equals(Object) equal}, must match.</li>
 * <li>The {@link Context#ANY ANY} Context must match every other Context.</li>
 * <li>This relation must be reflexive and symmetric.</li>
 * </ul>
 * </ul>
 * 
 * @author Fabian Storek
 */
interface Context {
	
	/**
	 * The <i>any</i> Context matches every Context.
	 */
	val Context ANY = new Context {
		
		override matches(Context other) {
			true
		}
		
		override toString() {
			'Context.ANY'
		}
		
	}
	
	/**
	 * Semantic comparison to another Context.
	 * <p>
	 * <b>Implementing this method:</b>
	 * <p>
	 * <ul>
	 * <li>Returns a boolean weather or not this Context matches another Context.</li>
	 * <li>Contexts, that are considered {@linkplain Object#equals(Object) equal}, must match.</li>
	 * <li>The {@link Context#ANY ANY} Context must match every other Context.</li>
	 * <li>This relation must be reflexive and symmetric.</li>
	 * </ul>
	 * <p>
	 * <b>Syntactic sugar:</b>
	 * <p>
	 * You may also use the {@linkplain Context#operator_spaceship(Context) spaceship operator}:
	 * <pre>
	 * thisContext <=> otherContext
	 * </pre>
	 * 
	 * @param other The <i>other</i> Context this Context will be compared to.
	 * 
	 * @return <ul>
	 *         <li>{@code true}, if this Context matches the other Context.</li>
	 *         <li>{@code true}, if this and the other Context are equal or identical.</li>
	 *         <li>{@code true}, if this or the other Context is the {@link Context#ANY ANY} Context.</li>
	 *         <li>{@code false}, if this Context does not match the other Context.</li>
	 *         </ul>
	 * 
	 * @see Context#operator_spaceship(Context) <=> (Spaceship operator)
	 */
	def boolean matches(Context other)
	
}
