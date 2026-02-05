package dev.gertjanassies.filament.commands;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class VersionCommandTests {
  
  @Autowired
  private VersionCommand versionCommand;

    @Test
  void testVersionCommand() {
    var result = versionCommand.showVersion();
    assertThat(result).isEqualTo("Version: 1.0.0-test");
  }

}
