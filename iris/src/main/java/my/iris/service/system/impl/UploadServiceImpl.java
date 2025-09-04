package my.iris.service.system.impl;

import jakarta.annotation.Resource;
import my.iris.service.system.UploadService;
import my.iris.storage.Storage;
import my.iris.storage.UploadResult;
import my.iris.util.LogUtils;
import my.iris.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

@Service
public class UploadServiceImpl implements UploadService {
    @Resource
    Storage storage;

    @Override
    public UploadResult upload(MultipartFile file, boolean useOriginalName) {
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (StringUtils.hasText(ext)) {
            ext = ext.toLowerCase();
            if ("jpe".equals(ext) || "jpeg".equals(ext)) {
                ext = "jpg";
            }
            ext = "." + ext;
        }
        try {
            String uri = SecurityUtils.sha3_256(file.getInputStream()) + Long.toHexString(file.getSize());
            uri = new BigInteger(uri, 16).toString(36);
            uri = uri.substring(0, 2) + "/" + uri.substring(2, 4) + "/" + uri.substring(4);

            /* whether keep original filename */
            if (useOriginalName) {
                uri += "/" + file.getOriginalFilename();
            } else {
                uri += ext;
            }

            try {
                return storage.upload(file.getInputStream(), uri);
            } finally {
                file.getInputStream().close();
            }

        } catch (Exception ex) {
            LogUtils.error(this.getClass(), ex);
            return new UploadResult("系统错误", null);
        }
    }
}
