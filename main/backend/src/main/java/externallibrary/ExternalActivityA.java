package externallibrary;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public interface ExternalActivityA extends externallibrary.ExternalAbstractActivityB {
	
	// Mandatory
	public PanacheEntity getDelegate();
	public void delete();
	
	// EAttributes
	public String getOfA();
	public void setOfA(String e);
	public String getDescription();
	public void setDescription(String e);
	
	public static java.util.List<externallibrary.ExternalActivityA> find(String query, Object... params) {
		return entity.externallibrary.ExternalActivityADB.find(query, params)
			.list()
			.stream()
			.map(n -> new externallibrary.impl.ExternalActivityAImpl((entity.externallibrary.ExternalActivityADB) n))
			.collect(java.util.stream.Collectors.toList());
	}
}
