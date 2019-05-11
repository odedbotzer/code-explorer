package upper.projectanalyzer;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SourcesAnalyzerTest {

    public static final String SRC_FOLDER = "C:\\Users\\odedbotzer\\IdeaProjects\\intellij-sdk-docs\\code_samples\\gradle_plugin_demo\\src\\main\\java";
    private SourcesAnalyzer dependencyAnalyzer;
    private Set<PackageIdentifier> packages;

    @Before
    public void calcPackages() {
        dependencyAnalyzer = new SourcesAnalyzer(new File(SRC_FOLDER));
        packages = dependencyAnalyzer.getSrcPackages();
        packages.stream().forEach(p -> System.out.println(p));
    }

    @Test
    public void getPackagesNotEmptyTest() {
        assertTrue(!packages.isEmpty());
    }

    @Test
    public void getDependenciesNotNullTest() {
        Map<PackageIdentifier, PackageIdentifier> dependencies = dependencyAnalyzer.getDependencies();
        assertFalse(dependencies.isEmpty());
    }

}