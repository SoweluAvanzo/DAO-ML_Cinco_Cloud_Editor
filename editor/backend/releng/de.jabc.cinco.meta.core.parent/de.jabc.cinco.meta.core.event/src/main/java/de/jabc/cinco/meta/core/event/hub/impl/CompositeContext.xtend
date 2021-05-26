// Unit tests: de.jabc.cinco.meta.core.event.test.CompositeContextTest

package de.jabc.cinco.meta.core.event.hub.impl

import de.jabc.cinco.meta.core.event.hub.Context
import de.jabc.cinco.meta.core.event.util.EventCoreExtension
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * A {@link Context} in the form of a composite of identifier segments.
 * <p>
 * A CompositeContext is matched by an array of identifier segments separated
 * by dots ({@code .}):
 * <pre>"hello.disney.world"</pre>
 * consists of the segments
 * <pre>["hello", "disney", "world"]</pre>
 * and would match another CompositeContext
 * <pre>"hello.*.world",</pre>
 * where {@code *} is a wildcard.
 *  
 * @author Fabian Storek
 */
@Accessors(PUBLIC_GETTER)
class CompositeContext implements Context, Iterable<String> {

	extension EventCoreExtension = new EventCoreExtension

	val public static String SEPARATOR       = '''.'''
	val public static String SEPARATOR_REGEX = '''\.'''
	val public static String WILDCARD        = '''*'''
	
	/**
	 * An array of identifier segments.
	 */
	val String[] identifierSegments
	
	/**
	 * CompositeContext constructor.
	 * <p>
	 * A String or collection of Strings (vararg) will represent this
	 * CompositeContext. Each String may be a composite of String segments
	 * (using dot-syntax).
	 * <p>
	 * If multiple Strings are provided, they will be compounded into a single
	 * CompositeContext:
	 * <pre>
	 * new CompositeContext("hello", "disney.world")
	 *  -> "hello.disney.world"
	 * </pre>
	 * Superfluous dots ({@code .}) will be removed:
	 * <pre>
	 * new CompositeContext("hello.", "", ".", ".disney...world.")
	 *  -> "hello.disney.world"
	 * </pre>
	 * 
	 * @param identifiers A String or collection of Strings (vararg), that will
	 *                    represent this CompositeContext. Each String may be a
	 *                    composite of String segments (using dot-syntax).
	 */
	new (String ... identifiers) {
		if (identifiers.nullOrEmpty) {
			this.identifierSegments = #[]
		}
		else {
			this.identifierSegments = identifiers
				.reject [ s | s.nullOrEmpty ]
				.map    [ s | s.split(SEPARATOR_REGEX).toList ]
				.flatten
				.reject [ s | s.nullOrEmpty ]
		}
	}
	
	/**
	 * Returns a <b><u>new</u></b> CompositeContext instance with the provided
	 * {@code appendix}. (The original CompositeContext will not be modified.)
	 * 
	 * @param appendix A String or collection of Strings (vararg), that will be
	 *                 appended to to a copy of this Context.
	 * 
	 * @return a copy of this Context with the provided {@code appendix}.
	 */
	def CompositeContext append(String ... appendix) {
		if (appendix.nullOrEmpty) {
			new CompositeContext(identifierSegments)
		}
		else {
			new CompositeContext(identifierSegments + appendix)
		}
	}
	
	/**
	 * Semantic comparison to another Context.
	 * <p>
	 * Returns a boolean weather or not this Context matches another Context.
	 * Contexts, that are considered {@linkplain Object#equals(Object) equal},
	 * will match. {@linkplain CompositeContext#isEmpty() Empty}
	 * CompositeContexts never match (except for the {@link Context#ANY ANY}
	 * Context). This relation is reflexive and symmetric.
	 * <p>
	 * Segments will compared pairwise. Segments containing only the wildcard
	 * ({@code *}) will match any other segment. If all segments match, the
	 * entire CompositeContext will match. (This implies, that the
	 * CompositeContexts must have the same length.)
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
	 * @return Checking from top to bottom:
	 *         <ol>
	 *         <li>{@code true},  if this or the other Context is the {@link Context#ANY ANY} Context.</li>
	 *         <li>{@code false}, if this or the other Context is {@linkplain CompositeContext#isEmpty() empty}.</li>
	 *         <li>{@code true},  if this and the other Context are equal or identical.</li>
	 *         <li>{@code true},  if this Context's {@link CompositeContext#identifierSegments identifierSegments} match the other's.</li>
	 *         <li>{@code false}, otherwise. (This Context does not match the other Context.)</li>
	 *         </ol>
	 * 
	 * @see Context#operator_spaceship(Context) <=> (Spaceship operator)
	 */
	override matches(Context other) {
		switch other {
			case ANY:
				true
			case this.empty:
				false
			CompositeContext case other.empty:
				false
			case this == other:
				true
			CompositeContext:
				this.length == other.length &&
				(this <> other).forall[
					left  == right    ||
					left  == WILDCARD ||
					right == WILDCARD
				]
			default:
				false
		}
	}
	
	/**
	 * Returns an iterator over all {@link CompositeContext#identifierSegments
	 * identifierSegments}.
	 */
	override iterator() {
		identifierSegments.iterator
	}
	
	/**
	 * Two CompositeContexts are equal, if all of their segments are equal.
	 * <p>
	 * Two equal CompositeContexts will always {@linkplain CompositeContext#matches(Context)
	 * match}, but two matching CompositeContexts may not be equal.
	 * <p>
	 * <b>Syntactic sugar:</b>
	 * <p>
	 * You may also use the {@linkplain CompositeContext#operator_equals(Object) equals operator}:
	 * <pre>
	 * thisContext == otherContext
	 * </pre>
	 * 
	 * @param other The <i>other</i> Context this context will be compared to.
	 * 
	 * @return {@code true}, iff all of their segments are equal.
	 * 
	 * @see CompositeContext#operator_equals(Object) == (Equals operator)
	 */
	override equals(Object other) {
		switch other {
			case this === other:
				true
			CompositeContext:
				(this <> other).forall[left == right]
			default:
				false
		}
	}
	
	/**
	 * Syntactic sugar for {@link CompositeContext#equals(Object) equals(Object)}.
	 * 
	 * @see CompositeContext#equals(Object) equals(Object)
	 */
	def boolean == (Object other) {
		this.equals(other)
	}
	
	/**
	 * Returns a String representation of the CompositeContext object.
	 */
	override toString() {
		'''«class.simpleName»("«identifier»")'''
	}
	
	/**
	 * Returns the whole identifier String.
	 */
	def String getIdentifier() {
		identifierSegments.join(SEPARATOR)
	}
	
	/**
	 * Returns the length (number of identifier segments).
	 */
	def int length() {
		identifierSegments.length
	}
	
	/**
	 * Returns {@code true}, if this CompositeContext contains no identifier
	 * segments ({@linkplain CompositeContext#length() length} of 0).
	 */
	def boolean isEmpty() {
		identifierSegments.empty
	}
	
	/**
	 * Returns the identifier segment at the {@code index}.
	 */
	def String get(int index) {
		identifierSegments.get(index)
	}
	
}
