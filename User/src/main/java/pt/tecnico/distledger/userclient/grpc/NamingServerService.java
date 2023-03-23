package pt.tecnico.distledger.userclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.Server;

import pt.tecnico.distledger.userclient.grpc.UserServiceStubHandler;

import pt.tecnico.distledger.userclient.exceptions.ServerNotFoundException;

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

	public LookupResponse lookup(String serviceName, String qualifier) {
		LookupRequest request = LookupRequest.newBuilder()
		                                     .setServiceName(serviceName)
		                                     .setQualifier(qualifier)
		                                     .build();
		LookupResponse response = stub.lookup(request);

		return response;
	}

	public Map<String, UserServiceStubHandler> getHandlers() {
		Map<String, UserServiceStubHandler> handlers = new HashMap<>();
		LookupResponse serverResponse = this.lookup("DistLedger", "");

		for (Server server : serverResponse.getServerList()) {
			handlers.put(server.getQualifier(),
			    new UserServiceStubHandler(server.getHost(), server.getPort()));
		}

		return handlers;
	}

	public UserServiceStubHandler getHandler(String qualifier) {
		LookupResponse serverResponse = this.lookup("DistLedger", qualifier);

		if (serverResponse.getServerCount() == 0) {
			throw new ServerNotFoundException(qualifier);
		}

		String host = serverResponse.getServer(0)
		                            .getHost();
		int port = serverResponse.getServer(0)
		                         .getPort();

		return new UserServiceStubHandler(host, port);
	}

	public void shutdown() {
		channel.shutdown();
	}
}
