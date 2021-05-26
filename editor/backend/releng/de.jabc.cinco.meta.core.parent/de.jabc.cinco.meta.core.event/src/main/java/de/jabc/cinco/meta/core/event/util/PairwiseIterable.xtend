package de.jabc.cinco.meta.core.event.util

import de.jabc.cinco.meta.core.event.util.PairwiseIterable.Pair
import java.util.Iterator
import java.util.NoSuchElementException
import org.eclipse.xtend.lib.annotations.Data

import static de.jabc.cinco.meta.core.event.util.PairwiseIterable.Fill.*

/**
 * An Iterable, that combines two other Iterables in a pairwise fashion.
 * <p>
 * <b>Example:</b>
 * <pre>
 * new PairwiseIterable(
 *     #[4, 3, 2, 1, 0],
 *     #[a, b, c]
 * )
 * </pre>
 * results in
 * <pre>
 * #[
 *     (4, a),
 *     (3, b),
 *     (2, c),
 *     (1, null),
 *     (0, null)
 * ]
 * </pre>
 * By default the shorter Iterable will be filled with {@code null}. If you
 * wish to omit pairs with missing values, use {@link
 * PairwiseIterable#pairwise(Iterable, Iterable, PairwiseIterable.Fill)
 * pairwise(Iterable, Iterable, Fill)}. {@link PairwiseIterable.Fill Fill}
 * indicates which side to fill with {@code null}, if necessary.
 * 
 * @author Fabian Storek
 */
class PairwiseIterable<L, R> implements Iterable<Pair<L, R>> {
	
	val Iterable<L> left
	val Iterable<R> right
	val Fill fill
	
	new(Iterable<L> left, Iterable<R> right) {
		this.left  = left
		this.right = right
		this.fill  = BOTH
	}
	
	new(Iterable<L> left, Iterable<R> right, Fill fill) {
		this.left  = left
		this.right = right
		this.fill  = fill
	}
	
	override iterator() {
		new PairwiseIterator(this)
	}
	
	static class PairwiseIterator<L, R> implements Iterator<Pair<L, R>> {
		
		val Iterator<L> left
		val Iterator<R> right
		val Fill fill
		var int index
		
		new(PairwiseIterable<L, R> iterable) {
			this.left  = iterable.left.iterator
			this.right = iterable.right.iterator
			this.fill  = iterable.fill
			this.index = -1
		}
		
		override hasNext() {
			switch fill {
				case NONE:  left.hasNext && right.hasNext
				case LEFT:  left.hasNext
				case RIGHT: right.hasNext
				case BOTH:  left.hasNext || right.hasNext
			}
		}
		
		override next() {
			if (!hasNext) {
				throw new NoSuchElementException
			}
			val leftHasNext = left.hasNext
			val rightHasNext = right.hasNext
			val leftItem  = if (leftHasNext)  left.next  else null
			val rightItem = if (rightHasNext) right.next else null
			index += 1
			new Pair(leftItem, rightItem, leftHasNext, rightHasNext, index)
		}
		
	}
	
	/**
	 * Describes which side of a {@link PairwiseIterable} will be filled with
	 * {@code null} if they are of different length.
	 * <ol>
	 * <li>{@link PairwiseIterable.Fill#NONE NONE}</li>
	 * <li>{@link PairwiseIterable.Fill#LEFT LEFT}</li>
	 * <li>{@link PairwiseIterable.Fill#RIGHT RIGHT}</li>
	 * <li>{@link PairwiseIterable.Fill#BOTH BOTH}</li>
	 * </ol>
	 */
	static enum Fill {
		
		/** Describes that no side will be filled with {@code null}. */
		NONE,
		
		/** Describes that the left side will be filled with {@code null}. */
		LEFT,
		
		/** Describes that the right side will be filled with {@code null}. */
		RIGHT,
		
		/** Describes that both sides will be filled with {@code null}. */
		BOTH
		
	}
	
	/**
	 * The element type of a {@link PairwiseIterable}. 
	 * <p>
	 * Contains the {@linkplain PairwiseIterable.Pair#left left} and
	 * {@linkplain PairwiseIterable.Pair#getRight() right} side of the element,
	 * whether or not the {@linkplain PairwiseIterable.Pair#leftIsEmpty left}
	 * or {@linkplain PairwiseIterable.Pair#rightIsEmpty right} had to be
	 * filled in with {@code null}, as well as the
	 * {@linkplain PairwiseIterable.Pair#index index}.
	 */
	@Data
	static class Pair<L, R> {
		
		/**
		 * The left side of this Pair.
		 */
		val L left
		
		/**
		 * The left side of this Pair.
		 */
		val R right
		
		/**
		 * {@code true} if the left Iterable was shorter than the right and the
		 * left side had to be filled with {@code null}.
		 */
		val boolean leftIsEmpty
		
		/**
		 * {@code true} if the right Iterable was shorter than the left and the
		 * right side had to be filled with {@code null}.
		 */
		val boolean rightIsEmpty
		
		/**
		 * The index of the elements in their respective Iterables.
		 */
		val int index
		
	}
	
}
