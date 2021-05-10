package info.scce.pyro.core.export;

import entity.primerefs.PrimeRefsDB;
import org.eclipse.xtend2.lib.StringConcatenation;

@SuppressWarnings("all")
public class PrimeRefsExporter {
  public PrimeRefsExporter() {
  }
  
  public String getContent(final PrimeRefsDB graph) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("No Content in here");
    _builder.newLine();
    return _builder.toString();
  }
}
