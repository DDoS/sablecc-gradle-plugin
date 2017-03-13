package ca.sapon.sablecc_gradle_plugin;

import java.io.File;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;

import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import org.codehaus.plexus.util.FileUtils;

import org.sablecc.sablecc.SableCC;

public class ProcessGrammars extends DefaultTask {
    private SableCcExtension extension = null;

    private SableCcExtension extension() {
        if (extension == null) {
            extension = getProject().getExtensions().getByType(SableCcExtension.class);
        }
        return extension;
    }

    @InputDirectory
    public File getGrammarDirectory() {
        return new File(extension().sourceDirectory);
    }

    @OutputDirectory
    public File getParserOutput() {
        return new File(extension().outputDirectory);
    }

    @TaskAction
    public void task() throws Exception {
        final SableCcExtension extension = extension();

        if (!FileUtils.fileExists(extension.outputDirectory)) {
            FileUtils.mkdir(extension.outputDirectory);
        }

        final Set<File> staleGrammars = computeStaleGrammars();
        final File outputDirFile = new File(extension.outputDirectory);
        final File timestampDirFile = new File(extension.timestampDirectory);
        for (File grammar : staleGrammars) {
            SableCC.processGrammar(grammar, outputDirFile);
            FileUtils.copyFileToDirectory(grammar, timestampDirFile);
        }
    }

    private Set<File> computeStaleGrammars() {
        final SuffixMapping mapping = new SuffixMapping(".sablecc", ".sablecc");
        final SourceInclusionScanner scanner = new StaleSourceScanner(extension.staleMillis);
        scanner.addSourceMapping(mapping);
        final File outDir = new File(extension.timestampDirectory);
        final Set<File> staleSources = new HashSet<>();
        final File sourceDir = new File(extension.sourceDirectory);
        try {
            staleSources.addAll(scanner.getIncludedSources(sourceDir, outDir));
        } catch (InclusionScanException e) {
            throw new RuntimeException("Error scanning source root: \'" + sourceDir + "\' for stale grammars to reprocess.", e);
        }
        return staleSources;
    }
}
