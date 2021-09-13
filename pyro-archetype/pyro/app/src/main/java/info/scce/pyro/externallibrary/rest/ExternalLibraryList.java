package info.scce.pyro.externallibrary.rest;

/**
 * Author zweihoff
 */

@com.fasterxml.jackson.annotation.JsonTypeName("info.scce.pyro.externallibrary.rest.ExternalLibraryList")
@com.fasterxml.jackson.annotation.JsonIdentityInfo(generator = com.voodoodyne.jackson.jsog.JSOGGenerator.class)
public class ExternalLibraryList
{
	private java.util.List<info.scce.pyro.externallibrary.rest.ExternalLibrary> list;
	
	@com.fasterxml.jackson.annotation.JsonProperty("list")
	public java.util.List<info.scce.pyro.externallibrary.rest.ExternalLibrary> getlist() {
		return this.list;
	}
	
	@com.fasterxml.jackson.annotation.JsonProperty("list")
	public void setlist(final java.util.List<info.scce.pyro.externallibrary.rest.ExternalLibrary> list) {
		this.list = list;
	}
	
	public static ExternalLibraryList fromEntity(final java.util.Collection<entity.externallibrary.ExternalLibraryDB> entity, info.scce.pyro.rest.ObjectCache objectCache) {
		ExternalLibraryList result = new ExternalLibraryList();
		result.setlist(
			entity.stream().map((n)->info.scce.pyro.externallibrary.rest.ExternalLibrary.fromEntity(n,objectCache)).collect(java.util.stream.Collectors.toList())
		);
		return result;
	}

}
