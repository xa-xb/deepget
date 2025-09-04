package my.iris.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import my.iris.auth.AdminToken;
import my.iris.auth.UserToken;
import my.iris.util.session.Session;

/**
 * 任务上下文，记录当前请求或任务的上下文信息。
 */
public final class TaskContext {
    private static final ThreadLocal<AdminToken> ADMIN_TOKEN = new ThreadLocal<>();
    private static final ThreadLocal<String> CLIENT_IP = new ThreadLocal<>();
    private static final ThreadLocal<Session> SESSION = new ThreadLocal<>();
    // 基于线程存储每个请求/任务的开始时间
    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    // 重置为当前时间
    public static void init(HttpServletRequest request, HttpServletResponse response) {
        START_TIME.set(System.currentTimeMillis());
        SESSION.set(new Session(request, response));
        CLIENT_IP.set(Helper.getClientIp(request));
    }

    public static AdminToken getAdminToken() {
        return ADMIN_TOKEN.get();
    }

    public static void setAdminToken(AdminToken adminToken) {
        TaskContext.ADMIN_TOKEN.set(adminToken);
    }

    public static String getClientIp() {
        return CLIENT_IP.get();
    }

    // 获取当前线程的开始时间
    public static long getStartTime() {
        Long time = START_TIME.get();
        if (time == null) {
            throw new IllegalStateException("Start time not initialized. Please call init() first.");
        }
        return time;
    }

    public static int getDuration() {
        return (int) (System.currentTimeMillis() - getStartTime());
    }

    public static Session getSession() {
        return SESSION.get();
    }

    public static void setSession(Session session) {
        TaskContext.SESSION.set(session);
    }


    public static UserToken getUserToken() {
        return UserToken.from(SESSION.get());
    }

    public static void clean() {
        ADMIN_TOKEN.remove();
        CLIENT_IP.remove();
        SESSION.remove();
        START_TIME.remove();
    }
}
