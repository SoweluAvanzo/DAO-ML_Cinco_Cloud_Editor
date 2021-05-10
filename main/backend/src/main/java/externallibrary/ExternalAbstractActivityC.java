package externallibrary;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public interface ExternalAbstractActivityC extends externallibrary.ExternalActivityD {
	
	// Mandatory
	public PanacheEntity getDelegate();
	public void delete();
	
	// EAttributes
	public String getOfC();
	public void setOfC(String e);
}
