package externallibrary;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public interface ExternalLibrary extends org.eclipse.emf.ecore.EObject {
	
	// Mandatory
	public PanacheEntity getDelegate();
	public void delete();
	public void setExtension(String extension);
	public void setFilename(String filename);
	public void setParent(PanacheEntity parent);
	public String getExtension();
	public String getFilename();
	public PanacheEntity getParent();
	public entity.core.PyroProjectDB getProject();
	
	// Contents
	public java.util.List<externallibrary.ExternalActivityLibrary> getExternalActivityLibrary();
	public void setExternalActivityLibrary(java.util.Collection<externallibrary.ExternalActivityLibrary> e);
	public boolean removeExternalActivityLibrary(externallibrary.ExternalActivityLibrary apiElement, boolean delete);
	public boolean removeExternalActivityLibrary(externallibrary.ExternalActivityLibrary apiElement);
	public void clearExternalActivityLibrary();
	public void clearExternalActivityLibrary(boolean delete);
	public void addAllExternalActivityLibrary(java.util.Collection<externallibrary.ExternalActivityLibrary> apiElements);
	public void addExternalActivityLibrary(externallibrary.ExternalActivityLibrary apiElement);
	public java.util.List<externallibrary.ExternalActivityA> getExternalActivityA();
	public void setExternalActivityA(java.util.Collection<externallibrary.ExternalActivityA> e);
	public boolean removeExternalActivityA(externallibrary.ExternalActivityA apiElement, boolean delete);
	public boolean removeExternalActivityA(externallibrary.ExternalActivityA apiElement);
	public void clearExternalActivityA();
	public void clearExternalActivityA(boolean delete);
	public void addAllExternalActivityA(java.util.Collection<externallibrary.ExternalActivityA> apiElements);
	public void addExternalActivityA(externallibrary.ExternalActivityA apiElement);
	public java.util.List<externallibrary.ExternalAbstractActivityB> getExternalAbstractActivityB();
	public void setExternalAbstractActivityB(java.util.Collection<externallibrary.ExternalAbstractActivityB> e);
	public boolean removeExternalAbstractActivityB(externallibrary.ExternalAbstractActivityB apiElement, boolean delete);
	public boolean removeExternalAbstractActivityB(externallibrary.ExternalAbstractActivityB apiElement);
	public void clearExternalAbstractActivityB();
	public void clearExternalAbstractActivityB(boolean delete);
	public void addAllExternalAbstractActivityB(java.util.Collection<externallibrary.ExternalAbstractActivityB> apiElements);
	public void addExternalAbstractActivityB(externallibrary.ExternalAbstractActivityB apiElement);
	public java.util.List<externallibrary.ExternalAbstractActivityC> getExternalAbstractActivityC();
	public void setExternalAbstractActivityC(java.util.Collection<externallibrary.ExternalAbstractActivityC> e);
	public boolean removeExternalAbstractActivityC(externallibrary.ExternalAbstractActivityC apiElement, boolean delete);
	public boolean removeExternalAbstractActivityC(externallibrary.ExternalAbstractActivityC apiElement);
	public void clearExternalAbstractActivityC();
	public void clearExternalAbstractActivityC(boolean delete);
	public void addAllExternalAbstractActivityC(java.util.Collection<externallibrary.ExternalAbstractActivityC> apiElements);
	public void addExternalAbstractActivityC(externallibrary.ExternalAbstractActivityC apiElement);
	public java.util.List<externallibrary.ExternalActivityD> getExternalActivityD();
	public void setExternalActivityD(java.util.Collection<externallibrary.ExternalActivityD> e);
	public boolean removeExternalActivityD(externallibrary.ExternalActivityD apiElement, boolean delete);
	public boolean removeExternalActivityD(externallibrary.ExternalActivityD apiElement);
	public void clearExternalActivityD();
	public void clearExternalActivityD(boolean delete);
	public void addAllExternalActivityD(java.util.Collection<externallibrary.ExternalActivityD> apiElements);
	public void addExternalActivityD(externallibrary.ExternalActivityD apiElement);
	
	public static java.util.List<externallibrary.ExternalLibrary> find(String query, Object... params) {
		return entity.externallibrary.ExternalLibraryDB.find(query, params)
			.list()
			.stream()
			.map(n -> new externallibrary.impl.ExternalLibraryImpl((entity.externallibrary.ExternalLibraryDB) n))
			.collect(java.util.stream.Collectors.toList());
	}
}
