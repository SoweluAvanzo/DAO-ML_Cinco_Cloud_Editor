package de.jabc.cinco.meta.core.utils.registry;

import java.util.Map;
import java.util.function.Function;

/**
 * 
 * @author Steve Bosselmann
 */
public class NonEmptyRegistry<K,V> extends Registry<K,V> {

	// generated
	private static final long serialVersionUID = -5040056200721537195L;
	
	private Function<K,V> valueSupplier;
	
	public NonEmptyRegistry(Function<K,V> valueSupplier) {
		super();
		this.valueSupplier = valueSupplier;
	}
	
	protected NonEmptyRegistry(Function<K,V> valueSupplier, Map<K, V> map) {
		super(map);
		this.valueSupplier = valueSupplier;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		V val = super.get(key);
		if (val != null)
			return val;
		val = valueSupplier.apply((K) key);
		put((K)key, val);
		return val;
	}
	
	public Function<K,V> getValueSupplier() {
		return valueSupplier;
	}
	
	public void setValueSupplier(Function<K,V> valueSupplier) {
		this.valueSupplier = valueSupplier;
	}
}
