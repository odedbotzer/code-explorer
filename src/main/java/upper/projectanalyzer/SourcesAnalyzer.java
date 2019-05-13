package upper.projectanalyzer;

//import org.fest.util.Sets;

import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static upper.projectanalyzer.JavaFileAnalyzer.doesFolderContainJavaFiles;
import static upper.projectanalyzer.JavaFileAnalyzer.getAllImportedClassesRelativePath;

public class SourcesAnalyzer {

    private Map<JavaFileIdentifier, Set<JavaFileIdentifier>> fileDependencies;
    private Map<PackageIdentifier, Set<PackageIdentifier>> packageDependencies;
    private Set<PackageIdentifier> srcPackages;
    private final File srcFolder;

    public SourcesAnalyzer(File srcFolder) {
        validateSrcFolder(srcFolder);
        this.srcFolder = srcFolder;
    }

    public Set<PackageIdentifier> getSrcPackages() {
        if (srcPackages == null || srcPackages.isEmpty())
            srcPackages = findAllSrcPackages();
        return srcPackages;
    }

    public Map<JavaFileIdentifier, Set<JavaFileIdentifier>> getFileDependencies() {
        if (fileDependencies == null)
            fileDependencies = analyzeAllFileDependencies();
        return fileDependencies;
    }

    public Set<JavaFileIdentifier> getReverseFileDependencies(JavaFileIdentifier toFile) {
        return getFileDependencies().entrySet().stream()
                .filter(entry -> entry.getValue().contains(toFile))
                .map(entry -> entry.getKey())
                .collect(toSet());
    }

    public Map<PackageIdentifier, Set<PackageIdentifier>> getPackageDependencies() {
        if (packageDependencies == null)
            packageDependencies = analyzeAllPackageDependencies();
        return packageDependencies;
    }

    private void validateSrcFolder(File srcFolder) {
        if (!srcFolder.isDirectory())
            throw new RuntimeException(format("%s is not a path to a directory", srcFolder.getAbsolutePath()));
        if (!srcFolder.getName().equals("java"))
            throw new RuntimeException(format("%s source directory should be the 'java' directory", srcFolder.getAbsolutePath()));
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

    private Map<PackageIdentifier, Set<PackageIdentifier>> analyzeAllPackageDependencies() {
        Map<PackageIdentifier, List<JavaFileIdentifier>> packageToContainedFileMap = getFileDependencies().keySet().stream()
                .collect(groupingBy(file -> file.getPackage()));
        return packageToContainedFileMap.entrySet().stream()
                .collect(toMap(map -> map.getKey(), map -> map.getValue().stream()
                        .map(file -> getFileDependencies().get(file))
                        .reduce(Sets::union)
                        .orElse(Sets.newHashSet()) //dependee file set
                        .stream()
                        .map(file -> file.getPackage())
                        .filter(dependeePackage -> !dependeePackage.equals(map.getKey())) //package does not depend upon itself
                        .collect(toSet()) //dependee package set
                )).entrySet().stream()
                .filter(map -> !map.getValue().isEmpty())
                .collect(toMap(map -> map.getKey(), map -> map.getValue()));
    }

    private Map<JavaFileIdentifier, Set<JavaFileIdentifier>> analyzeAllFileDependencies() {
        return getSrcPackages().stream()
                .flatMap(packageWithFiles -> packageWithFiles.getContainedJavaFiles().stream())
                .collect(toMap(javaFile -> javaFile, this::getAllImportedPackages))
                .entrySet().stream()
                .filter(map -> !map.getValue().isEmpty())
                .collect(toMap(map -> map.getKey(), map -> map.getValue()));
    }

    private Set<JavaFileIdentifier> getAllImportedPackages(JavaFileIdentifier javaFile) {
        return getAllImportedClassesRelativePath(javaFile).stream()
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