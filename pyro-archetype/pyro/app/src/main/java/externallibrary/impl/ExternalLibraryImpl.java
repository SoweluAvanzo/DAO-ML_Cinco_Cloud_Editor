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
	
	public externallibrary.ExternalLibrary eClass() {
		return this;
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
	
	public String getName() {
		return "externalLibrary";
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
	public java.util.List<externallibrary.ExternalActivity> getExternalActivity() {
		java.util.Collection<PanacheEntity> entityList = this.delegate.getExternalActivity();
		java.util.List<externallibrary.ExternalActivity> apiList = entityList.stream().map(n->
			new externallibrary.impl.ExternalActivityImpl(
				(entity.externallibrary.ExternalActivityDB) n
			)
		).collect(java.util.stream.Collectors.toList());
		return apiList;
	}
	
	@Override
	public void setExternalActivity(java.util.Collection<externallibrary.ExternalActivity> e) {
		java.util.Collection<PanacheEntity> entityList = e.stream().map(n->
			n.getDelegate()
		).collect(java.util.stream.Collectors.toList());
		this.delegate.setExternalActivity(entityList);
		this.delegate.persist();
	}
	
	@Override
	public boolean removeExternalActivity(externallibrary.ExternalActivity apiElement, boolean delete) {
		PanacheEntity dbElement = apiElement.getDelegate();
		return ((entity.externallibrary.ExternalLibraryDB) this.delegate).removeExternalActivity(dbElement, delete);
	}
	
	@Override
	public boolean removeExternalActivity(externallibrary.ExternalActivity apiElement) {
		return removeExternalActivity(apiElement, true);
	}
	
	@Override
	public void clearExternalActivity() {
		((entity.externallibrary.ExternalLibraryDB) this.delegate).clearExternalActivity();
	}
	
	@Override
	public void clearExternalActivity(boolean delete) {
		((entity.externallibrary.ExternalLibraryDB) this.delegate).clearExternalActivity(delete);
	}
	
	@Override
	public void addAllExternalActivity(java.util.Collection<externallibrary.ExternalActivity> apiElements) {
		for(externallibrary.ExternalActivity apiElement : apiElements) {
			addExternalActivity(apiElement);
		}
	}
	
	@Override
	public void addExternalActivity(externallibrary.ExternalActivity apiElement) {
		PanacheEntity dbElement = apiElement.getDelegate();
		((entity.externallibrary.ExternalLibraryDB) this.delegate).addExternalActivity(dbElement);
	}
}
