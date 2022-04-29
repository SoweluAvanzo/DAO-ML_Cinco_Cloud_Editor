package de.jabc.cinco.meta.plugin.event.api.test

import de.jabc.cinco.meta.plugin.event.api.util.Fqn
import de.jabc.cinco.meta.plugin.event.api.util.Fqn.FqnFormatException
import java.lang.reflect.Type
import java.util.concurrent.ThreadLocalRandom
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

/**
 * Unit test (JUnit 5) for {@link Fqn}.
 * @author Fabian Storek
 */
class FqnTest {
	
	@Test
	def void test() {
		new Fqn('java.util.List<? extends bar.Foo>')
	}
	
	@Test
	def void testExceptions() {
		assertThrows(NullPointerException) [ new Fqn(null as CharSequence) ]
		assertThrows(NullPointerException) [ new Fqn(null as Type)         ]
		assertThrows(FqnFormatException)   [ new Fqn('')                   ]
		assertThrows(FqnFormatException)   [ new Fqn('java.lang.')         ]
		assertThrows(FqnFormatException)   [ new Fqn('List<Integer')       ]
		assertThrows(FqnFormatException)   [ new Fqn('ListInteger>')       ]
		assertThrows(FqnFormatException)   [ new Fqn('List<Integer>>')     ]
		assertThrows(FqnFormatException)   [ new Fqn('List<<Integer>')     ]
		assertThrows(FqnFormatException)   [ new Fqn('List Integer')       ]
	}
	
	@Test
	def void testConstructor() {
		check(null, 'A', null, null,  null)
		check('b',  'A', null, null,  null)
		check(null, '?', null, 'b.A', null)
		check(null, 'A', #[],  null,  null)
		check('b',  'A', #[],  null,  null)
		check(null, '?', null, 'b.A<>', null)
		check(null, 'A', #['c.D'], null, null)
		check('b',  'A', #['c.D'], null, null)
		check(null, '?', null, 'b.A<c.D>', null)
		check(null, 'A', #['c.D', 'e.F'], null, null)
		check('b',  'A', #['c.D', 'e.F'], null, null)
		check(null, '?', null, 'b.A<c.D, e.F>', null)
		check(null, 'A', #['c.D', 'e.F', 'g.H'], null, null)
		check('b',  'A', #['c.D', 'e.F', 'g.H'], null, null)
		check(null, '?', null, 'b.A<c.D, e.F, g.H>', null)
	}
	
	def private void check(String packageName, String className, String[] generics, String extendsConstraint, String superConstraint) {
		if (extendsConstraint !== null && superConstraint !== null) {
			throw new Exception('Test error: At either extendsConstraint or superConstraint or both must be null!')
		}
		if (extendsConstraint.notNullAndEmpty || superConstraint.notNullAndEmpty) {
			throw new Exception('Test error: extendsConstraint and superConstraint may not be empty!')
		}
		val fullyQualifiedName = '''«IF !packageName.nullOrEmpty»«packageName».«ENDIF»«className»'''
		val fqn = new Fqn('''«w»«fullyQualifiedName»«w»«IF generics !== null»<«FOR g: generics SEPARATOR ','»«w»«g»«w»«ENDFOR»>«w»«ENDIF»«IF extendsConstraint !== null»«w1»extends«w1»«extendsConstraint»«ENDIF»«IF superConstraint !== null»«w1»super«w1»«superConstraint»«ENDIF»''')
		assertEquals(fullyQualifiedName, fqn.fullyQualifiedName)
		assertEquals(packageName, fqn.packageName)
		assertEquals(className, fqn.className)
		assertEquals(generics === null, fqn.generics === null)
		assertEquals(generics !== null, fqn.hasGenerics)
		if (generics !== null) {
			assertEquals(generics.length, fqn.generics.length)
			assertIterableEquals(generics, fqn.generics.map[ fullyQualifiedNameWithSuffix ])
		}
		assertEquals(extendsConstraint, fqn.extendsConstraint?.fullyQualifiedNameWithSuffix)
		assertEquals(extendsConstraint !== null, fqn.hasExtendsConstraint)
		assertEquals(superConstraint, fqn.superConstraint?.fullyQualifiedNameWithSuffix)
		assertEquals(superConstraint !== null, fqn.hasSuperConstraint)
		assertEquals(extendsConstraint !== null || superConstraint !== null, fqn.hasConstraint)
	}
	
	def notNullAndEmpty(String str) {
		str !== null && str.empty
	}
	
	def w() {
		randomWhitespace
	}
	
	def w1() {
		randomWhitespace1
	}
	
	def String randomWhitespace() {
		val randGen = ThreadLocalRandom.current
		var result = ''
		if (randGen.nextInt(0, 3) == 0) {
			val whitespace = #[' ', '\t', '\n', '\r', '\f']
			val count = randGen.nextInt(1, 4)
			for (i: 0 ..< count) {
				val random = randGen.nextInt(0, whitespace.length)
				result += whitespace.get(random)
			}
		}
		return result
	}
	
	def String randomWhitespace1() {
		val randGen = ThreadLocalRandom.current
		var result = ''
		val whitespace = #[' ', '\t', '\n', '\r', '\f']
		val count = randGen.nextInt(1, 4)
		for (i: 0 ..< count) {
			val random = randGen.nextInt(0, whitespace.length)
			result += whitespace.get(random)
		}
		return result
	}
	
}