package info.scce.pyro.rest;

import java.util.Set;
import org.reflections.Reflections;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.scce.pyro.core.graphmodel.IdentifiableElement;

public class PolymorphyRegistrator {
	
	private static final String packageName = "info.scce.pyro";
	
	/*
	 * Registration of all SubTypes of IdentifiableElement inside the static package,
	 * for resolving json subtypes by the jackson-mapper. This method utilizes the
	 * Reflections-API of org.relfections (going under the  WTFPL License)
	 */
	public static void registerSubTypes(ObjectMapper mapper) {
		Reflections reflections = new Reflections(packageName);
		Set<Class<? extends IdentifiableElement>> classes = reflections.getSubTypesOf(IdentifiableElement.class);
		for(Class<? extends IdentifiableElement> clazz : classes) {
			mapper.registerSubtypes(
				new com.fasterxml.jackson.databind.jsontype.NamedType(clazz)
			);
		}
	}
}
