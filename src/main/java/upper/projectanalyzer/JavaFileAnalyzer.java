package upper.projectanalyzer;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Set;

public class JavaFileAnalyzer {
    @NotNull
    static FilenameFilter javaFileFilter = (dir, name) -> name.endsWith(".java");

    static boolean doesFolderContainJavaFiles(File folder) {
        return folder.list(javaFileFilter).length > 0;
    }

    public static Set<PackageIdentifier> getAllPackagesImported(File javaFile) {
        return null;
    }
}
