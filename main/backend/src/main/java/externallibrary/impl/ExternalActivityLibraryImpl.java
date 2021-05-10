package externallibrary.impl;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public class ExternalActivityLibraryImpl implements externallibrary.ExternalActivityLibrary {
	
	private final entity.externallibrary.ExternalActivityLibraryDB delegate;
	
	public ExternalActivityLibraryImpl(
		entity.externallibrary.ExternalActivityLibraryDB delegate
	) {
		this.delegate = delegate;
	}
	
	public ExternalActivityLibraryImpl(
	) {
		this.delegate = new entity.externallibrary.ExternalActivityLibraryDB();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof externallibrary.ExternalActivityLibrary
			&& ((externallibrary.ExternalActivityLibrary) obj).getId().equals(getId());
	}
	
	@Override
	public int hashCode() {
		return delegate.id.intValue();
	}
	
	@Override
	public String getId() {
		return Long.toString(this.delegate.id);
	}
	
	@Override
	public long getDelegateId() {
		return this.delegate.id;
	}
	
	@Override
	public entity.externallibrary.ExternalActivityLibraryDB getDelegate() {
		return this.delegate;
	}
	
	@Override
    public org.eclipse.emf.ecore.EObject eContainer() {
        return null;
    }
	
	@Override
	public void delete() {
		this.delegate.delete();
	}
	
	// EAttributes
	@Override
	public String getName() {
		return this.delegate.name;
	}
	
	@Override
	public void setName(String e) {
		this.delegate.name = e;
		this.delegate.persist();
	}
	
	@Override
	public long getValueInteger() {
		return this.delegate.valueinteger;
	}
	
	@Override
	public void setValueInteger(long e) {
		this.delegate.valueinteger = e;
		this.delegate.persist();
	}
	
	@Override
	public long getValueLong() {
		return this.delegate.valuelong;
	}
	
	@Override
	public void setValueLong(long e) {
		this.delegate.valuelong = e;
		this.delegate.persist();
	}
	
	@Override
	public String getValueString() {
		return this.delegate.valuestring;
	}
	
	@Override
	public void setValueString(String e) {
		this.delegate.valuestring = e;
		this.delegate.persist();
	}
	
	@Override
	public java.util.List<Long> getValueIntegerList() {
		return this.delegate.valueintegerlist.stream().collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public void setValueIntegerList(java.util.Collection<Long> e) {
		this.delegate.valueintegerlist = e;
		this.delegate.persist();
	}
	
	@Override
	public java.util.List<Long> getValueLongList() {
		return this.delegate.valuelonglist.stream().collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public void setValueLongList(java.util.Collection<Long> e) {
		this.delegate.valuelonglist = e;
		this.delegate.persist();
	}
	
	@Override
	public java.util.List<String> getValueStringList() {
		return this.delegate.valuestringlist.stream().collect(java.util.stream.Collectors.toList());
	}
	
	@Override
	public void setValueStringList(java.util.Collection<String> e) {
		this.delegate.valuestringlist = e;
		this.delegate.persist();
	}
	
	// EReferences
	@Override
	public java.util.List<externallibrary.ExternalActivityA> getActivities() {
		java.util.Collection<PanacheEntity> entityList = this.delegate.getActivities();
		java.util.List<externallibrary.ExternalActivityA> apiList = entityList.stream().map(n->
			new externallibrary.impl.ExternalActivityAImpl(
				(entity.externallibrary.ExternalActivityADB) n
			)
		).collect(java.util.stream.Collectors.toList());
		return apiList;
	}
	
	@Override
	public void setActivities(java.util.Collection<externallibrary.ExternalActivityA> e) {
		java.util.Collection<PanacheEntity> entityList = e.stream().map(n->
			n.getDelegate()
		).collect(java.util.stream.Collectors.toList());
		this.delegate.setActivities(entityList);
		this.delegate.persist();
	}
	
	@Override
	public boolean removeActivities(externallibrary.ExternalActivityA apiElement, boolean delete) {
		PanacheEntity dbElement = apiElement.getDelegate();
		return ((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).removeActivities(dbElement, delete);
	}
	
	@Override
	public boolean removeActivities(externallibrary.ExternalActivityA apiElement) {
		return removeActivities(apiElement, true);
	}
	
	@Override
	public void clearActivities() {
		((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).clearActivities();
	}
	
	@Override
	public void clearActivities(boolean delete) {
		((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).clearActivities(delete);
	}
	
	@Override
	public void addAllActivities(java.util.Collection<externallibrary.ExternalActivityA> apiElements) {
		for(externallibrary.ExternalActivityA apiElement : apiElements) {
			addActivities(apiElement);
		}
	}
	
	@Override
	public void addActivities(externallibrary.ExternalActivityA apiElement) {
		PanacheEntity dbElement = apiElement.getDelegate();
		((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).addActivities(dbElement);
	}
	
	@Override
	public java.util.List<externallibrary.ExternalActivityA> getRepresentsA() {
		java.util.Collection<PanacheEntity> entityList = this.delegate.getRepresentsA();
		java.util.List<externallibrary.ExternalActivityA> apiList = entityList.stream().map(n->
			new externallibrary.impl.ExternalActivityAImpl(
				(entity.externallibrary.ExternalActivityADB) n
			)
		).collect(java.util.stream.Collectors.toList());
		return apiList;
	}
	
	@Override
	public void setRepresentsA(java.util.Collection<externallibrary.ExternalActivityA> e) {
		java.util.Collection<PanacheEntity> entityList = e.stream().map(n->
			n.getDelegate()
		).collect(java.util.stream.Collectors.toList());
		this.delegate.setRepresentsA(entityList);
		this.delegate.persist();
	}
	
	@Override
	public boolean removeRepresentsA(externallibrary.ExternalActivityA apiElement, boolean delete) {
		PanacheEntity dbElement = apiElement.getDelegate();
		return ((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).removeRepresentsA(dbElement, delete);
	}
	
	@Override
	public boolean removeRepresentsA(externallibrary.ExternalActivityA apiElement) {
		return removeRepresentsA(apiElement, true);
	}
	
	@Override
	public void clearRepresentsA() {
		((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).clearRepresentsA();
	}
	
	@Override
	public void clearRepresentsA(boolean delete) {
		((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).clearRepresentsA(delete);
	}
	
	@Override
	public void addAllRepresentsA(java.util.Collection<externallibrary.ExternalActivityA> apiElements) {
		for(externallibrary.ExternalActivityA apiElement : apiElements) {
			addRepresentsA(apiElement);
		}
	}
	
	@Override
	public void addRepresentsA(externallibrary.ExternalActivityA apiElement) {
		PanacheEntity dbElement = apiElement.getDelegate();
		((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).addRepresentsA(dbElement);
	}
	
	@Override
	public java.util.List<externallibrary.ExternalAbstractActivityB> getRepresentsB() {
		java.util.Collection<PanacheEntity> entityList = this.delegate.getRepresentsB();
		java.util.List<externallibrary.ExternalAbstractActivityB> apiList = entityList.stream().map(n->
			{
				if(n instanceof entity.externallibrary.ExternalActivityADB) {
					return (externallibrary.ExternalAbstractActivityB)
						new externallibrary.impl.ExternalActivityAImpl(
							(entity.externallibrary.ExternalActivityADB) n
						);
				}
				return null;
			}
		).collect(java.util.stream.Collectors.toList());
		return apiList;
	}
	
	@Override
	public void setRepresentsB(java.util.Collection<externallibrary.ExternalAbstractActivityB> e) {
		java.util.Collection<PanacheEntity> entityList = e.stream().map(n->
			n.getDelegate()
		).collect(java.util.stream.Collectors.toList());
		this.delegate.setRepresentsB(entityList);
		this.delegate.persist();
	}
	
	@Override
	public boolean removeRepresentsB(externallibrary.ExternalAbstractActivityB apiElement, boolean delete) {
		PanacheEntity dbElement = apiElement.getDelegate();
		return ((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).removeRepresentsB(dbElement, delete);
	}
	
	@Override
	public boolean removeRepresentsB(externallibrary.ExternalAbstractActivityB apiElement) {
		return removeRepresentsB(apiElement, true);
	}
	
	@Override
	public void clearRepresentsB() {
		((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).clearRepresentsB();
	}
	
	@Override
	public void clearRepresentsB(boolean delete) {
		((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).clearRepresentsB(delete);
	}
	
	@Override
	public void addAllRepresentsB(java.util.Collection<externallibrary.ExternalAbstractActivityB> apiElements) {
		for(externallibrary.ExternalAbstractActivityB apiElement : apiElements) {
			addRepresentsB(apiElement);
		}
	}
	
	@Override
	public void addRepresentsB(externallibrary.ExternalAbstractActivityB apiElement) {
		PanacheEntity dbElement = apiElement.getDelegate();
		((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).addRepresentsB(dbElement);
	}
	
	@Override
	public java.util.List<externallibrary.ExternalAbstractActivityC> getRepresentsC() {
		java.util.Collection<PanacheEntity> entityList = this.delegate.getRepresentsC();
		java.util.List<externallibrary.ExternalAbstractActivityC> apiList = entityList.stream().map(n->
			{
				if(n instanceof entity.externallibrary.ExternalActivityADB) {
					return (externallibrary.ExternalAbstractActivityC)
						new externallibrary.impl.ExternalActivityAImpl(
							(entity.externallibrary.ExternalActivityADB) n
						);
				}
				return null;
			}
		).collect(java.util.stream.Collectors.toList());
		return apiList;
	}
	
	@Override
	public void setRepresentsC(java.util.Collection<externallibrary.ExternalAbstractActivityC> e) {
		java.util.Collection<PanacheEntity> entityList = e.stream().map(n->
			n.getDelegate()
		).collect(java.util.stream.Collectors.toList());
		this.delegate.setRepresentsC(entityList);
		this.delegate.persist();
	}
	
	@Override
	public boolean removeRepresentsC(externallibrary.ExternalAbstractActivityC apiElement, boolean delete) {
		PanacheEntity dbElement = apiElement.getDelegate();
		return ((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).removeRepresentsC(dbElement, delete);
	}
	
	@Override
	public boolean removeRepresentsC(externallibrary.ExternalAbstractActivityC apiElement) {
		return removeRepresentsC(apiElement, true);
	}
	
	@Override
	public void clearRepresentsC() {
		((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).clearRepresentsC();
	}
	
	@Override
	public void clearRepresentsC(boolean delete) {
		((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).clearRepresentsC(delete);
	}
	
	@Override
	public void addAllRepresentsC(java.util.Collection<externallibrary.ExternalAbstractActivityC> apiElements) {
		for(externallibrary.ExternalAbstractActivityC apiElement : apiElements) {
			addRepresentsC(apiElement);
		}
	}
	
	@Override
	public void addRepresentsC(externallibrary.ExternalAbstractActivityC apiElement) {
		PanacheEntity dbElement = apiElement.getDelegate();
		((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).addRepresentsC(dbElement);
	}
	
	@Override
	public java.util.List<externallibrary.ExternalActivityD> getRepresentsD() {
		java.util.Collection<PanacheEntity> entityList = this.delegate.getRepresentsD();
		java.util.List<externallibrary.ExternalActivityD> apiList = entityList.stream().map(n->
			new externallibrary.impl.ExternalActivityDImpl(
				(entity.externallibrary.ExternalActivityDDB) n
			)
		).collect(java.util.stream.Collectors.toList());
		return apiList;
	}
	
	@Override
	public void setRepresentsD(java.util.Collection<externallibrary.ExternalActivityD> e) {
		java.util.Collection<PanacheEntity> entityList = e.stream().map(n->
			n.getDelegate()
		).collect(java.util.stream.Collectors.toList());
		this.delegate.setRepresentsD(entityList);
		this.delegate.persist();
	}
	
	@Override
	public boolean removeRepresentsD(externallibrary.ExternalActivityD apiElement, boolean delete) {
		PanacheEntity dbElement = apiElement.getDelegate();
		return ((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).removeRepresentsD(dbElement, delete);
	}
	
	@Override
	public boolean removeRepresentsD(externallibrary.ExternalActivityD apiElement) {
		return removeRepresentsD(apiElement, true);
	}
	
	@Override
	public void clearRepresentsD() {
		((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).clearRepresentsD();
	}
	
	@Override
	public void clearRepresentsD(boolean delete) {
		((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).clearRepresentsD(delete);
	}
	
	@Override
	public void addAllRepresentsD(java.util.Collection<externallibrary.ExternalActivityD> apiElements) {
		for(externallibrary.ExternalActivityD apiElement : apiElements) {
			addRepresentsD(apiElement);
		}
	}
	
	@Override
	public void addRepresentsD(externallibrary.ExternalActivityD apiElement) {
		PanacheEntity dbElement = apiElement.getDelegate();
		((entity.externallibrary.ExternalActivityLibraryDB) this.delegate).addRepresentsD(dbElement);
	}
}
