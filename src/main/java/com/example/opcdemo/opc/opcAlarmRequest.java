package com.example.opcdemo.opc;

import lombok.Data;

@Data
public class opcAlarmRequest {
    private String topic;
    private String code;
    private String level;
    private String name; 
}
