package pt.tecnico.distledger.sharedutils;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.*;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger.*;

import pt.tecnico.distledger.namingserver.NamingServerService;
import pt.tecnico.distledger.sharedutils.exceptions.ServerNotFoundException;

import io.grpc.ConnectivityState;

import java.util.Map;
import java.util.HashMap;

public abstract class Frontend<T> {
	private NamingServerService namingServerService;
	private Map<String, StubHandler<T>> stubHandlers;
	private StubBuilder<T> stubBuilder;

	public Frontend(StubBuilder<T> stubBuilder) {
		this.initNamingServer();
		this.stubBuilder = stubBuilder;
		this.stubHandlers = this.getHandlers();
		System.out.println(this.stubBuilder != null);
	}

	public void setNamingServer(NamingServerService namingServerService) {
		this.namingServerService = namingServerService;
	}

	public abstract void initNamingServer();

	public Map<String, StubHandler<T>> getHandlers() {
		Map<String, StubHandler<T>> handlers = new HashMap<>();
		LookupResponse serverResponse = namingServerService.lookup("DistLedger",
		    "");

		for (Server server : serverResponse.getServerList()) {
			handlers.put(server.getQualifier(), new StubHandler<T>(
			    server.getHost(), server.getPort(), this.stubBuilder));
		}

		return handlers;
	}

	public StubHandler<T> getHandler(String qualifier) {
		LookupResponse serverResponse = namingServerService.lookup("DistLedger",
		    qualifier);

		if (serverResponse.getServerCount() == 0) {
			throw new ServerNotFoundException(qualifier);
		}

		String host = serverResponse.getServer(0)
		                            .getHost();
		int port = serverResponse.getServer(0)
		                         .getPort();

		return new StubHandler<T>(host, port, this.stubBuilder);
	}

	private boolean shouldReplaceChannel(String qualifier) {
		if (!stubHandlers.containsKey(qualifier)) {
			return true;
		}

		// This is done twice to refresh the channel state.
		ConnectivityState state = stubHandlers.get(qualifier)
		                                      .getChannel()
		                                      .getState(true);
		state = stubHandlers.get(qualifier)
		                    .getChannel()
		                    .getState(true);

		return state == ConnectivityState.TRANSIENT_FAILURE
		    || state == ConnectivityState.CONNECTING
		    || state == ConnectivityState.SHUTDOWN;
	}

	private void replaceChannel(String qualifier) {
		if (!stubHandlers.containsKey(qualifier)) {
			return;
		}

		stubHandlers.get(qualifier)
		            .shutdown();
		stubHandlers.replace(qualifier, this.getHandler(qualifier));
	}

	public T getStub(String qualifier) {
		try {
			if (!stubHandlers.containsKey(qualifier)) {
				stubHandlers.put(qualifier, this.getHandler(qualifier));

			} else if (shouldReplaceChannel(qualifier)) {
				replaceChannel(qualifier);
			}

			return stubHandlers.get(qualifier)
			                   .getStub();
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public void shutdown() {
		stubHandlers.values()
		            .forEach(StubHandler::shutdown);
		namingServerService.shutdown();
	}
}
