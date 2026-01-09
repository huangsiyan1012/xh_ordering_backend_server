package com.xh.ordering.common;

/**
 * 统一响应码枚举
 */
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    
    // 4xx 客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    
    // 5xx 服务器错误
    INTERNAL_ERROR(500, "系统内部错误"),
    
    // 业务错误码 1000+
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_DISABLED(1002, "用户已被禁用"),
    USER_ALREADY_EXISTS(1003, "用户已存在"),
    PASSWORD_ERROR(1004, "密码错误"),
    PHONE_ALREADY_EXISTS(1005, "手机号已注册"),
    
    ADMIN_NOT_FOUND(1101, "管理员不存在"),
    ADMIN_DISABLED(1102, "管理员账号已被禁用"),
    ADMIN_PASSWORD_ERROR(1103, "管理员密码错误"),
    
    PRODUCT_NOT_FOUND(1201, "菜品不存在"),
    PRODUCT_OFFLINE(1202, "菜品已下架"),
    
    POINTS_NOT_ENOUGH(1301, "积分不足"),
    POINTS_INVALID(1302, "积分金额无效"),
    
    ORDER_CREATE_FAILED(1401, "订单创建失败"),
    ORDER_NOT_FOUND(1402, "订单不存在");
    
    private final Integer code;
    private final String message;
    
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}

