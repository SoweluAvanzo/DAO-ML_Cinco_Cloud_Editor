package org.eclipse.emf.ecore;

public interface EObject extends graphmodel.IdentifiableElement // TODO: SAMI: or PyroElement?
{
  EObject eContainer();
  String getId();
  io.quarkus.hibernate.orm.panache.PanacheEntity getDelegate();
}