package com.example.opcdemo.opc;

import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/opc")
public class opcController {
    
	private final opcService opc;

    @PostMapping(value = "/write")
    public ResponseEntity<?> opcWrite(@RequestBody List<opcRequest> tagList) throws UaException, InterruptedException, ExecutionException{
        
        List<StatusCode> result = opc.opcRemoteWrite(tagList);

        opcResponse response = opcResponse.builder().successCount(
            result.stream().filter(x -> x.isGood()).count()).totalCount(tagList.size()).build();

        return ResponseEntity.ok().body(response);
    }
}
