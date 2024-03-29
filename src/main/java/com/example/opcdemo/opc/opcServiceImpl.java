package com.example.opcdemo.opc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.springframework.stereotype.Service;

import com.digitalpetri.opcua.stack.core.Identifiers;
import com.google.common.collect.Streams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;

@Service
@RequiredArgsConstructor
@Slf4j
public class opcServiceImpl implements opcService {

    @Value("${OPC.UA.Server}")
    private String opcServerUri;
    @Value("${OPC.UA.ApplicationUri}")
    private String opcAppUri;
    @Value("${OPC.UA.ApplicationName}")
    private String opcAppName;
    @Value("${OPC.UA.RequestTimeout}")
    private Integer opcRequestTimeout;
    @Value("${OPC.UA.TrueStr}")
    private String trueStr;
	@Value("${OPC.UA.NameSpaceIDX}")
    private Integer nameSpaceIdx;

    @Override
    public List<StatusCode> opcRemoteWrite(List<opcRequest> tagList) throws UaException, InterruptedException, ExecutionException{

        List<NodeId> nodeList = new ArrayList<NodeId>();
		List<DataValue> dataList = new ArrayList<DataValue>();
		CompletableFuture<OpcUaClient> future = new CompletableFuture<>();
		

		tagList.stream().forEach(x -> {

			String currentValue = x.getChangeValue();
			NodeId node = new NodeId(nameSpaceIdx,x.getOpcTagCd());
			Variant v = null;
			if(x.getDataType().equals(opcType.Boolean)){
				v = new Variant(currentValue.equals(trueStr) ? true : false);						
			}else if(x.getDataType().equals(opcType.UInt16)){
				Float parseValue = Float.parseFloat(currentValue) / x.getScale();						
				short convertValue =(short)Math.floor(parseValue);		
				v = new Variant(ushort(convertValue));
			}

			DataValue dv = new DataValue(v,null,null);

			nodeList.add(node);
			dataList.add(dv);
		});

		
		OpcUaClient client = createOpcClient();
		
		future.whenCompleteAsync((c, ex) -> {
			try {
                c.disconnect().get();
                Stack.releaseSharedResources();

				if (ex != null) 
               		log.error("error");
				else
					log.info("input log");
            } catch (InterruptedException | ExecutionException e) {
					log.error("disconnecting error log");
            }
        });

		client.connect().get();
		
		CompletableFuture<List<StatusCode>> f = client.writeValues(nodeList, dataList);
		List<StatusCode> resultList = f.get();

		future.complete(client);
		
        return resultList;
    }

	@Override
	public List<opcReadResponse> opcRemoteRead(List<String> tagList)
			throws UaException, InterruptedException, ExecutionException {

		List<NodeId> nodeList = new ArrayList<NodeId>();
		CompletableFuture<OpcUaClient> future = new CompletableFuture<>();
		
		future.whenCompleteAsync((c, ex) -> {
			try {
                c.disconnect().get();
                Stack.releaseSharedResources();

				if (ex != null) 
					log.error("error");
				else
					log.info("input log");
            } catch (InterruptedException | ExecutionException e) {
					log.info("disconnecting error log");
            }
        });

		tagList.stream().forEach(x -> {
			NodeId node = new NodeId(nameSpaceIdx,x);
			nodeList.add(node);
		});
		
		OpcUaClient client = createOpcClient();
		
		client.connect().get();
		
		CompletableFuture<List<DataValue>> f = client.readValues(0.0, TimestampsToReturn.Both, nodeList);
		List<DataValue> resultList = f.get();
		
		Stream<opcReadResponse> stream = Streams.zip( tagList.stream(),resultList.stream(),
		 (id, value) -> opcReadResponse.builder()
		 .opcTagCd(id).tagValue(String.valueOf(value.getValue().getValue())).build());

		 future.complete(client);

		return stream.collect(Collectors.toList());
	}

	private OpcUaClient createOpcClient() throws UaException{
		return OpcUaClient.create(
            opcServerUri,
	            endpoints ->
	                endpoints.stream()
	                    .filter(e -> SecurityPolicy.None.getUri().equals(e.getSecurityPolicyUri()))
	                    .findFirst(),
	            configBuilder ->
	                configBuilder
	                    .setApplicationName(LocalizedText.english(opcAppName))
	                    .setApplicationUri(opcAppUri)                   
	                    .setRequestTimeout(uint(opcRequestTimeout))
	                    .build()
	        );
	}
    
    
}
