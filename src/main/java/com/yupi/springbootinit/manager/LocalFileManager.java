package com.yupi.springbootinit.manager;



import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class LocalFileManager {

    private final String storageDirectory = "/Users/manydory/Downloads/springboot-init-master 2/files"; // 本地存储目录

    public String saveFile(String filename, File file) throws IOException {
        // 确保存储目录存在
        Path storagePath = Paths.get(storageDirectory);
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }

        // 目标文件路径
        Path targetPath = storagePath.resolve(filename);

        // 将文件复制到目标路径
        Files.copy(file.toPath(), targetPath);

        // 返回文件的存储路径
        return targetPath.toString();
    }

    public File getFile(String filename) {
        // 获取文件
        Path filePath = Paths.get(storageDirectory, filename);
        return filePath.toFile();
    }

    public boolean deleteFile(String filename) {
        // 删除文件
        Path filePath = Paths.get(storageDirectory, filename);
        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}