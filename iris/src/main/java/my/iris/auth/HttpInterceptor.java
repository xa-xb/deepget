package my.iris.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import my.iris.cache.AdminMenuCache;
import my.iris.config.AppConfig;
import my.iris.model.ApiResult;
import my.iris.model.system.entity.AdminEntity;
import my.iris.service.system.AdminRoleService;
import my.iris.service.system.AdminService;
import my.iris.util.JsonUtils;
import my.iris.util.LogUtils;
import my.iris.util.TaskContext;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * http interceptor, invalid for with websocket
 */
public final class HttpInterceptor implements HandlerInterceptor {
    private final AdminMenuCache adminMenuCache;
    private final AdminService adminService;
    private final AdminRoleService adminRoleService;

    private final static String ADMIN_API_PREFIX = "/admin/";
    private final static String USER_API_PREFIX = "/api/";
    private final static String[] USER_API_PREFIXES_SKIP_AUTH;

    static {
        USER_API_PREFIXES_SKIP_AUTH = new String[]{
                USER_API_PREFIX + "v1/main/",
                USER_API_PREFIX + "v1/test/",
        };
        Arrays.sort(USER_API_PREFIXES_SKIP_AUTH);
    }


    public HttpInterceptor() {
        adminMenuCache = AppConfig.getContext().getBean(AdminMenuCache.class);
        adminService = AppConfig.getContext().getBean(AdminService.class);
        adminRoleService = AppConfig.getContext().getBean(AdminRoleService.class);
    }

    /**
     * initial global parameters
     *
     * @param request  req
     * @param response res
     * @param handler  handler
     * @return true
     */
    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {
        boolean result = true;
        TaskContext.init(request, response);
        String uri = request.getRequestURI();
        if (handler instanceof HandlerMethod handlerMethod) {
            if (uri.startsWith(ADMIN_API_PREFIX)) {
                result = adminAuth(response, handlerMethod);
            } else if (uri.startsWith(USER_API_PREFIX)) {
                result = userAuth(uri, response);
            }
        }
        if (!result) {
            // If authentication fails, clean up the context to avoid memory leaks
            TaskContext.clean();
        }
        return result;
    }

    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            Exception ex) {
        TaskContext.clean();
    }

    private boolean adminAuth(HttpServletResponse response, HandlerMethod handlerMethod) {

        var authorize = handlerMethod.getMethod().getAnnotation(Authorize.class);
        if (authorize == null) {
            setResponse(response, ApiResult.unauthorized(null));
            return false;
        } else if (authorize.skipAuth()) {
            return true;
        }

        var userToken = TaskContext.getUserToken();
        AdminEntity adminEntity;
        if (userToken == null
                || (adminEntity = adminService.findByUserId(userToken.getUserId())) == null) {
            setResponse(response, ApiResult.forbidden("请先登录"));
            return false;
        }
        if (!adminEntity.getEnabled()) {
            setResponse(response, ApiResult.forbidden("账户已禁用"));
            return false;
        }

        var authorizeIds = adminRoleService.getAuthorizeIdsByRolesId(adminEntity.getRoleIds());
        var authorizeNames = adminMenuCache.getEnabledAuthorizeByIds(authorizeIds);
        AdminToken adminToken = new AdminToken(authorizeNames);
        userToken.copyTo(adminToken);
        TaskContext.setAdminToken(adminToken);
        // System built-in administrator or methods annotated 'skipAuthorization' skips authorize check
        if (adminEntity.getRoleIds().contains(1L) || authorize.skipAuthorization()) {
            return true;
        }

        for (var item : authorizeNames) {
            if (ObjectUtils.containsElement(authorize.value(), item)) {
                return true;
            }
        }

        setResponse(response, ApiResult.unauthorized("权限不足"));
        return false;
    }

    private boolean userAuth(String uri, HttpServletResponse response) {
        if (TaskContext.getUserToken() != null) {
            return true;
        }
        for (var item : USER_API_PREFIXES_SKIP_AUTH) {
            if (uri.startsWith(item)) {
                return true;
            }
        }
        setResponse(response, ApiResult.forbidden(null));
        return false;
    }

    private void setResponse(HttpServletResponse response, ApiResult<Void> apiResult) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            response.getWriter().write(JsonUtils.stringify(apiResult));
        } catch (Exception e) {
            LogUtils.error(getClass(), e);
        }
    }
}
