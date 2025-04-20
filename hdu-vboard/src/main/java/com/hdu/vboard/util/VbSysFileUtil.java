package com.hdu.vboard.util;

import cn.hutool.core.io.FileUtil;
import org.apache.commons.io.FileExistsException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class VbSysFileUtil {
    public static void saveFile(MultipartFile multipartFile, String fullPath) throws IOException {
        if (multipartFile.isEmpty()) return;
        File file = new File(fullPath);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new FileExistsException("创建文件夹出错");
            }
        }
        multipartFile.transferTo(file);
    }

    public static String getRootBasePath() {
        String absolutePath = FileUtil.getAbsolutePath(".");
        if (absolutePath.contains("target/")) {
            return FileUtil.getAbsolutePath("../../");
        }
        return absolutePath;
    }

    public static String getVbBasePath() {
        return "vb/";
    }

    // 查找目录而非文件路径
    // vb
    // |__save      // namespace下保存top.v和bind.json
    // |__workbench // namespace下保存工作区
    public static String getSavePath(String dirName) {
        String basePath = getVbBasePath();
        if (Objects.equals(dirName, "")) {
            basePath += "save";
        } else {
            basePath += "save/" + dirName;
        }
        String absolutePath = FileUtil.getAbsolutePath(basePath);
        if (absolutePath.contains("target/")) {
            return "../../" + basePath;
        }
        return basePath;
    }

    public static String getWorkbenchPath(String dirName) {
        String basePath = getVbBasePath();
        if (Objects.equals(dirName, "")) {
            basePath += "workbench";
        } else {
            basePath += "workbench/" + dirName;
        }
        String absolutePath = FileUtil.getAbsolutePath(basePath);
        if (absolutePath.contains("target/")) {
            return "../../" + basePath;
        }
        return basePath;
    }

    public static String getFullSavePath(String dirName) {
        return FileUtil.getAbsolutePath(getSavePath(dirName));
    }

    public static String getFullWorkbenchPath(String dirName) {
        return FileUtil.getAbsolutePath(getWorkbenchPath(dirName));
    }

    public static void deleteDirectory(File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    FileUtil.del(file);
                }
            }
        }
        FileUtil.del(directory);
    }
}
