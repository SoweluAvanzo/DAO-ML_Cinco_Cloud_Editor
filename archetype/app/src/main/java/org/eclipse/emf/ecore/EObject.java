package org.eclipse.emf.ecore;

public interface EObject extends graphmodel.IdentifiableElement
{
  EObject eContainer();
  String getId();
  io.quarkus.hibernate.orm.panache.PanacheEntity getDelegate();
}