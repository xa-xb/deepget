package my.iris.service.file.impl;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import my.iris.model.ApiResult;
import my.iris.model.file.dto.PathRenameDto;
import my.iris.model.file.vo.FileVo;
import my.iris.service.file.FileService;
import my.iris.service.system.AdminLogService;
import my.iris.storage.LocalStorage;
import my.iris.util.LogUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class FileServiceImpl implements FileService {

    @Resource
    AdminLogService adminLogService;

    @Override
    public ApiResult<Void> createDir(String filePath) {
        Path path;
        try {
            path = getPath(filePath);
        } catch (IllegalArgumentException e) {
            return ApiResult.badRequest(e.getMessage());
        }
        if (Files.exists(path)) return ApiResult.error("创建失败, 路径已存在");
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            LogUtils.error(getClass(), null, e);
            return ApiResult.error("创建失败");
        }
        adminLogService.addLog("create_directory", Map.of("path", filePath));
        return ApiResult.success();
    }

    @Override
    public ApiResult<Void> delete(String filePath) {
        Path path;
        try {
            path = getPath(filePath);
        } catch (IllegalArgumentException e) {
            return ApiResult.badRequest(e.getMessage());
        }

        try {
            Files.delete(path);
        } catch (IOException e) {
            if (e instanceof DirectoryNotEmptyException) {
                return ApiResult.error("不能删除非空目录");
            } else if (e instanceof NoSuchFileException) {
                return ApiResult.error("文件不存在");
            }
            LogUtils.error(getClass(), "", e);
            return ApiResult.error("删除失败");
        }
        adminLogService.addLog("delete_file", Map.of("path", filePath));
        return ApiResult.success();


    }

    @Override
    public ApiResult<List<FileVo>> getList(String dirPath) {
        dirPath = dirPath == null ? "" : dirPath;
        Path path;
        try {
            path = getPath(dirPath);
        } catch (IllegalArgumentException e) {
            return ApiResult.badRequest(e.getMessage());
        }

        // Check if path exists and is a directory
        if (!Files.exists(path)
            || !Files.isDirectory(path)) {
            return ApiResult.success(List.of());
        }


        try (var list = Files.list(path)) {
            // Mapping file attributes to FileVo with optimized stream processing
            return ApiResult.success(list.sorted((p1, p2) -> {
                // 目录排在前面
                if (Files.isDirectory(p1) && !Files.isDirectory(p2)) {
                    return -1; // p1 是目录，p2 是文件，p1 应排在前面
                }
                if (!Files.isDirectory(p1) && Files.isDirectory(p2)) {
                    return 1;  // p1 是文件，p2 是目录，p2 应排在前面
                }
                // 目录和文件都按字母排序
                return p1.getFileName().toString().compareToIgnoreCase(p2.getFileName().toString());
            }).map(this::createFileVo).toList());
        } catch (IOException e) {
            // Log exception here (e.g., using a logger)
            return ApiResult.success(List.of());
        }
    }

    @Override
    public ApiResult<Void> rename(PathRenameDto renameDto) {
        Path pathNew, pathOld;
        try {
            pathNew = getPath(renameDto.getNewPath());
            pathOld = getPath(renameDto.getOldPath());
        } catch (IllegalArgumentException e) {
            return ApiResult.badRequest(e.getMessage());
        }
        if (pathNew.equals(pathOld)) return ApiResult.success();
        try {
            Files.move(pathOld, pathNew);
        } catch (IOException e) {
            if (e instanceof FileAlreadyExistsException) {
                return ApiResult.error("target exists");
            }
            LogUtils.error(getClass(), null, e);
            return ApiResult.error("rename failed");
        }
        adminLogService.addLog("path_rename", renameDto);
        return ApiResult.success();
    }

    @Override
    public ApiResult<Void> uploadChunk(MultipartFile file, String path, String fileName, int chunkIndex, int totalChunks) {
        Path filepath;
        try {
            filepath = getPath(path + '/' + fileName);
        } catch (IllegalArgumentException e) {
            return ApiResult.badRequest(e.getMessage());
        }
        var exists = Files.exists(filepath);
        if (chunkIndex == 1 && exists) {
            return ApiResult.error("文件已存在");
        } else if (chunkIndex > 1 && !exists) {
            return ApiResult.error("The file does not exist");
        }
        try {
            Files.write(filepath, file.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            LogUtils.error(getClass(), null, e);
            return ApiResult.error("File write failed");
        }
        // finished
        if (chunkIndex == totalChunks) {
            long fileSize = -1;
            try {
                fileSize = Files.size(filepath);
            } catch (IOException e) {
                LogUtils.error(getClass(), "", e);
            }

            adminLogService.addLog("upload_chunk_finished",
                    Map.of("path", filepath.toString(),
                            "size", fileSize));
        }
        return ApiResult.success();
    }

    // Helper method to create FileVo from Path
    private FileVo createFileVo(Path item) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(item, BasicFileAttributes.class);
            return new FileVo(
                    attrs.isDirectory(),
                    item.getFileName().toString(),
                    attrs.size(),
                    convertToLocalDateTime(attrs.lastModifiedTime())
            );
        } catch (IOException e) {
            // Log the error or handle it accordingly
            return null;  // Return null or a default value if there's an error reading file attributes
        }
    }

    // Helper method to convert FileTime to LocalDateTime
    private LocalDateTime convertToLocalDateTime(FileTime fileTime) {
        return LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
    }

    private Path getPath(String filepath) throws IllegalArgumentException {
        try {
            Path path = Paths.get(LocalStorage.UPLOAD_DIR, filepath).normalize();
            if (!path.startsWith(LocalStorage.UPLOAD_DIR))
                throw new IllegalArgumentException("file path error");
            return path;
        } catch (InvalidPathException ignored) {
            throw new IllegalArgumentException("file path error");
        }


    }

}
