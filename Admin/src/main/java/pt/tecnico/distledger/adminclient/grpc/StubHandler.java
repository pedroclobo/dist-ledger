package pt.tecnico.distledger.adminclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.LedgerState;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ConnectivityState;

import java.util.Map;

/**
 * The StubHandler class encapsulates a gRPC channel and stub for the
 * AdminService. It also handles the creation of new channels and stubs when
 * necessary.
 */
public class StubHandler {
	private NamingServerService namingServerService;
	private Map<String, AdminServiceStubHandler> stubHandlers;

	/**
	 * Constructs a new StubHandler.
	 *
	 * @param namingServerService the naming server service
	 */
	public StubHandler(NamingServerService namingServerService) {
		this.namingServerService = namingServerService;
		this.stubHandlers = namingServerService.getHandlers();
	}

	/**
	 * Checks if the channel for the given server qualifier should be replaced.
	 *
	 * @param qualifier the server qualifier
	 */
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

	/**
	 * Replaces the channel for the given server qualifier.
	 *
	 * @param qualifier the server qualifier
	 */
	private void replaceChannel(String qualifier) {
		if (!stubHandlers.containsKey(qualifier)) {
			return;
		}

		stubHandlers.get(qualifier)
		            .shutdown();
		stubHandlers.replace(qualifier,
		    namingServerService.getHandler(qualifier));
	}

	/**
	 * Returns the gRPC stub for the AdminService service.
	 *
	 * @param qualifier the server qualifier
	 * @throws ServerNotFoundException if the server is not available
	 */
	public AdminServiceGrpc.AdminServiceBlockingStub getStub(String qualifier) {
		try {
			if (!stubHandlers.containsKey(qualifier)) {
				stubHandlers.put(qualifier,
				    namingServerService.getHandler(qualifier));
			} else if (shouldReplaceChannel(qualifier)) {
				replaceChannel(qualifier);
			}

			return stubHandlers.get(qualifier).getStub();
		} catch (RuntimeException e) {
			throw e;
		}
	}

	/**
	 * Shuts down the gRPC channels.
	 */
	public void shutdown() {
		stubHandlers.values().forEach(AdminServiceStubHandler::shutdown);
	}
}
