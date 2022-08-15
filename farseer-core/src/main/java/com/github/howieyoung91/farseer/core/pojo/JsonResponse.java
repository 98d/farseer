package com.github.howieyoung91.farseer.core.pojo;

public class JsonResponse {
    public static final int SUCCESSFUL   = 200; // OK
    public static final int REDISTRIBUTE = 302; // 重定向
    public static final int FAIL         = 400; // fail without reasons
    public static final int UNAUTHORIZED = 401; // 未授权
    public static final int FORBIDDEN    = 403; // 操作禁止
    public static final int NOT_FOUND    = 404; // 资源未找到

    private static final JsonResponse FAIL_SINGLETON
            = new JsonResponse().fail();
    private static final JsonResponse SUCCESSFUL_SINGLETON
            = new JsonResponse().success();
    private static final JsonResponse REDISTRIBUTE_SINGLETON
            = new JsonResponse().redistribute();
    private static final JsonResponse UNAUTHORIZED_SINGLETON
            = new JsonResponse().unauthorized().message("authorized");

    private int    code;
    private String message;
    private Object data;

    public static JsonResponse FAIL(String message) {
        return new JsonResponse().fail().message(message);
    }

    public static JsonResponse FAIL() {
        return FAIL_SINGLETON;
    }

    public static JsonResponse SUCCESSFUL() {
        return SUCCESSFUL_SINGLETON;
    }

    public static JsonResponse SUCCESSFUL(Object data) {
        return new JsonResponse().success().data(data);
    }

    public static JsonResponse REDISTRIBUTE() {
        return REDISTRIBUTE_SINGLETON;
    }

    public static JsonResponse REDISTRIBUTE(String message) {
        return new JsonResponse().redistribute().message(message);
    }

    public static JsonResponse UNAUTHORIZED() {
        return UNAUTHORIZED_SINGLETON;
    }

    public static JsonResponse of(int code, String message, Object data) {
        return new JsonResponse().code(code).message(message).data(data);
    }

    public static JsonResponse of(int code, String message) {
        return new JsonResponse().code(code).message(message);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public JsonResponse code(int code) {
        this.code = code;
        return this;
    }

    public JsonResponse message(String message) {
        this.message = message;
        return this;
    }

    public JsonResponse data(Object data) {
        this.data = data;
        return this;
    }

    public JsonResponse fail() {
        this.code = FAIL;
        return this;
    }

    public JsonResponse success() {
        this.code = SUCCESSFUL;
        return this;
    }

    public JsonResponse redistribute() {
        this.code = REDISTRIBUTE;
        return this;
    }

    public JsonResponse unauthorized() {
        this.code = UNAUTHORIZED;
        return this;
    }

    @Override
    public String toString() {
        return "JsonResponse{" +
               "code=" + code +
               ", message='" + message + '\'' +
               ", data=" + data +
               '}';
    }
}
