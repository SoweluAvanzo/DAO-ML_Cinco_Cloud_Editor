package info.scce.pyro.core.export;

import entity.hierarchy.HierarchyDB;
import org.eclipse.xtend2.lib.StringConcatenation;

@SuppressWarnings("all")
public class HierarchyExporter {
  public HierarchyExporter() {
  }
  
  public String getContent(final HierarchyDB graph) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("No Content in here");
    _builder.newLine();
    return _builder.toString();
  }
}
