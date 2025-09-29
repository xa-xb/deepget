package my.iris.controller.admin.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import my.iris.auth.Authorize;
import my.iris.model.ApiResult;
import my.iris.model.file.dto.FilePathDto;
import my.iris.model.file.dto.PathRenameDto;
import my.iris.model.file.vo.FileVo;
import my.iris.service.file.FileService;
import my.iris.util.JsonUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/file/file", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin file file", description = "file file management")
public class FileController {

    @Resource
    FileService fileService;

    @Authorize("/file/file/upload")
    @Operation(summary = "Upload a file",
            description = "Upload a file with multipart/form-data"
    )
    @PostMapping(value = "upload_chunk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResult<Void> uploadChunk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("path") String path,
            @RequestParam("fileName") String fileName,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("totalChunks") int totalChunks
    ) {
        return fileService.uploadChunk(file, path, fileName, chunkIndex, totalChunks);
    }


    @Authorize("/file/file/create_dir")
    @PostMapping("create_dir")
    public ApiResult<Void> createDir(@RequestBody @Validated FilePathDto filePathDto) {
        return fileService.createDir(filePathDto.getFilePath());
    }

    @Authorize("/file/file/delete")
    @PostMapping("delete")
    public ApiResult<Void> delete(@RequestBody @Validated FilePathDto filePathDto) {
        return fileService.delete(filePathDto.getFilePath());
    }

    @Authorize("/file/file/query")
    @Operation(summary = "get file list")
    @PostMapping("list")
    public ApiResult<List<FileVo>> list(@RequestBody @Validated FilePathDto filePathDto) {
        return fileService.getList(filePathDto.getFilePath());
    }

    @Authorize("/file/file/rename")
    @PostMapping(value = "rename")
    public ApiResult<Void> rename(@RequestBody @Validated PathRenameDto renameDto) {
        return fileService.rename(renameDto);
    }
}
