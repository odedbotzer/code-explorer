package plantuml;

import com.google.common.collect.Sets;
import org.fest.util.Maps;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import upper.projectanalyzer.JavaFileIdentifier;
import upper.projectanalyzer.PackageIdentifier;

import java.util.Map;
import java.util.Set;

public class UmlBuilder {
    private Set<PackageIdentifier> packages = Sets.newHashSet();
    private Map<JavaFileIdentifier, Set<JavaFileIdentifier>> fileDependencies = Maps.newHashMap();
    private Map<PackageIdentifier, Set<PackageIdentifier>> packageDependencies = Maps.newHashMap();

    public UmlBuilder setPackages(Set<PackageIdentifier> packages) {
        this.packages = packages;
        return this;
    }

    public UmlBuilder addFileDependencies(Map<JavaFileIdentifier, Set<JavaFileIdentifier>> fileDependencies) {
        fileDependencies.entrySet().stream()
                .forEach(dependency -> this.fileDependencies.merge(dependency.getKey(), dependency.getValue(), Sets::union));
        return this;
    }

    public UmlBuilder addReverseFileDependencies(Set<JavaFileIdentifier> fromFiles, JavaFileIdentifier toFile) {
        Set<JavaFileIdentifier> toFileSet = Sets.newHashSet(toFile);
        fromFiles.stream().forEach(fromFile -> this.fileDependencies.merge(fromFile, toFileSet, Sets::union));
        return this;
    }

    public UmlBuilder addPackageDependencies(Map<PackageIdentifier, Set<PackageIdentifier>> packageDependencies) {
        packageDependencies.entrySet().stream()
                .forEach(dependency -> this.packageDependencies.merge(dependency.getKey(), dependency.getValue(), Sets::union));
        return this;
    }

    public UmlRepresentation build() {
        throw new NotImplementedException();
    }
}
