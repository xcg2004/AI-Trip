package com.xcg.aitripassistant.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Meta implements Serializable {
    private String role;

    private String messageType;

    private String finishReason;

    private String refusal;

    private String index;

    private String id;
}
