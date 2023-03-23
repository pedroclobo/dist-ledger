package pt.tecnico.distledger.userclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.LedgerState;
import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ConnectivityState;

import java.util.Map;

public class StubHandler {
	private NamingServerService namingServerService;
	private Map<String, UserServiceStubHandler> stubHandlers;

	public StubHandler(NamingServerService namingServerService) {
		this.namingServerService = namingServerService;
		this.stubHandlers = namingServerService.getHandlers();
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
		stubHandlers.replace(qualifier,
		    namingServerService.getHandler(qualifier));
	}

	public UserServiceGrpc.UserServiceBlockingStub getStub(String qualifier) {
		try {
			if (!stubHandlers.containsKey(qualifier)) {
				stubHandlers.put(qualifier,
				    namingServerService.getHandler(qualifier));

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
		            .forEach(UserServiceStubHandler::shutdown);
	}
}
