package com.xcg.aitripassistant.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result <T>{
    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> success(){
        return new Result<>(200, "success", null);
    }

    public static <T> Result<T> success(T data){
        return new Result<>(200,"success",data);
    }

    public static <T> Result<T> fail(String msg){
        return new Result<>(500,msg,null);
    }


}
