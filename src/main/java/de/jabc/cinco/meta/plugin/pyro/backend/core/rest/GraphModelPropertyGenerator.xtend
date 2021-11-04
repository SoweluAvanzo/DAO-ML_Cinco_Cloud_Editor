package de.jabc.cinco.meta.plugin.pyro.backend.core.rest

import de.jabc.cinco.meta.plugin.pyro.util.Generatable
import de.jabc.cinco.meta.plugin.pyro.util.GeneratorCompound

class GraphModelPropertyGenerator extends Generatable {
	
	new(GeneratorCompound gc) {
		super(gc)
	}
	
	def fileName()
	'''GraphModelProperty.java'''
	
	def content()
	'''
		package info.scce.pyro.core.rest.types;
		
		/**
		 * Author zweihoff
		 */
		
		public class GraphModelProperty extends info.scce.pyro.rest.RESTBaseImpl
		{
		
		    private String router;
		
		    @com.fasterxml.jackson.annotation.JsonProperty("router")
		    public String getrouter() {
		        return this.router;
		    }
		
		    @com.fasterxml.jackson.annotation.JsonProperty("router")
		    public void setrouter(final String router) {
		        this.router = router;
		    }
		
		    private String connector;
		
		    @com.fasterxml.jackson.annotation.JsonProperty("connector")
		    public String getconnector() {
		        return this.connector;
		    }
		
		    @com.fasterxml.jackson.annotation.JsonProperty("connector")
		    public void setconnector(final String connector) {
		        this.connector = connector;
		    }
		
		    private long width;
		
		    @com.fasterxml.jackson.annotation.JsonProperty("width")
		    public long getwidth() {
		        return this.width;
		    }
		
		    @com.fasterxml.jackson.annotation.JsonProperty("width")
		    public void setwidth(final long width) {
		        this.width = width;
		    }
		
		    private long height;
		
		    @com.fasterxml.jackson.annotation.JsonProperty("height")
		    public long getheight() {
		        return this.height;
		    }
		
		    @com.fasterxml.jackson.annotation.JsonProperty("height")
		    public void setheight(final long height) {
		        this.height = height;
		    }
		
		    private double scale;
		
		    @com.fasterxml.jackson.annotation.JsonProperty("scale")
		    public double getscale() {
		        return this.scale;
		    }
		
		    @com.fasterxml.jackson.annotation.JsonProperty("scale")
		    public void setscale(final double scale) {
		        this.scale = scale;
		    }
		
		    private String messageType;
		
		    @com.fasterxml.jackson.annotation.JsonProperty("messageType")
		    public String getmessageType() {
		        return this.messageType;
		    }
		
		    @com.fasterxml.jackson.annotation.JsonProperty("messageType")
		    public void setmessageType(final String messageType) {
		        this.messageType = messageType;
		    }
			«FOR g:gc.graphMopdels»
				
				public static GraphModelProperty fromEntity(final «g.entityFQN» entity) {
				
				    final GraphModelProperty result;
				    result = new GraphModelProperty();
				    result.setId(entity.id);
				
				    result.setconnector(entity.connector);
				    result.setrouter(entity.router);
				    result.setwidth(entity.width);
				    result.setheight(entity.height);
				    result.setscale(entity.scale);
				    result.setmessageType("graphmodelProperty");
				
				
				    return result;
				}
		    «ENDFOR»
		}
	'''
	
}
