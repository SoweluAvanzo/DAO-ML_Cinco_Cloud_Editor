package info.scce.pyro.core.export;

import entity.flowgraph.FlowGraphDB;
import org.eclipse.xtend2.lib.StringConcatenation;

@SuppressWarnings("all")
public class FlowGraphExporter {
  public FlowGraphExporter() {
  }
  
  public String getContent(final FlowGraphDB graph) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("No Content in here");
    _builder.newLine();
    return _builder.toString();
  }
}
