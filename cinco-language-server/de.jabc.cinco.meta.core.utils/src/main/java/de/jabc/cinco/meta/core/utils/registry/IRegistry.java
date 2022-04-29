package de.jabc.cinco.meta.core.utils.registry;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Steve Bosselmann
 */
public interface IRegistry<K,T> {
	
	T lookup(K key);
	
	<U extends T> Map<K, U> lookupByType(Class<U> type);
	
	<U extends T> Map<K, U> lookupByType(Class<U> type, boolean includeSubTypes);
	
	List<K> keys();
	
	List<T> values();
}
