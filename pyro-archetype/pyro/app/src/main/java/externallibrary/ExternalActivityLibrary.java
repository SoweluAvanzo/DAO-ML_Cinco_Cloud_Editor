package externallibrary;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public interface ExternalActivityLibrary extends org.eclipse.emf.ecore.EObject {
	
	// Mandatory
	public PanacheEntity getDelegate();
	public void delete();
	
	// EReferences
	public java.util.List<externallibrary.ExternalActivity> getActivities();
	public void setActivities(java.util.Collection<externallibrary.ExternalActivity> e);
	public boolean removeActivities(externallibrary.ExternalActivity apiElement, boolean delete);
	public boolean removeActivities(externallibrary.ExternalActivity apiElement);
	public void clearActivities();
	public void clearActivities(boolean delete);
	public void addAllActivities(java.util.Collection<externallibrary.ExternalActivity> apiElements);
	public void addActivities(externallibrary.ExternalActivity apiElement);
	
	public static java.util.List<externallibrary.ExternalActivityLibrary> find(String query, Object... params) {
		return entity.externallibrary.ExternalActivityLibraryDB.find(query, params)
			.list()
			.stream()
			.map(n -> new externallibrary.impl.ExternalActivityLibraryImpl((entity.externallibrary.ExternalActivityLibraryDB) n))
			.collect(java.util.stream.Collectors.toList());
	}
}
