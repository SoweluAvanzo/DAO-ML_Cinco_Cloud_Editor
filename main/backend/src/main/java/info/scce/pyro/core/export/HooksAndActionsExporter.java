package info.scce.pyro.core.export;

import entity.hooksandactions.HooksAndActionsDB;
import org.eclipse.xtend2.lib.StringConcatenation;

@SuppressWarnings("all")
public class HooksAndActionsExporter {
  public HooksAndActionsExporter() {
  }
  
  public String getContent(final HooksAndActionsDB graph) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("No Content in here");
    _builder.newLine();
    return _builder.toString();
  }
}
