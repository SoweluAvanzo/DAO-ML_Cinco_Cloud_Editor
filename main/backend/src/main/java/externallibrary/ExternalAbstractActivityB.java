package externallibrary;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public interface ExternalAbstractActivityB extends externallibrary.ExternalAbstractActivityC {
	
	// Mandatory
	public PanacheEntity getDelegate();
	public void delete();
	
	// EAttributes
	public String getOfB();
	public void setOfB(String e);
}
