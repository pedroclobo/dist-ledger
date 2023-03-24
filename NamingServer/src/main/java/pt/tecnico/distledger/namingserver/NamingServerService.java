package pt.tecnico.distledger.namingserver;

import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.Server;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Map;
import java.util.HashMap;

public abstract class NamingServerService {

	protected final ManagedChannel channel;
	protected NamingServerServiceGrpc.NamingServerServiceBlockingStub stub;

	public NamingServerService(String host, int port) {
		String target = host + ":" + port;
		channel = ManagedChannelBuilder.forTarget(target)
		                               .usePlaintext()
		                               .build();
		stub = NamingServerServiceGrpc.newBlockingStub(channel);
	}

	public LookupResponse lookup(String serviceName, String qualifier) {
		LookupRequest request = LookupRequest.newBuilder()
		                                     .setServiceName(serviceName)
		                                     .setQualifier(qualifier)
		                                     .build();
		LookupResponse response = stub.lookup(request);

		return response;
	}

	public void shutdown() {
		channel.shutdown();
	}
}
