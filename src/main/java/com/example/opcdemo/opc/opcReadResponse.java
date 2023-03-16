package com.example.opcdemo.opc;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class opcReadResponse {
    private String tagCd;
	private String tagValue;
}
