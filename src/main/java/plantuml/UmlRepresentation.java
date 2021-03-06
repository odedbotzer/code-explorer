package plantuml;

import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;
import upper.projectanalyzer.JavaFileIdentifier;
import upper.projectanalyzer.PackageIdentifier;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public class UmlRepresentation {
    private final Optional<JavaFileIdentifier> focusedFile;
    private final Map<PackageIdentifier, Set<JavaFileIdentifier>> containmentMap;
    private final Map<PackageIdentifier, Set<PackageIdentifier>> packageDependencies;
    private final Map<JavaFileIdentifier, Set<JavaFileIdentifier>> fileDependencies;

    public UmlRepresentation(Map<PackageIdentifier, Set<PackageIdentifier>> packageDependencies,
                             Map<JavaFileIdentifier, Set<JavaFileIdentifier>> fileDependencies,
                             Set<JavaFileIdentifier> additionalFiles,
                             Optional<JavaFileIdentifier> focusedFile) {

        focusedFile.ifPresent(file -> additionalFiles.add(file));
        Set<JavaFileIdentifier> allFiles = getAllFiles(packageDependencies, fileDependencies, additionalFiles);
        this.containmentMap = allFiles.stream().collect(toMap(JavaFileIdentifier::getPackage, Sets::newHashSet, Sets::union));
        this.fileDependencies = fileDependencies;
        this.packageDependencies = packageDependencies;
        this.focusedFile = focusedFile;
    }

    public String writeUml() {
        String uml = getUmlOpening();
        uml += "\n";
        uml += getContainmentUml();
        uml += "\n";
        uml += getContainmentUml();
        uml += "\n";
        uml += getFocusedFileUml();
        uml += "\n";
        uml += getUmlClosing();
        return uml;
    }

    private String getFocusedFileUml() {
        return this.focusedFile.map(file -> {
            return "d";
        }).orElse("");
    }

    @NotNull
    private String getUmlClosing() {
        return "@enduml";
    }

    @NotNull
    private String getUmlOpening() {
        return "@startuml\n";
    }

    @NotNull
    private String getContainmentUml() {
        return "";
    }

    private Set<JavaFileIdentifier> getAllFiles(Map<PackageIdentifier, Set<PackageIdentifier>> packageDependencies,
                                                Map<JavaFileIdentifier, Set<JavaFileIdentifier>> fileDependencies,
                                                Set<JavaFileIdentifier> additionalFiles) {
        Set<JavaFileIdentifier> allFiles = flattenEntitiesFromDependencyMap(this.fileDependencies);
        allFiles.addAll(additionalFiles);
        Set<PackageIdentifier> allPackages = flattenEntitiesFromDependencyMap(this.packageDependencies);
        addFilesOfEmptyPackages(allFiles, allPackages);
        return allFiles;
    }


    private void addFilesOfEmptyPackages(Set<JavaFileIdentifier> allFiles, Set<PackageIdentifier> allPackages) {
        Set<PackageIdentifier> nonEmptyPackages = allFiles.stream().map(JavaFileIdentifier::getPackage).collect(toSet());
        Set<PackageIdentifier> emptyPackages = allPackages.stream()
                .filter(p -> !nonEmptyPackages.contains(p))
                .collect(toSet());
        allFiles.addAll(emptyPackages.stream().map(p -> p.getContainedJavaFiles().get(0)).collect(toSet()));
    }

    private <T> Set<T> flattenEntitiesFromDependencyMap(Map<T, Set<T>> dependencyMap) {
        Set<T> allEntities = dependencyMap.values().stream()
                .reduce(Sets::union).orElse(newHashSet());
        allEntities.addAll(dependencyMap.keySet());
        return allEntities;
    }


}
