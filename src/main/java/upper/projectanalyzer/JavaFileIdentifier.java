package upper.projectanalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class JavaFileIdentifier {

    private final String fullPath;
    private final File fileObj;

    public JavaFileIdentifier(File file) {
        this.fileObj = file;
        this.fullPath = file.getAbsolutePath();
        if (!fileObj.isFile())
            throw new RuntimeException(fullPath + " is not a file");
        if (!this.fullPath.endsWith(".java"))
            throw new RuntimeException(fullPath + " is not a java file");
    }

    public JavaFileIdentifier(String fullPath) {
        this(new File(fullPath));
    }

    public PackageIdentifier getPackage() {
        return new PackageIdentifier(fileObj.getParentFile());
    }

    public Reader getFileReader() {
        try {
            return new FileReader(this.fileObj);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("not really possible");
        }
    }

    @Override
    public String toString() {
        return this.fileObj.getName();
    }
}
