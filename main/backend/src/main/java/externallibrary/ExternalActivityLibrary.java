package externallibrary;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public interface ExternalActivityLibrary extends org.eclipse.emf.ecore.EObject {
	
	// Mandatory
	public PanacheEntity getDelegate();
	public void delete();
	
	// EAttributes
	public String getName();
	public void setName(String e);
	public long getValueInteger();
	public void setValueInteger(long e);
	public long getValueLong();
	public void setValueLong(long e);
	public String getValueString();
	public void setValueString(String e);
	public java.util.List<Long> getValueIntegerList();
	public void setValueIntegerList(java.util.Collection<Long> e);
	public java.util.List<Long> getValueLongList();
	public void setValueLongList(java.util.Collection<Long> e);
	public java.util.List<String> getValueStringList();
	public void setValueStringList(java.util.Collection<String> e);
	
	// EReferences
	public java.util.List<externallibrary.ExternalActivityA> getActivities();
	public void setActivities(java.util.Collection<externallibrary.ExternalActivityA> e);
	public boolean removeActivities(externallibrary.ExternalActivityA apiElement, boolean delete);
	public boolean removeActivities(externallibrary.ExternalActivityA apiElement);
	public void clearActivities();
	public void clearActivities(boolean delete);
	public void addAllActivities(java.util.Collection<externallibrary.ExternalActivityA> apiElements);
	public void addActivities(externallibrary.ExternalActivityA apiElement);
	public java.util.List<externallibrary.ExternalActivityA> getRepresentsA();
	public void setRepresentsA(java.util.Collection<externallibrary.ExternalActivityA> e);
	public boolean removeRepresentsA(externallibrary.ExternalActivityA apiElement, boolean delete);
	public boolean removeRepresentsA(externallibrary.ExternalActivityA apiElement);
	public void clearRepresentsA();
	public void clearRepresentsA(boolean delete);
	public void addAllRepresentsA(java.util.Collection<externallibrary.ExternalActivityA> apiElements);
	public void addRepresentsA(externallibrary.ExternalActivityA apiElement);
	public java.util.List<externallibrary.ExternalAbstractActivityB> getRepresentsB();
	public void setRepresentsB(java.util.Collection<externallibrary.ExternalAbstractActivityB> e);
	public boolean removeRepresentsB(externallibrary.ExternalAbstractActivityB apiElement, boolean delete);
	public boolean removeRepresentsB(externallibrary.ExternalAbstractActivityB apiElement);
	public void clearRepresentsB();
	public void clearRepresentsB(boolean delete);
	public void addAllRepresentsB(java.util.Collection<externallibrary.ExternalAbstractActivityB> apiElements);
	public void addRepresentsB(externallibrary.ExternalAbstractActivityB apiElement);
	public java.util.List<externallibrary.ExternalAbstractActivityC> getRepresentsC();
	public void setRepresentsC(java.util.Collection<externallibrary.ExternalAbstractActivityC> e);
	public boolean removeRepresentsC(externallibrary.ExternalAbstractActivityC apiElement, boolean delete);
	public boolean removeRepresentsC(externallibrary.ExternalAbstractActivityC apiElement);
	public void clearRepresentsC();
	public void clearRepresentsC(boolean delete);
	public void addAllRepresentsC(java.util.Collection<externallibrary.ExternalAbstractActivityC> apiElements);
	public void addRepresentsC(externallibrary.ExternalAbstractActivityC apiElement);
	public java.util.List<externallibrary.ExternalActivityD> getRepresentsD();
	public void setRepresentsD(java.util.Collection<externallibrary.ExternalActivityD> e);
	public boolean removeRepresentsD(externallibrary.ExternalActivityD apiElement, boolean delete);
	public boolean removeRepresentsD(externallibrary.ExternalActivityD apiElement);
	public void clearRepresentsD();
	public void clearRepresentsD(boolean delete);
	public void addAllRepresentsD(java.util.Collection<externallibrary.ExternalActivityD> apiElements);
	public void addRepresentsD(externallibrary.ExternalActivityD apiElement);
	
	public static java.util.List<externallibrary.ExternalActivityLibrary> find(String query, Object... params) {
		return entity.externallibrary.ExternalActivityLibraryDB.find(query, params)
			.list()
			.stream()
			.map(n -> new externallibrary.impl.ExternalActivityLibraryImpl((entity.externallibrary.ExternalActivityLibraryDB) n))
			.collect(java.util.stream.Collectors.toList());
	}
}
