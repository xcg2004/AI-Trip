package com.xcg.aitripassistant.utils;

public class UserHolder {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static Long getUser() {
        return threadLocal.get();
    }

    public static void setUser(Long userId){
        threadLocal.set(userId);
    }

    public static void removeUser(){
        threadLocal.remove();
    }
}
