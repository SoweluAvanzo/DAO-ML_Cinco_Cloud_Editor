package externallibrary;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public interface ExternalActivityD extends org.eclipse.emf.ecore.EObject {
	
	// Mandatory
	public PanacheEntity getDelegate();
	public void delete();
	
	// EAttributes
	public long getOfD();
	public void setOfD(long e);
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
	public externallibrary.ExternalActivityD getReferencedOfD();
	public void setReferencedOfD(externallibrary.ExternalActivityD e);
	public java.util.List<externallibrary.ExternalActivityD> getRecerencingAbstractList();
	public void setRecerencingAbstractList(java.util.Collection<externallibrary.ExternalActivityD> e);
	public boolean removeRecerencingAbstractList(externallibrary.ExternalActivityD apiElement, boolean delete);
	public boolean removeRecerencingAbstractList(externallibrary.ExternalActivityD apiElement);
	public void clearRecerencingAbstractList();
	public void clearRecerencingAbstractList(boolean delete);
	public void addAllRecerencingAbstractList(java.util.Collection<externallibrary.ExternalActivityD> apiElements);
	public void addRecerencingAbstractList(externallibrary.ExternalActivityD apiElement);
	public java.util.List<externallibrary.ExternalAbstractActivityB> getReferencingList();
	public void setReferencingList(java.util.Collection<externallibrary.ExternalAbstractActivityB> e);
	public boolean removeReferencingList(externallibrary.ExternalAbstractActivityB apiElement, boolean delete);
	public boolean removeReferencingList(externallibrary.ExternalAbstractActivityB apiElement);
	public void clearReferencingList();
	public void clearReferencingList(boolean delete);
	public void addAllReferencingList(java.util.Collection<externallibrary.ExternalAbstractActivityB> apiElements);
	public void addReferencingList(externallibrary.ExternalAbstractActivityB apiElement);
	
	public static java.util.List<externallibrary.ExternalActivityD> find(String query, Object... params) {
		return entity.externallibrary.ExternalActivityDDB.find(query, params)
			.list()
			.stream()
			.map(n -> new externallibrary.impl.ExternalActivityDImpl((entity.externallibrary.ExternalActivityDDB) n))
			.collect(java.util.stream.Collectors.toList());
	}
}
