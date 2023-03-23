package pt.tecnico.distledger.adminclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.Server;

import pt.tecnico.distledger.adminclient.exceptions.ServerNotFoundException;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Map;
import java.util.HashMap;

/**
 * The NamingServerService class is responsible for communicating with the
 * Naming Server to retrieve the list of servers and their addresses.
 */
public class NamingServerService {

	private final ManagedChannel channel;
	private NamingServerServiceGrpc.NamingServerServiceBlockingStub stub;

	/**
	 * Constructs a new NamingServerService with the given host and port.
	 *
	 * @param host the host of the Naming Server
	 * @param port the port of the Naming Server
	 */
	public NamingServerService(String host, int port) {
		String target = host + ":" + port;
		channel = ManagedChannelBuilder.forTarget(target).usePlaintext()
		    .build();
		stub = NamingServerServiceGrpc.newBlockingStub(channel);
	}

	/**
	 * Performs a lookup request to the Naming Server.
	 *
	 * @param serviceName the name of the service to look up
	 * @param qualifier   the qualifier of the service to look up
	 */
	public LookupResponse lookup(String serviceName, String qualifier) {
		LookupRequest request = LookupRequest.newBuilder()
		    .setServiceName(serviceName).setQualifier(qualifier).build();
		LookupResponse response = stub.lookup(request);

		return response;
	}

	/**
	 * Performs a lookup request to the Naming Server.
	 */
	public Map<String, AdminServiceStubHandler> getHandlers() {
		Map<String, AdminServiceStubHandler> handlers = new HashMap<>();
		LookupResponse serverResponse = this.lookup("DistLedger", "");

		for (Server server : serverResponse.getServerList()) {
			handlers.put(server.getQualifier(), new AdminServiceStubHandler(
			    server.getHost(), server.getPort()));
		}

		return handlers;
	}

	/**
	 * Performs a lookup request to the Naming Server.
	 *
	 * @param qualifier the qualifier of the server to look up
	 */
	public AdminServiceStubHandler getHandler(String qualifier) {
		LookupResponse serverResponse = this.lookup("DistLedger", qualifier);

		if (serverResponse.getServerCount() == 0) {
			throw new ServerNotFoundException(qualifier);
		}

		String host = serverResponse.getServer(0).getHost();
		int port = serverResponse.getServer(0).getPort();

		return new AdminServiceStubHandler(host, port);
	}

	/**
	 * Shuts down the channel.
	 */
	public void shutdown() {
		channel.shutdown();
	}
}
