package com.xcg.aitripassistant.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisJson implements Serializable {
    private String messageType;
    private String text;
    private Meta metadata;


}
