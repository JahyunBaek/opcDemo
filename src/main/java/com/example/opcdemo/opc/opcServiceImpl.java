package com.example.opcdemo.opc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;

@Service
@RequiredArgsConstructor
public class opcServiceImpl implements opcService {

    @Value("${OPC.UA.Server}")
    private String opcServerUri;
    @Value("${OPC.UA.ApplicationUri}")
    private String opcAppUri;
    @Value("${OPC.UA.ApplicationName}")
    private String opcAppName;
    @Value("${OPC.UA.RequestTimeout}")
    private Integer RequestTimeout;
    
    @Override
    public List<StatusCode> opcRemoteWrite(List<opcRequest> tagList) throws UaException, InterruptedException, ExecutionException{

        List<NodeId> nodeList = new ArrayList<NodeId>();
		List<DataValue> dataList = new ArrayList<DataValue>();
		CompletableFuture<OpcUaClient> future = new CompletableFuture<OpcUaClient>();
		
		tagList.stream().forEach(x -> {
			String currentValue = x.getChangeValue();
			NodeId node = new NodeId(2,x.getOpcTagCd());
			Variant v = null;
			if(currentValue.equals("true") || currentValue.equals("false")) 
				v = new Variant(Boolean.valueOf(currentValue));			
			else{
				Float parseValue = Float.parseFloat(currentValue) / x.getScale();						
				short convertValue =(short)Math.floor(parseValue);		
				v = new Variant(ushort(convertValue));				
			}
			DataValue dv = new DataValue(v, null, null);

			nodeList.add(node);
			dataList.add(dv);
		});

		
		OpcUaClient client = OpcUaClient.create(
            opcServerUri,
	            endpoints ->
	                endpoints.stream()
	                    .filter(e -> SecurityPolicy.None.getUri().equals(e.getSecurityPolicyUri()))
	                    .findFirst(),
	            configBuilder ->
	                configBuilder
	                    .setApplicationName(LocalizedText.english(opcAppName))
	                    .setApplicationUri(opcAppUri)                   
	                    .setRequestTimeout(uint(RequestTimeout))
	                    .build()
	        );
		
		client.connect().get();

		CompletableFuture<List<StatusCode>> f = client.writeValues(nodeList, dataList);
		List<StatusCode> s = f.get();

		future.complete(client);

        return s;
    }
    
    
}
