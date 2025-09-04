package my.iris.config;

import my.iris.model.ApiResult;
import my.iris.util.JsonUtils;
import my.iris.util.LogUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.hibernate.HibernateException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<String> getResponseEntity(ApiResult<?> apiResponse) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(
                JsonUtils.stringify(apiResponse),
                httpHeaders,
                HttpStatus.OK.value());
    }


    /**
     * generate error response entity
     *
     * @return response entity
     */
    private ResponseEntity<String> getResponseEntity(HttpStatus httpStatus) {
        return getResponseEntity(httpStatus, null);
    }

    private ResponseEntity<String> getResponseEntity(HttpStatus httpStatus, String errorDetail) {
        HttpHeaders httpHeaders = new HttpHeaders();
        ApiResult<?> apiResult;
        int httpStatusCode = httpStatus.value();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        String msg = Strings.isEmpty(errorDetail)
                ? httpStatus.getReasonPhrase()
                : errorDetail;
        if (HttpStatus.BAD_REQUEST.equals(httpStatus)) {
            apiResult = ApiResult.badRequest(msg);
            httpStatusCode = HttpStatus.OK.value();
        } else {
            apiResult = ApiResult.error(msg);
        }
        return new ResponseEntity<>(
                JsonUtils.stringify(apiResult),
                httpHeaders,
                httpStatusCode);
    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    @ResponseBody
    public ResponseEntity<String> handleHttpMediaTypeNotSupportedException() {
        return getResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * request data transfer exception
     *
     * @return msg
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseBody
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return getResponseEntity(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
    }

    /**
     * HttpRequestMethodNotSupportedException
     *
     * @return METHOD_NOT_ALLOWED info
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseBody
    public ResponseEntity<String> handleHttpRequestMethodNotSupportedException() {
        return getResponseEntity(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * 上传文件名包含\u0000等字符串时会引发该错误
     *
     * @return 400 error info
     */
    @ExceptionHandler(InvalidFileNameException.class)
    @ResponseBody
    public ResponseEntity<String> handleInvalidFileNameExceptionException() {
        return getResponseEntity(HttpStatus.BAD_REQUEST);
    }


    /**
     * request data transfer exception with
     *
     * @param ex exception
     * @return msg
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var bindingResult = ex.getBindingResult();
        StringBuilder sb = new StringBuilder();
        if (bindingResult.hasErrors()) {
            var err = bindingResult.getFieldErrors().getFirst();
            sb.append("数据错误, 字段 ")
                    .append(err.getField())
                    .append(" ")
                    .append(err.getDefaultMessage());

        }
        return getResponseEntity(
                ApiResult.badRequest(sb.toString())
        );
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseBody
    public ResponseEntity<String> handleMultipartException() {
        return getResponseEntity(ApiResult.badRequest("Current request is not a multipart request"));
    }


    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseBody
    public ResponseEntity<String> handleFileNotFoundException() {
        return getResponseEntity(HttpStatus.NOT_FOUND);
    }


    /**
     * 上传文件过大时会引发该错误
     *
     * @return 413 error info
     */
    @ExceptionHandler(SizeLimitExceededException.class)
    @ResponseBody
    public ResponseEntity<String> handleSizeLimitExceededExceptionException() {
        return getResponseEntity(HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(HibernateException.class)
    @ResponseBody
    public ResponseEntity<String> handleInternalServerError(Exception e) {
        LogUtils.error(getClass(), e);
        return getResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
