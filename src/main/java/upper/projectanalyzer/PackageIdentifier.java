package upper.projectanalyzer;

import java.io.File;
import java.util.Objects;

import static upper.projectanalyzer.JavaFileAnalyzer.javaFileFilter;

public class PackageIdentifier {
    final String fullPath;
    private final File fileObj;

    public PackageIdentifier(String fullPath) {
        fileObj = new File(fullPath);
        if (!fileObj.isDirectory())
            throw new RuntimeException(fullPath + " is not a directory");
        this.fullPath = fullPath;
    }

    public File[] getContainedJavaFiles() {
        return fileObj.listFiles(javaFileFilter);
    }


//    private String folderPathToPackageName(String fullFolderPath) {
//        return fullFolderPath
//                .substring(this.srcFolder.getAbsolutePath().length() + File.separator.length())
//                .replace(File.separator, ".");
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageIdentifier that = (PackageIdentifier) o;
        return Objects.equals(fullPath, that.fullPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullPath);
    }
}
