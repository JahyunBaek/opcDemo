package com.example.opcdemo.opc;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.NonNull;


@Data
public class opcRequest {
    private String opcTagCd;
	private String tagValue;
	private String changeValue;
	private Float scale;
	private opcType dataType;
	private String userId;
	private String deviceId;
}