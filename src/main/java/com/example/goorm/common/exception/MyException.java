package com.example.goorm.common.exception;

import lombok.Getter;

@Getter
public class MyException extends RuntimeException{
    private MyErrorCode errorCode;

    public MyException(MyErrorCode errorCode){
        this.errorCode = errorCode;
    }
}
