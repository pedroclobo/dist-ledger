package pt.tecnico.distledger.sharedutils;

import pt.tecnico.distledger.namingserver.ClientNamingServerService;

public class ClientFrontend<T> extends Frontend<T> {
	public ClientFrontend(StubBuilder<T> stubBuilder) {
		super(stubBuilder);
	}

	@Override
	public void initNamingServer() {
		ClientNamingServerService namingServerService = new ClientNamingServerService(
		    "localhost", 5001);

		setNamingServer(namingServerService);
	}
}
