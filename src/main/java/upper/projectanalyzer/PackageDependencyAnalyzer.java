package upper.projectanalyzer;

import org.fest.util.Maps;
import org.fest.util.Sets;

import java.io.File;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static upper.projectanalyzer.JavaFileAnalyzer.javaFileFilter;

public class PackageDependencyAnalyzer {

    private Map<PackageIdentifier, PackageIdentifier> dependencies;
    private Set<PackageIdentifier> srcPackages;
    private final File srcFolder;

    public PackageDependencyAnalyzer(File srcFolder) {
        validateSrcFolder(srcFolder);
        this.srcFolder = srcFolder;
    }

    private void validateSrcFolder(File srcFolder) {
        if (!srcFolder.isDirectory())
            throw new RuntimeException(format("%s is not a path to a directory", srcFolder.getAbsolutePath()));
        if (!srcFolder.getName().equals("java"))
            throw new RuntimeException(format("%s source directory should be the 'java' directory", srcFolder.getAbsolutePath()));
    }

    public Set<PackageIdentifier> getSrcPackages() {
        if (srcPackages == null || srcPackages.isEmpty())
            srcPackages = findAllSrcPackages();
        return srcPackages;
    }

    private Set<PackageIdentifier> findAllSrcPackages() {
        Set<PackageIdentifier> packages = Sets.newHashSet();
        addAllPakcagesWithinFolder(packages, srcFolder);
        return packages;
    }

    private void addAllPakcagesWithinFolder(Set<PackageIdentifier> curPackages, File folder) {
        if (doesContainJavaFiles(folder))
            curPackages.add(new PackageIdentifier(folder.getAbsolutePath()));
        stream(folder.listFiles(path -> path.isDirectory()))
                .forEach(subfolder -> addAllPakcagesWithinFolder(curPackages, subfolder));
    }

    private static boolean doesContainJavaFiles(File folder) {
        return folder.list(javaFileFilter).length > 0;
    }

    public Map<PackageIdentifier, PackageIdentifier> getDependencies() {
        if (dependencies == null) {
            dependencies = analyzeAllDependencies();
        }
        return dependencies;
    }

    private Map<PackageIdentifier, PackageIdentifier> analyzeAllDependencies() {
        Map<PackageIdentifier, PackageIdentifier> allDependencies = Maps.newHashMap();
        getSrcPackages().forEach(packageWithFiles -> addAllDependenciesWithinFolder(packageWithFiles, allDependencies));
        return allDependencies;
    }

    private void addAllDependenciesWithinFolder(PackageIdentifier packageWithFiles, Map<PackageIdentifier, PackageIdentifier> allDependencies) {
        File[] javaFiles = packageWithFiles.getContainedJavaFiles();
        stream(javaFiles).forEach(JavaFileAnalyzer::getAllPackagesImported);
    }
}