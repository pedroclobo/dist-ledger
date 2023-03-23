package pt.tecnico.distledger.server.grpc;

import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.Server;

import pt.tecnico.distledger.server.exceptions.ServerNotFoundException;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Map;
import java.util.HashMap;

public class NamingServerService {

	private final ManagedChannel channel;
	private NamingServerServiceGrpc.NamingServerServiceBlockingStub stub;

	public NamingServerService(String host, int port) {
		String target = host + ":" + port;
		channel = ManagedChannelBuilder.forTarget(target)
		                               .usePlaintext()
		                               .build();
		stub = NamingServerServiceGrpc.newBlockingStub(channel);
	}

	public void register(String serviceName, String qualifier, String host,
	    int port) {
		RegisterRequest request = RegisterRequest.newBuilder()
		                                         .setServiceName(serviceName)
		                                         .setQualifier(qualifier)
		                                         .setHost(host)
		                                         .setPort(port)
		                                         .build();
		RegisterResponse response = stub.register(request);
	}

	public LookupResponse lookup(String serviceName, String qualifier) {
		LookupRequest request = LookupRequest.newBuilder()
		                                     .setServiceName(serviceName)
		                                     .setQualifier(qualifier)
		                                     .build();
		LookupResponse response = stub.lookup(request);

		return response;
	}

	public Map<String, DistLedgerCrossServerServiceStubHandler> getHandlers() {
		Map<String, DistLedgerCrossServerServiceStubHandler> handlers = new HashMap<>();
		LookupResponse serverResponse = this.lookup("DistLedger", "");

		for (Server server : serverResponse.getServerList()) {
			handlers.put(server.getQualifier(),
			    new DistLedgerCrossServerServiceStubHandler(server.getHost(),
			        server.getPort()));
		}

		return handlers;
	}

	public DistLedgerCrossServerServiceStubHandler getHandler(
	    String qualifier) {
		LookupResponse serverResponse = this.lookup("DistLedger", qualifier);

		if (serverResponse.getServerCount() == 0) {
			throw new ServerNotFoundException(qualifier);
		}

		String host = serverResponse.getServer(0)
		                            .getHost();
		int port = serverResponse.getServer(0)
		                         .getPort();

		return new DistLedgerCrossServerServiceStubHandler(host, port);
	}

	public void delete(String serviceName, String host, int port) {
		DeleteRequest request = DeleteRequest.newBuilder()
		                                     .setServiceName(serviceName)
		                                     .setHost(host)
		                                     .setPort(port)
		                                     .build();
		DeleteResponse response = stub.delete(request);
	}

	public void shutdown() {
		channel.shutdown();
	}
}
