
package dev.gertjanassies.filament.commands;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;


@ShellComponent
public class VersionCommand {
  
  @Value("${application.version}")
  private String version;

  @ShellMethod(key="version", value="Displays the application version")
  public String showVersion() {
    var result = "Version: " + version;
    return result;
  }

}
