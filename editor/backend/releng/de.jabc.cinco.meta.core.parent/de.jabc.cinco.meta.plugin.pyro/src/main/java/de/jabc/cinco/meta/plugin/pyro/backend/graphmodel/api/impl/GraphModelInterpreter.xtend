package de.jabc.cinco.meta.plugin.pyro.backend.graphmodel.api.impl

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound
import mgl.GraphModel

class GraphModelInterpreter extends Generatable{
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def filename(GraphModel g)
	'''«g.interpreter».java'''
	
	def content(GraphModel g) {
		'''
			package «g.interpreterPackage»;
			
			/**
			 * Author zweihoff
			 */
			public abstract class «g.interpreter» extends info.scce.pyro.api.PyroControl {
			
				private java.util.Map<String,Object> context;
				
				protected void write(graphmodel.IdentifiableElement e,Object obj) {
						context.put(e.getId(),obj);
				}
				protected Object read(graphmodel.IdentifiableElement e) {
					return context.get(e.getId());
				}
				protected Byte readByte(graphmodel.IdentifiableElement e) {
					return (Byte) context.get(e.getId());
				}
				protected Short readShort(graphmodel.IdentifiableElement e) {
					return (Short) context.get(e.getId());
				}
				protected Integer readInteger(graphmodel.IdentifiableElement e) {
					return (Integer) context.get(e.getId());
				}
				protected Long readLong(graphmodel.IdentifiableElement e) {
					return (Long) context.get(e.getId());
				}
				protected Float readFloat(graphmodel.IdentifiableElement e) {
					return (Float) context.get(e.getId());
				}
				protected Double readDouble(graphmodel.IdentifiableElement e) {
					return (Double) context.get(e.getId());
				}
				protected Character readCharacter(graphmodel.IdentifiableElement e) {
					return (Character) context.get(e.getId());
				}
				protected Boolean readBoolean(graphmodel.IdentifiableElement e) {
					return (Boolean) context.get(e.getId());
				}
				protected boolean isWritten(graphmodel.IdentifiableElement e) { return context.containsKey(e.getId()); }
			    
			    public final void runInterpreter(«g.apiFQN» g) {
			    	context = new java.util.HashMap<>();
			    	java.util.List<graphmodel.ModelElement> waitingList = getInitialElements(g);
			    	while(!waitingList.isEmpty()) {
						graphmodel.ModelElement current = waitingList.get(0);
						«FOR n:g.nodesTopologically + g.edgesTopologically»
							if(current instanceof «n.apiFQN») {
								«n.apiFQN» e = («n.apiFQN») current;
								if(canExecute«n.name.fuEscapeJava»(e,g)) {
									execute«n.name.fuEscapeJava»(e,g);
									waitingList.addAll(nextElementsAfter«n.name.fuEscapeJava»(e,g));	    				
								}
							}
						«ENDFOR»
						waitingList.remove(0);
			    	}
			    }
			    
			    public abstract <T extends graphmodel.ModelElement> java.util.List<T> getInitialElements(«g.apiFQN» g);
			    
				«FOR n:g.elements»
					
					public void execute«n.name.fuEscapeJava»(«n.apiFQN» element,«g.apiFQN» graph) {}
					
					public boolean canExecute«n.name.fuEscapeJava»(«n.apiFQN» e,«g.apiFQN» g) {
						return «IF n.isExtending»canExecute«n.extendingModelType.name.fuEscapeJava»(e,g)«ELSE»true«ENDIF»;
					}
					
					public <T extends graphmodel.ModelElement> java.util.List<T> nextElementsAfter«n.name.fuEscapeJava»(«n.apiFQN» element,«g.apiFQN» graph) {
						return java.util.Collections.emptyList();
					}
				«ENDFOR»
			}
		'''
	}
	
}