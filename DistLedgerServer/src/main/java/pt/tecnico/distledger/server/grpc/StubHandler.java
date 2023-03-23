package pt.tecnico.distledger.server.grpc;

import pt.tecnico.distledger.server.domain.operation.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.LedgerState;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;

import io.grpc.ManagedChannel;
import io.grpc.ConnectivityState;

import java.util.Map;

public class StubHandler {
	private NamingServerService namingServerService;
	private Map<String, DistLedgerCrossServerServiceStubHandler> stubHandlers;

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

	public DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub getStub(
	    String qualifier) {
		if (!stubHandlers.containsKey(qualifier)) {
			stubHandlers.put(qualifier,
			    namingServerService.getHandler(qualifier));

		} else if (shouldReplaceChannel(qualifier)) {
			replaceChannel(qualifier);
		}

		return stubHandlers.get(qualifier)
		                   .getStub();
	}

	public void shutdown() {
		stubHandlers.values()
		            .forEach(DistLedgerCrossServerServiceStubHandler::shutdown);
	}
}
