package global.sud.op.hello.ui.game.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChooseImageResult {
    public List<String> tempFilePaths; // 图片的本地临时文件路径列表 (本地路径)
    public List<TempFilesModel> tempFiles; // 图片的本地临时文件列表

    public static class TempFilesModel {
        public String path; // 本地临时文件路径 (本地路径)
        public long number; // 本地临时文件大小，单位 B
    }

    public void addFile(File file) {
        if (file == null) {
            return;
        }
        if (tempFilePaths == null) {
            tempFilePaths = new ArrayList<>();
        }
        if (tempFiles == null) {
            tempFiles = new ArrayList<>();
        }
        String absolutePath = file.getAbsolutePath();
        tempFilePaths.add(absolutePath);

        TempFilesModel tempFilesModel = new TempFilesModel();
        tempFilesModel.path = absolutePath;
        tempFilesModel.number = file.length();
        tempFiles.add(tempFilesModel);
    }

}
