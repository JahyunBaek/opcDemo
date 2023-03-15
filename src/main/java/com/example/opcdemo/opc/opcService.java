package com.example.opcdemo.opc;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;

public interface opcService {
    List<StatusCode> opcRemoteWrite(List<opcRequest> tagList) throws UaException, InterruptedException, ExecutionException;
    List<DataValue> opcRemoteRead(List<String> tagList) throws UaException, InterruptedException, ExecutionException;
}
