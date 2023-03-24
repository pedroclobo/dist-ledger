package pt.tecnico.distledger.sharedutils;

import pt.tecnico.distledger.namingserver.ServerNamingServerService;

public class ServerFrontend<T> extends Frontend<T> {
	public ServerFrontend(String qualifier, String host, int port, StubBuilder<T> stubBuilder) {
		super(qualifier, host, port, stubBuilder);
	}

	@Override
	public void initNamingServer(String qualifier, String host, int port) {
		ServerNamingServerService namingServerService = new ServerNamingServerService(qualifier, host, port);

		setNamingServer(namingServerService);
	}
}
