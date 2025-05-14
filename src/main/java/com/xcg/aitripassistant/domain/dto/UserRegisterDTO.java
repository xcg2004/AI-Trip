package com.xcg.aitripassistant.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO implements Serializable {
    private String username;

    private String password;

    private String nickname;

    private String email;

}
