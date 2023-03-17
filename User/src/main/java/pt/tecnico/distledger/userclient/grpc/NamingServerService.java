package pt.tecnico.distledger.userclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.List;

public class NamingServerService {

	private final ManagedChannel channel;
	private NamingServerServiceGrpc.NamingServerServiceBlockingStub stub;

	public NamingServerService(String host, int port) {
		String target = host + ":" + port;
		channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		stub = NamingServerServiceGrpc.newBlockingStub(channel);
	}

	public LookupResponse lookup(String serviceName, String qualifier) {
		LookupRequest request = LookupRequest.newBuilder().setServiceName(serviceName).setQualifier(qualifier).build();
		LookupResponse response = stub.lookup(request);

		return response;
	}

	public void shutdown() {
		channel.shutdown();
	}
}
