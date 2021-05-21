package de.jabc.cinco.meta.plugin.event.api.test

import de.jabc.cinco.meta.plugin.event.api.util.EventApiExtension
import de.jabc.cinco.meta.plugin.event.api.util.EventEnum
import de.jabc.cinco.meta.plugin.event.api.util.Fqn
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

/**
 * Unit test (JUnit 5) for {@link EventEnum}.
 * @author Fabian Storek
 */
class EventEnumTest {
	
	extension EventApiExtension = new EventApiExtension
	
	@Test
	def void testParameters() {
		for (event: EventEnum.EVENTS) {
			val pairwise = event.payloadClass.declaredFields <> event.eventClass.methods.findFirst [ name == event.methodName ].parameters
			for (it: pairwise) {
				checkFqn(
					left.genericType.toFqn,
					right.parameterizedType.toFqn,
					'''
						Type of parameter «index» does not match.
							Event: «event.methodName»
							eventClass: «event.eventClass.name»
							payloadClass: «event.payloadClass.name»
							Parameter: «left.name»
					'''
				)
			}
		}
	}
	
	@Test
	def void testReturnTypes() {
		for (event: EventEnum.EVENTS) {
			checkFqn(
				event.payloadClass.genericInterfaces.head.toFqn.generics.last,
				event.eventClass.methods.findFirst[ name == event.methodName ].returnType.toFqn,
				'''
					Return type does not match.
						Event: «event.methodName»
						eventClass: «event.eventClass.name»
						payloadClass: «event.payloadClass.name»
				'''
			)
		}
	}
	
	def void checkFqn(Fqn expected, Fqn actual, String message) {
		if (expected.isPrimitive && actual.isPrimitiveWrapper) {
			assertEquals(
				expected.fullyQualifiedNameWithSuffix,
				actual.unwrapped.fullyQualifiedNameWithSuffix,
				message
			)
		}
		else if (expected.isPrimitiveWrapper && actual.isPrimitive) {
			assertEquals(
				expected.unwrapped.fullyQualifiedNameWithSuffix,
				actual.fullyQualifiedNameWithSuffix,
				message
			)
		}
		else {
			assertEquals(
				expected.fullyQualifiedNameWithSuffix,
				actual.fullyQualifiedNameWithSuffix,
				message
			)
		}
	}
	
}