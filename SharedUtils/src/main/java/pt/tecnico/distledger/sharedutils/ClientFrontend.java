package pt.tecnico.distledger.sharedutils;

import pt.tecnico.distledger.namingserver.ClientNamingServerService;

public class ClientFrontend<T> extends Frontend<T> {
	public ClientFrontend(StubBuilder<T> stubBuilder) {
		super(null, null, 0, stubBuilder);
	}

	@Override
	public void initNamingServer(String qualifier, String host, int port) {
		ClientNamingServerService namingServerService = new ClientNamingServerService();

		setNamingServer(namingServerService);
	}
}
