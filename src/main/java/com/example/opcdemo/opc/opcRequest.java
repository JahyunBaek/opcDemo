package com.example.opcdemo.opc;

import lombok.Data;
import lombok.NonNull;


@Data
public class opcRequest {
    private String tagCd;
	private String tagValue;
	private String changeValue;
	private Float scale;
}