package my.iris.config;

import my.iris.storage.LocalStorage;
import my.iris.storage.Storage;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class StorageConfig {
    @Bean
    Storage getStorage() {
        /*
        if ("oss".equals(SystemCache.getStorageType())) {
            return null;
        } else {
            return new LocalStorage();
        }*/
        return new LocalStorage();
    }
}
