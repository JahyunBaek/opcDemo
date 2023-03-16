package com.example.opcdemo.opc;

import lombok.Data;

@Data
public class opcAlarmRequest {
    private String code;
    private String level;
    private String name; 
}
