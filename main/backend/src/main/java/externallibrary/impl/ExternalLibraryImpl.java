package externallibrary.impl;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public class ExternalLibraryImpl implements externallibrary.ExternalLibrary {
	
	private final entity.externallibrary.ExternalLibraryDB delegate;
	
	public ExternalLibraryImpl(
		entity.externallibrary.ExternalLibraryDB delegate
	) {
		this.delegate = delegate;
	}
	
	public ExternalLibraryImpl(
	) {
		this.delegate = new entity.externallibrary.ExternalLibraryDB();
	}
	
	@Override
	public void setExtension(String extension) {
		this.delegate.extension = extension;
		delegate.persist();
	}
	
	@Override
	public String getExtension() {
		return this.delegate.extension;
	}
	
	@Override
	public void setFilename(String filename) {
		this.delegate.filename = filename;
		delegate.persist();
	}
	
	@Override
	public String getFilename() {
		return this.delegate.filename;
	}
	
	@Override
	public void setParent(PanacheEntity parent) {
		if(parent instanceof entity.core.PyroProjectDB)
			this.delegate.parent = (entity.core.PyroProjectDB) parent;
		else if(parent instanceof entity.core.PyroFolderDB)
			this.delegate.parent = (entity.core.PyroFolderDB) parent;
		else
			throw new IllegalArgumentException("parent is no FileContainerType!");
		delegate.persist();
	}
	
	@Override
	public PanacheEntity getParent() {
		return this.delegate.parent;
	}
	
	@Override
	public entity.core.PyroProjectDB getProject() {
		PanacheEntity parent = this.delegate.parent;
		if(parent instanceof entity.core.PyroProjectDB)
			return (entity.core.PyroProjectDB) parent;
		while(!(parent instanceof entity.core.PyroProjectDB)) {
			if(parent instanceof entity.core.PyroFolderDB)
				parent = ((entity.core.PyroFolderDB) parent).parent;
			else
				throw new IllegalArgumentException("parent is no FileContainerType!");
		}
		return (entity.core.PyroProjectDB) parent;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null
			&& obj instanceof externallibrary.ExternalLibrary
			&& ((externallibrary.ExternalLibrary) obj).getId().equals(getId());
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
	public entity.externallibrary.ExternalLibraryDB getDelegate() {
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
	
	// Contents
	@Override
	public java.util.List<externallibrary.ExternalActivityLibrary> getExternalActivityLibrary() {
		java.util.Collection<PanacheEntity> entityList = this.delegate.getExternalActivityLibrary();
		java.util.List<externallibrary.ExternalActivityLibrary> apiList = entityList.stream().map(n->
			new externallibrary.impl.ExternalActivityLibraryImpl(
				(entity.externallibrary.ExternalActivityLibraryDB) n
			)
		).collect(java.util.stream.Collectors.toList());
		return apiList;
	}
	
	@Override
	public void setExternalActivityLibrary(java.util.Collection<externallibrary.ExternalActivityLibrary> e) {
		java.util.Collection<PanacheEntity> entityList = e.stream().map(n->
			n.getDelegate()
		).collect(java.util.stream.Collectors.toList());
		this.delegate.setExternalActivityLibrary(entityList);
		this.delegate.persist();
	}
	
	@Override
	public boolean removeExternalActivityLibrary(externallibrary.ExternalActivityLibrary apiElement, boolean delete) {
		PanacheEntity dbElement = apiElement.getDelegate();
		return ((entity.externallibrary.ExternalLibraryDB) this.delegate).removeExternalActivityLibrary(dbElement, delete);
	}
	
	@Override
	public boolean removeExternalActivityLibrary(externallibrary.ExternalActivityLibrary apiElement) {
		return removeExternalActivityLibrary(apiElement, true);
	}
	
	@Override
	public void clearExternalActivityLibrary() {
		((entity.externallibrary.ExternalLibraryDB) this.delegate).clearExternalActivityLibrary();
	}
	
	@Override
	public void clearExternalActivityLibrary(boolean delete) {
		((entity.externallibrary.ExternalLibraryDB) this.delegate).clearExternalActivityLibrary(delete);
	}
	
	@Override
	public void addAllExternalActivityLibrary(java.util.Collection<externallibrary.ExternalActivityLibrary> apiElements) {
		for(externallibrary.ExternalActivityLibrary apiElement : apiElements) {
			addExternalActivityLibrary(apiElement);
		}
	}
	
	@Override
	public void addExternalActivityLibrary(externallibrary.ExternalActivityLibrary apiElement) {
		PanacheEntity dbElement = apiElement.getDelegate();
		((entity.externallibrary.ExternalLibraryDB) this.delegate).addExternalActivityLibrary(dbElement);
	}
	
	@Override
	public java.util.List<externallibrary.ExternalActivityA> getExternalActivityA() {
		java.util.Collection<PanacheEntity> entityList = this.delegate.getExternalActivityA();
		java.util.List<externallibrary.ExternalActivityA> apiList = entityList.stream().map(n->
			new externallibrary.impl.ExternalActivityAImpl(
				(entity.externallibrary.ExternalActivityADB) n
			)
		).collect(java.util.stream.Collectors.toList());
		return apiList;
	}
	
	@Override
	public void setExternalActivityA(java.util.Collection<externallibrary.ExternalActivityA> e) {
		java.util.Collection<PanacheEntity> entityList = e.stream().map(n->
			n.getDelegate()
		).collect(java.util.stream.Collectors.toList());
		this.delegate.setExternalActivityA(entityList);
		this.delegate.persist();
	}
	
	@Override
	public boolean removeExternalActivityA(externallibrary.ExternalActivityA apiElement, boolean delete) {
		PanacheEntity dbElement = apiElement.getDelegate();
		return ((entity.externallibrary.ExternalLibraryDB) this.delegate).removeExternalActivityA(dbElement, delete);
	}
	
	@Override
	public boolean removeExternalActivityA(externallibrary.ExternalActivityA apiElement) {
		return removeExternalActivityA(apiElement, true);
	}
	
	@Override
	public void clearExternalActivityA() {
		((entity.externallibrary.ExternalLibraryDB) this.delegate).clearExternalActivityA();
	}
	
	@Override
	public void clearExternalActivityA(boolean delete) {
		((entity.externallibrary.ExternalLibraryDB) this.delegate).clearExternalActivityA(delete);
	}
	
	@Override
	public void addAllExternalActivityA(java.util.Collection<externallibrary.ExternalActivityA> apiElements) {
		for(externallibrary.ExternalActivityA apiElement : apiElements) {
			addExternalActivityA(apiElement);
		}
	}
	
	@Override
	public void addExternalActivityA(externallibrary.ExternalActivityA apiElement) {
		PanacheEntity dbElement = apiElement.getDelegate();
		((entity.externallibrary.ExternalLibraryDB) this.delegate).addExternalActivityA(dbElement);
	}
	
	@Override
	public java.util.List<externallibrary.ExternalAbstractActivityB> getExternalAbstractActivityB() {
		java.util.Collection<PanacheEntity> entityList = this.delegate.getExternalAbstractActivityB();
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
	public void setExternalAbstractActivityB(java.util.Collection<externallibrary.ExternalAbstractActivityB> e) {
		java.util.Collection<PanacheEntity> entityList = e.stream().map(n->
			n.getDelegate()
		).collect(java.util.stream.Collectors.toList());
		this.delegate.setExternalAbstractActivityB(entityList);
		this.delegate.persist();
	}
	
	@Override
	public boolean removeExternalAbstractActivityB(externallibrary.ExternalAbstractActivityB apiElement, boolean delete) {
		PanacheEntity dbElement = apiElement.getDelegate();
		return ((entity.externallibrary.ExternalLibraryDB) this.delegate).removeExternalAbstractActivityB(dbElement, delete);
	}
	
	@Override
	public boolean removeExternalAbstractActivityB(externallibrary.ExternalAbstractActivityB apiElement) {
		return removeExternalAbstractActivityB(apiElement, true);
	}
	
	@Override
	public void clearExternalAbstractActivityB() {
		((entity.externallibrary.ExternalLibraryDB) this.delegate).clearExternalAbstractActivityB();
	}
	
	@Override
	public void clearExternalAbstractActivityB(boolean delete) {
		((entity.externallibrary.ExternalLibraryDB) this.delegate).clearExternalAbstractActivityB(delete);
	}
	
	@Override
	public void addAllExternalAbstractActivityB(java.util.Collection<externallibrary.ExternalAbstractActivityB> apiElements) {
		for(externallibrary.ExternalAbstractActivityB apiElement : apiElements) {
			addExternalAbstractActivityB(apiElement);
		}
	}
	
	@Override
	public void addExternalAbstractActivityB(externallibrary.ExternalAbstractActivityB apiElement) {
		PanacheEntity dbElement = apiElement.getDelegate();
		((entity.externallibrary.ExternalLibraryDB) this.delegate).addExternalAbstractActivityB(dbElement);
	}
	
	@Override
	public java.util.List<externallibrary.ExternalAbstractActivityC> getExternalAbstractActivityC() {
		java.util.Collection<PanacheEntity> entityList = this.delegate.getExternalAbstractActivityC();
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
	public void setExternalAbstractActivityC(java.util.Collection<externallibrary.ExternalAbstractActivityC> e) {
		java.util.Collection<PanacheEntity> entityList = e.stream().map(n->
			n.getDelegate()
		).collect(java.util.stream.Collectors.toList());
		this.delegate.setExternalAbstractActivityC(entityList);
		this.delegate.persist();
	}
	
	@Override
	public boolean removeExternalAbstractActivityC(externallibrary.ExternalAbstractActivityC apiElement, boolean delete) {
		PanacheEntity dbElement = apiElement.getDelegate();
		return ((entity.externallibrary.ExternalLibraryDB) this.delegate).removeExternalAbstractActivityC(dbElement, delete);
	}
	
	@Override
	public boolean removeExternalAbstractActivityC(externallibrary.ExternalAbstractActivityC apiElement) {
		return removeExternalAbstractActivityC(apiElement, true);
	}
	
	@Override
	public void clearExternalAbstractActivityC() {
		((entity.externallibrary.ExternalLibraryDB) this.delegate).clearExternalAbstractActivityC();
	}
	
	@Override
	public void clearExternalAbstractActivityC(boolean delete) {
		((entity.externallibrary.ExternalLibraryDB) this.delegate).clearExternalAbstractActivityC(delete);
	}
	
	@Override
	public void addAllExternalAbstractActivityC(java.util.Collection<externallibrary.ExternalAbstractActivityC> apiElements) {
		for(externallibrary.ExternalAbstractActivityC apiElement : apiElements) {
			addExternalAbstractActivityC(apiElement);
		}
	}
	
	@Override
	public void addExternalAbstractActivityC(externallibrary.ExternalAbstractActivityC apiElement) {
		PanacheEntity dbElement = apiElement.getDelegate();
		((entity.externallibrary.ExternalLibraryDB) this.delegate).addExternalAbstractActivityC(dbElement);
	}
	
	@Override
	public java.util.List<externallibrary.ExternalActivityD> getExternalActivityD() {
		java.util.Collection<PanacheEntity> entityList = this.delegate.getExternalActivityD();
		java.util.List<externallibrary.ExternalActivityD> apiList = entityList.stream().map(n->
			new externallibrary.impl.ExternalActivityDImpl(
				(entity.externallibrary.ExternalActivityDDB) n
			)
		).collect(java.util.stream.Collectors.toList());
		return apiList;
	}
	
	@Override
	public void setExternalActivityD(java.util.Collection<externallibrary.ExternalActivityD> e) {
		java.util.Collection<PanacheEntity> entityList = e.stream().map(n->
			n.getDelegate()
		).collect(java.util.stream.Collectors.toList());
		this.delegate.setExternalActivityD(entityList);
		this.delegate.persist();
	}
	
	@Override
	public boolean removeExternalActivityD(externallibrary.ExternalActivityD apiElement, boolean delete) {
		PanacheEntity dbElement = apiElement.getDelegate();
		return ((entity.externallibrary.ExternalLibraryDB) this.delegate).removeExternalActivityD(dbElement, delete);
	}
	
	@Override
	public boolean removeExternalActivityD(externallibrary.ExternalActivityD apiElement) {
		return removeExternalActivityD(apiElement, true);
	}
	
	@Override
	public void clearExternalActivityD() {
		((entity.externallibrary.ExternalLibraryDB) this.delegate).clearExternalActivityD();
	}
	
	@Override
	public void clearExternalActivityD(boolean delete) {
		((entity.externallibrary.ExternalLibraryDB) this.delegate).clearExternalActivityD(delete);
	}
	
	@Override
	public void addAllExternalActivityD(java.util.Collection<externallibrary.ExternalActivityD> apiElements) {
		for(externallibrary.ExternalActivityD apiElement : apiElements) {
			addExternalActivityD(apiElement);
		}
	}
	
	@Override
	public void addExternalActivityD(externallibrary.ExternalActivityD apiElement) {
		PanacheEntity dbElement = apiElement.getDelegate();
		((entity.externallibrary.ExternalLibraryDB) this.delegate).addExternalActivityD(dbElement);
	}
}
