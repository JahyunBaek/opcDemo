package com.example.opcdemo.opc;



import lombok.Builder;
import lombok.Data;

@Data
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class opcResponse {
    private long totalCount;
    private long successCount;
}
