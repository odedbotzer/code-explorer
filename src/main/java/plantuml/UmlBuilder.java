package plantuml;

import com.google.common.collect.Sets;
import org.fest.util.Maps;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import upper.projectanalyzer.JavaFileIdentifier;
import upper.projectanalyzer.PackageIdentifier;

import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

public class UmlBuilder {
    private Set<PackageIdentifier> packages = Sets.newHashSet();
    private Map<JavaFileIdentifier, Set<JavaFileIdentifier>> fileDependencies = Maps.newHashMap();
    private Map<PackageIdentifier, Set<PackageIdentifier>> packageDependencies = Maps.newHashMap();

    public UmlBuilder setPackages(Set<PackageIdentifier> packages) {
        this.packages = packages;
        return this;
    }

    public UmlBuilder addFileDependencies(Map<JavaFileIdentifier, Set<JavaFileIdentifier>> fileDependencies) {
        this.fileDependencies.putAll(fileDependencies);
        return this;
    }

    public UmlBuilder addReverseFileDependencies(Set<JavaFileIdentifier> fromFiles, JavaFileIdentifier toFile) {
        Map<JavaFileIdentifier, Set<JavaFileIdentifier>> unreversedDependencyMap = fromFiles.stream()
                .collect(toMap(fromFile -> fromFile, fromFile -> Sets.newHashSet(toFile)));
        this.fileDependencies.putAll(unreversedDependencyMap);
        return this;
    }

    public UmlBuilder addPackageDependencies(Map<PackageIdentifier, Set<PackageIdentifier>> packageDependencies) {
        this.packageDependencies.putAll(packageDependencies);
        return this;
    }

    public UmlRepresentation build() {
        throw new NotImplementedException();
    }
}
