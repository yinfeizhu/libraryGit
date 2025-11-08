package org.fly.utils;

public class CurrentHolder {

    private static final ThreadLocal<Integer> CURRENT_USERID = new ThreadLocal<>();

    //设置当前用户ID
    public static void setCurrentUserId(Integer userId) {
        CURRENT_USERID.set(userId);
    }
    //获取当前用户ID
    public static Integer getCurrentUserId() {
        return CURRENT_USERID.get();
    }
    //删除当前用户ID
    public static void remove() {
        CURRENT_USERID.remove();
    }
}
