package upper.filefollower;

import com.intellij.openapi.fileEditor.impl.EditorTabTitleProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import static com.intellij.openapi.ui.Messages.getInformationIcon;
import static com.intellij.openapi.ui.Messages.showMessageDialog;

public class EditorTabFollower implements EditorTabTitleProvider {
    private static String lastFile = "";
    public static boolean active = false;

    @Nullable
    @Override
    public String getEditorTabTitle(Project project, VirtualFile file) {
        String curPath = file.getPath();
        if (!curPath.equals(lastFile) && active) {
            String name = file.getNameWithoutExtension();
            showMessageDialog(project, "File name: " + name, "File Follower", getInformationIcon());
            lastFile = curPath;
        }
        return null;
    }
}
