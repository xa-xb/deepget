package my.iris.service.file;

import my.iris.model.ApiResult;
import my.iris.model.file.dto.PathRenameDto;
import my.iris.model.file.vo.FileVo;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    ApiResult<Void> createDir(String filePath);

    ApiResult<Void> delete(String filePath);

    ApiResult<List<FileVo>> getList(String filePath);

    ApiResult<Void> rename(PathRenameDto renameDto);
    ApiResult<Void> uploadChunk(
            MultipartFile file,
            String path,
            String fileName,
            int chunkIndex,
            int totalChunks
    );

}