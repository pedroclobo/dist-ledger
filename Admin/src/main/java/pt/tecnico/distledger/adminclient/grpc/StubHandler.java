package pt.tecnico.distledger.adminclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.LedgerState;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;

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
