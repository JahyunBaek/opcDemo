package com.example.opcdemo.opc;



import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class opcResponse {
    private long totalCount;
    private long successCount;   
}
