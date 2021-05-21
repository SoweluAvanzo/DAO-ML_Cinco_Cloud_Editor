// Unit tests: de.jabc.cinco.meta.core.event.test.RegexContextTest

package de.jabc.cinco.meta.core.event.hub.impl

import de.jabc.cinco.meta.core.event.hub.Context
import java.util.regex.Pattern
import org.eclipse.xtend.lib.annotations.Accessors

import static extension java.util.regex.Pattern.compile

/**
 * An implementation of {@link Context} using regular expressions.
 * <p>
 * A RegexContext is matched by using Strings and regular expressions. Each
 * RegexContext contains a String {@link RegexContext#context context} that
 * fulfills two purposes. It is used to create a regex {@link
 * RegexContext#getPattern() pattern} and it is used to match against regexes
 * of other RegexContexts.
 * 
 * @author Fabian Storek
 */
@Accessors(PUBLIC_GETTER)
class RegexContext implements Context {
	
	/**
	 * This String fulfills two purposes. It is used to create the regex {@link
	 * RegexContext#getPattern() pattern} and it is used to match against
	 * regexes of other RegexContexts.
	 */
	val String context
	
	/**
	 * This is the regex pattern instance of this RegexContext. It should be
	 * called using {@link RegexContext#getPattern() getPattern()} as it is
	 * created lazily.
	 */
	@Accessors(NONE)
	var Pattern lazyPattern
	
	/**
	 * RegexContext constructor.
	 * 
	 * @param context is used to create a regex {@link
	 *                RegexContext#getPattern() pattern} and it is used to
	 *                match against regexes of other RegexContexts.
	 */
	new (String context) {
		this.context = context?: ''
	}
	
	/**
	 * Returns the regex {@link RegexContext#lazyPattern lazyPattern} instance
	 * of this RegexContext. It should always be called using this getter as it
	 * is created lazily.
	 */
	def getPattern() {
		if (lazyPattern === null) {
			lazyPattern = context.compile
		}
		return lazyPattern
	}
	
	/**
	 * Semantic comparison to another Context.
	 * <p>
	 * Returns a boolean weather or not this Context matches another Context.
	 * Contexts, that are considered {@linkplain Object#equals(Object) equal},
	 * will match. {@linkplain RegexContext#isEmpty() Empty} RegexContexts
	 * never match (except for the {@link Context#ANY ANY} Context). This
	 * relation is reflexive and symmetric.
	 * <p>
	 * Either this RegexContext's {@link RegexContext#getPattern() pattern}
	 * matches the {@link RegexContext#context context} String of the other or
	 * vice versa.
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
	 *         <li>{@code false}, if this or the other Context is {@linkplain RegexContext#isEmpty() empty}.</li>
	 *         <li>{@code true},  if this and the other Context are equal or identical.</li>
	 *         <li>{@code true},  if this Context's {@link RegexContext#getPattern() pattern} matches the other's {@link RegexContext#context context}.</li>
	 *         <li>{@code true},  if the other Context's {@link RegexContext#getPattern() pattern} matches this Context's {@link RegexContext#context context}.</li>
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
			RegexContext case other.empty:
				false
			case this == other:
				true
			RegexContext:
				this.pattern.matcher(other.context).matches ||
				other.pattern.matcher(this.context).matches
			default:
				false
		}
	}
	
	/**
	 * Returns a String representation of the RegexContext object.
	 */
	override toString() {
		'''«class.simpleName»("«context»")'''
	}
	
	/**
	 * Returns {@code true}, if this RegexContext {@link RegexContext#context
	 * context} is empty.
	 */
	def boolean isEmpty() {
		context.empty
	}
	
}