package com.example.opcdemo.opc;

import lombok.Data;


@Data
public class opcRequest {
    private String opcTagCd;
	private String tagValue;
	private String changeValue;
	private Float scale;
}