package upper.projectanalyzer;

import org.fest.util.Sets;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;
import static upper.projectanalyzer.JavaFileAnalyzer.getAllImportedClassesRelativePath;
import static upper.projectanalyzer.JavaFileIdentifier.doesFolderContainJavaFiles;

public class SourcesAnalyzer {

    private Map<JavaFileIdentifier, Set<JavaFileIdentifier>> dependencies;
    private Set<PackageIdentifier> srcPackages;
    private final File srcFolder;

    public SourcesAnalyzer(File srcFolder) {
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
        if (doesFolderContainJavaFiles(folder))
            curPackages.add(new PackageIdentifier(folder));
        subFoldersStream(folder).forEach(subfolder -> addAllPakcagesWithinFolder(curPackages, subfolder));
    }

    @NotNull
    private Stream<File> subFoldersStream(File folder) {
        return stream(folder.listFiles(path -> path.isDirectory()));
    }

    public Map<JavaFileIdentifier, Set<JavaFileIdentifier>> getDependencies() {
        if (dependencies == null) {
            dependencies = analyzeAllDependencies();
        }
        return dependencies;
    }

    private Map<JavaFileIdentifier, Set<JavaFileIdentifier>> analyzeAllDependencies() {
        return getSrcPackages().stream()
                .flatMap(packageWithFiles -> packageWithFiles.getContainedJavaFiles().stream())
                .collect(toMap(javaFile -> javaFile, this::getAllImportedPackages));
    }

    private Set<JavaFileIdentifier> getAllImportedPackages(JavaFileIdentifier javaFile) {
        Set<String> allImportedClassesRelativePath = getAllImportedClassesRelativePath(javaFile);
        return allImportedClassesRelativePath.stream()
                .map(this::relativeClassPathToJavaFile)
                .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toSet());
    }

    private Optional<JavaFileIdentifier> relativeClassPathToJavaFile(String relativeClassPath) {
        try {
            return Optional.of(new JavaFileIdentifier(this.srcFolder.getAbsolutePath() + File.separator + relativeClassPath + ".java"));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }
}