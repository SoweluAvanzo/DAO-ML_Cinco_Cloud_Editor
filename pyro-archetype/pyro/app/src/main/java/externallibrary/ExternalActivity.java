package externallibrary;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public interface ExternalActivity extends org.eclipse.emf.ecore.EObject {
	
	// Mandatory
	public PanacheEntity getDelegate();
	public void delete();
	
	// EAttributes
	public String getName();
	public void setName(String e);
	public String getDescription();
	public void setDescription(String e);
	
	public static java.util.List<externallibrary.ExternalActivity> find(String query, Object... params) {
		return entity.externallibrary.ExternalActivityDB.find(query, params)
			.list()
			.stream()
			.map(n -> new externallibrary.impl.ExternalActivityImpl((entity.externallibrary.ExternalActivityDB) n))
			.collect(java.util.stream.Collectors.toList());
	}
}
