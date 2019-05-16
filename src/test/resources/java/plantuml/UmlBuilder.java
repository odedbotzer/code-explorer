package plantuml;

import com.google.common.collect.Sets;
import org.fest.util.Maps;
import upper.projectanalyzer.JavaFileIdentifier;
import upper.projectanalyzer.PackageIdentifier;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class UmlBuilder {
    private Set<JavaFileIdentifier> additionalFiles = Sets.newHashSet();
    private Map<JavaFileIdentifier, Set<JavaFileIdentifier>> fileDependencies = Maps.newHashMap();
    private Map<PackageIdentifier, Set<PackageIdentifier>> packageDependencies = Maps.newHashMap();

    public UmlBuilder addFiles(Set<JavaFileIdentifier> additionalFiles) {
        this.additionalFiles.addAll(additionalFiles);
        return this;
    }

    public UmlBuilder addFileDependencies(Map<JavaFileIdentifier, Set<JavaFileIdentifier>> fileDependencies) {
        fileDependencies.entrySet().stream()
                .forEach(dependency -> this.fileDependencies.merge(dependency.getKey(), dependency.getValue(), Sets::union));
        return this;
    }

    public UmlBuilder addReverseFileDependencies(Set<JavaFileIdentifier> fromFiles, JavaFileIdentifier toFile) {
        Set<JavaFileIdentifier> toFileSet = newHashSet(toFile);
        fromFiles.stream().forEach(fromFile -> this.fileDependencies.merge(fromFile, toFileSet, Sets::union));
        return this;
    }

    public UmlBuilder addPackageDependencies(Map<PackageIdentifier, Set<PackageIdentifier>> packageDependencies) {
        packageDependencies.entrySet().stream()
                .forEach(dependency -> this.packageDependencies.merge(dependency.getKey(), dependency.getValue(), Sets::union));
        return this;
    }

    public UmlRepresentation build() {
        return new UmlRepresentation(this.packageDependencies, this.fileDependencies, this.additionalFiles, Optional.empty());
    }

}
