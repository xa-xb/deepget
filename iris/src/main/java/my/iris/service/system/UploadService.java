package my.iris.service.system;

import jakarta.servlet.http.HttpServletRequest;
import my.iris.model.ApiResult;
import my.iris.storage.UploadResult;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    String[] IMAGES_EXT = new String[]{
            "jpe", "jpeg", "jpg", "png", "svg", "webp"
    };
    default String checkFile(MultipartFile file, int sizeLimit, boolean imgOnly) {
        if (file == null || file.isEmpty() || !StringUtils.hasText(file.getOriginalFilename())) {
            return "未找到上传文件";
        }
        if (sizeLimit > 0 && file.getSize() > sizeLimit) {
            return "文件体积太大";
        }
        if (imgOnly) {
            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            if (StringUtils.hasLength(ext)) {
                ext = ext.toLowerCase();
            }
            if (!ObjectUtils.containsElement(IMAGES_EXT, ext)) {
                return file.getOriginalFilename() + " 不是图片文件";
            }
        }
        return null;
    }
     UploadResult upload(MultipartFile file, boolean useOriginalName) ;
}
