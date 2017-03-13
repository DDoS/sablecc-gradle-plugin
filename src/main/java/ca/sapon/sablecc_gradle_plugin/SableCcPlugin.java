package ca.sapon.sablecc_gradle_plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.Copy;
import org.gradle.api.plugins.JavaPluginConvention;

public class SableCcPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().add("sableccPlugin", SableCcExtension.class);
        final SableCcExtension extension = project.getExtensions().getByType(SableCcExtension.class);

        final TaskContainer tasks = project.getTasks();

        final ProcessGrammars processGrammars = tasks.create("sableccGrammars", ProcessGrammars.class);
        project.getTasks().getByName("compileJava").dependsOn(processGrammars);

        final Copy copyResources = tasks.create("sableccResources", Copy.class);
        copyResources.setDescription("Copies SableCC resource files.");
        copyResources.setDestinationDir(project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets()
                .getByName(SourceSet.MAIN_SOURCE_SET_NAME).getOutput().getResourcesDir());
        copyResources.from(extension.outputDirectory).include("**/*.dat");
        project.getTasks().getByName("classes").dependsOn(copyResources);

        project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets()
                .getByName(SourceSet.MAIN_SOURCE_SET_NAME).getJava().srcDir(extension.outputDirectory);
    }
}
