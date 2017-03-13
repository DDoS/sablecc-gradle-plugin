package ca.sapon.sablecc_gradle_plugin;

import org.gradle.api.Project;

public class SableCcExtension {
    public String sourceDirectory = "src/main/sablecc";
    public String outputDirectory = Project.DEFAULT_BUILD_DIR_NAME + "/generated-sources/sablecc";
    public String timestampDirectory = Project.DEFAULT_BUILD_DIR_NAME + "/sablecc/timestamps";
    public long staleMillis = 0;
}
