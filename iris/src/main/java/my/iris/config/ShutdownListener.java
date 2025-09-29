package my.iris.config;

import my.iris.util.LogUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
public class ShutdownListener implements ApplicationListener<ContextClosedEvent> {
    @Override
    public void onApplicationEvent(@NonNull ContextClosedEvent event) {
        LogUtils.info(getClass(), "Shutdown.");
    }
}
