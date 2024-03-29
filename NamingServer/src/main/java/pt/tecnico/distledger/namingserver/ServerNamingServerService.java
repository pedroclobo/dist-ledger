package pt.tecnico.distledger.namingserver;

import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger.*;

public class ServerNamingServerService extends NamingServerService {
	private String host;
	private int port;

	public ServerNamingServerService(String qualifier, String host, int port) {
		super();
		this.host = host;
		this.port = port;
		register("DistLedger", qualifier, host, port);
	}

	public void register(String serviceName, String qualifier, String host, int port) {
		RegisterRequest request = RegisterRequest.newBuilder()
		                                         .setServiceName(serviceName)
		                                         .setQualifier(qualifier)
		                                         .setHost(host)
		                                         .setPort(port)
		                                         .build();
		stub.register(request);
	}

	public void delete(String serviceName, String host, int port) {
		DeleteRequest request = DeleteRequest.newBuilder()
		                                     .setServiceName(serviceName)
		                                     .setHost(host)
		                                     .setPort(port)
		                                     .build();
		stub.delete(request);
	}

	@Override
	public void shutdown() {
		delete("DistLedger", host, port);
		super.shutdown();
	}
}
