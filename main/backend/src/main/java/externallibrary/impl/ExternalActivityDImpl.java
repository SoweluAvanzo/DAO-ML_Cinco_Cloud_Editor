package externallibrary.impl;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public class ExternalActivityDImpl implements externallibrary.ExternalActivityD {
	
	private final entity.externallibrary.ExternalActivityDDB delegate;
	
	public ExternalActivityDImpl(
		entity.externallibrary.ExternalActivityDDB delegate
	) {
		this.delegate = delegate;
	}
	
	public ExternalActivityDImpl(
	) {
		this.delegate = new entity.externallibrary.ExternalActivityDDB();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof externallibrary.ExternalActivityD
			&& ((externallibrary.ExternalActivityD) obj).getId().equals(getId());
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
	public entity.externallibrary.ExternalActivityDDB getDelegate() {
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
	public long getOfD() {
		return this.delegate.ofd;
	}
	
	@Override
	public void setOfD(long e) {
		this.delegate.ofd = e;
		this.delegate.persist();
	}
	
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
	public externallibrary.ExternalActivityD getReferencedOfD() {
		externallibrary.ExternalActivityD result = null;
		PanacheEntity dbEntity = this.delegate.getReferencedOfD();
		if(dbEntity instanceof entity.externallibrary.ExternalActivityDDB) {
			result = new externallibrary.impl.ExternalActivityDImpl((entity.externallibrary.ExternalActivityDDB) dbEntity);
		} else if(dbEntity instanceof entity.externallibrary.ExternalActivityADB) {
			result = new externallibrary.impl.ExternalActivityAImpl((entity.externallibrary.ExternalActivityADB) dbEntity);
		}
		return result;
	}
	
	@Override
	public void setReferencedOfD(externallibrary.ExternalActivityD e) {
		PanacheEntity entity = e.getDelegate();
		this.delegate.setReferencedOfD(entity, true);
		this.delegate.persist();
	}
	
	@Override
	public java.util.List<externallibrary.ExternalActivityD> getRecerencingAbstractList() {
		java.util.Collection<PanacheEntity> entityList = this.delegate.getRecerencingAbstractList();
		java.util.List<externallibrary.ExternalActivityD> apiList = entityList.stream().map(n->
			new externallibrary.impl.ExternalActivityDImpl(
				(entity.externallibrary.ExternalActivityDDB) n
			)
		).collect(java.util.stream.Collectors.toList());
		return apiList;
	}
	
	@Override
	public void setRecerencingAbstractList(java.util.Collection<externallibrary.ExternalActivityD> e) {
		java.util.Collection<PanacheEntity> entityList = e.stream().map(n->
			n.getDelegate()
		).collect(java.util.stream.Collectors.toList());
		this.delegate.setRecerencingAbstractList(entityList);
		this.delegate.persist();
	}
	
	@Override
	public boolean removeRecerencingAbstractList(externallibrary.ExternalActivityD apiElement, boolean delete) {
		PanacheEntity dbElement = apiElement.getDelegate();
		return ((entity.externallibrary.ExternalActivityDDB) this.delegate).removeRecerencingAbstractList(dbElement, delete);
	}
	
	@Override
	public boolean removeRecerencingAbstractList(externallibrary.ExternalActivityD apiElement) {
		return removeRecerencingAbstractList(apiElement, true);
	}
	
	@Override
	public void clearRecerencingAbstractList() {
		((entity.externallibrary.ExternalActivityDDB) this.delegate).clearRecerencingAbstractList();
	}
	
	@Override
	public void clearRecerencingAbstractList(boolean delete) {
		((entity.externallibrary.ExternalActivityDDB) this.delegate).clearRecerencingAbstractList(delete);
	}
	
	@Override
	public void addAllRecerencingAbstractList(java.util.Collection<externallibrary.ExternalActivityD> apiElements) {
		for(externallibrary.ExternalActivityD apiElement : apiElements) {
			addRecerencingAbstractList(apiElement);
		}
	}
	
	@Override
	public void addRecerencingAbstractList(externallibrary.ExternalActivityD apiElement) {
		PanacheEntity dbElement = apiElement.getDelegate();
		((entity.externallibrary.ExternalActivityDDB) this.delegate).addRecerencingAbstractList(dbElement);
	}
	
	@Override
	public java.util.List<externallibrary.ExternalAbstractActivityB> getReferencingList() {
		java.util.Collection<PanacheEntity> entityList = this.delegate.getReferencingList();
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
	public void setReferencingList(java.util.Collection<externallibrary.ExternalAbstractActivityB> e) {
		java.util.Collection<PanacheEntity> entityList = e.stream().map(n->
			n.getDelegate()
		).collect(java.util.stream.Collectors.toList());
		this.delegate.setReferencingList(entityList);
		this.delegate.persist();
	}
	
	@Override
	public boolean removeReferencingList(externallibrary.ExternalAbstractActivityB apiElement, boolean delete) {
		PanacheEntity dbElement = apiElement.getDelegate();
		return ((entity.externallibrary.ExternalActivityDDB) this.delegate).removeReferencingList(dbElement, delete);
	}
	
	@Override
	public boolean removeReferencingList(externallibrary.ExternalAbstractActivityB apiElement) {
		return removeReferencingList(apiElement, true);
	}
	
	@Override
	public void clearReferencingList() {
		((entity.externallibrary.ExternalActivityDDB) this.delegate).clearReferencingList();
	}
	
	@Override
	public void clearReferencingList(boolean delete) {
		((entity.externallibrary.ExternalActivityDDB) this.delegate).clearReferencingList(delete);
	}
	
	@Override
	public void addAllReferencingList(java.util.Collection<externallibrary.ExternalAbstractActivityB> apiElements) {
		for(externallibrary.ExternalAbstractActivityB apiElement : apiElements) {
			addReferencingList(apiElement);
		}
	}
	
	@Override
	public void addReferencingList(externallibrary.ExternalAbstractActivityB apiElement) {
		PanacheEntity dbElement = apiElement.getDelegate();
		((entity.externallibrary.ExternalActivityDDB) this.delegate).addReferencingList(dbElement);
	}
}
