package externallibrary;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public interface ExternalLibrary extends org.eclipse.emf.ecore.EObject {
	
	// Mandatory
	public PanacheEntity getDelegate();
	public void delete();
	public void setExtension(String extension);
	public void setFilename(String filename);
	public String getExtension();
	public String getFilename();
	
	// Contents
	public java.util.List<externallibrary.ExternalActivityLibrary> getExternalActivityLibrary();
	public void setExternalActivityLibrary(java.util.Collection<externallibrary.ExternalActivityLibrary> e);
	public boolean removeExternalActivityLibrary(externallibrary.ExternalActivityLibrary apiElement, boolean delete);
	public boolean removeExternalActivityLibrary(externallibrary.ExternalActivityLibrary apiElement);
	public void clearExternalActivityLibrary();
	public void clearExternalActivityLibrary(boolean delete);
	public void addAllExternalActivityLibrary(java.util.Collection<externallibrary.ExternalActivityLibrary> apiElements);
	public void addExternalActivityLibrary(externallibrary.ExternalActivityLibrary apiElement);
	public java.util.List<externallibrary.ExternalActivity> getExternalActivity();
	public void setExternalActivity(java.util.Collection<externallibrary.ExternalActivity> e);
	public boolean removeExternalActivity(externallibrary.ExternalActivity apiElement, boolean delete);
	public boolean removeExternalActivity(externallibrary.ExternalActivity apiElement);
	public void clearExternalActivity();
	public void clearExternalActivity(boolean delete);
	public void addAllExternalActivity(java.util.Collection<externallibrary.ExternalActivity> apiElements);
	public void addExternalActivity(externallibrary.ExternalActivity apiElement);
	
	public static java.util.List<externallibrary.ExternalLibrary> find(String query, Object... params) {
		return entity.externallibrary.ExternalLibraryDB.find(query, params)
			.list()
			.stream()
			.map(n -> new externallibrary.impl.ExternalLibraryImpl((entity.externallibrary.ExternalLibraryDB) n))
			.collect(java.util.stream.Collectors.toList());
	}
}
