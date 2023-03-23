package pt.tecnico.distledger.adminclient.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.ActivateRequest;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.DeactivateRequest;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.getLedgerStateRequest;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.getLedgerStateResponse;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger.LookupResponse;

import io.grpc.StatusRuntimeException;

/**
 * The AdminService class provides the gRPC stubs for the AdminService.
 */
public class AdminService {

	/**
	 * Set flag to true to print debug messages. The flag can be set using the
	 * -Ddebug command line option.
	 */
	private static final boolean DEBUG_FLAG = (System
	    .getProperty("debug") != null);

	/**
	 * Helper method to print debug messages.
	 */
	private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
	}

	private NamingServerService namingServerService;
	private StubHandler stubHandler;

	/**
	 * Constructs a new AdminService.
	 *
	 * @param namingServerService the naming server service to use
	 */
	public AdminService(NamingServerService namingServerService) {
		this.namingServerService = namingServerService;
		this.stubHandler = new StubHandler(namingServerService);
	}

	/**
	 * Activates a server.
	 *
	 * @param qualifier the qualifier of the server to activate
	 * @throws ServerNotFoundException if the server is not registered
	 */
	public String activate(String qualifier) {
		try {
			AdminServiceGrpc.AdminServiceBlockingStub stub = stubHandler
			    .getStub(qualifier);

			ActivateRequest request = ActivateRequest.newBuilder().build();
			debug("Send activate request");
			stub.activate(request);
			debug("Received activate response");

			return "OK\n";
		} catch (StatusRuntimeException e) {
			return e.getStatus().getDescription() + "\n";
		}

	}

	/**
	 * Deactivates a server.
	 *
	 * @param qualifier the qualifier of the server to deactivate
	 * @throws ServerNotFoundException if the server is not registered
	 */
	public String deactivate(String qualifier) {
		try {
			AdminServiceGrpc.AdminServiceBlockingStub stub = stubHandler
			    .getStub(qualifier);

			DeactivateRequest request = DeactivateRequest.newBuilder().build();
			debug("Send deactivate request");
			stub.deactivate(request);
			debug("Received deactivate response");

			return "OK\n";
		} catch (StatusRuntimeException e) {
			return e.getStatus().getDescription() + "\n";
		}
	}

	/**
	 * Retrives the ledger state of a server.
	 *
	 * @param qualifier the qualifier of the server
	 * @throws ServerNotFoundException if the server is not registered
	 */
	public String getLedgerState(String qualifier) {
		try {
			AdminServiceGrpc.AdminServiceBlockingStub stub = stubHandler
			    .getStub(qualifier);

			getLedgerStateRequest request = getLedgerStateRequest.newBuilder()
			    .build();
			debug("Send getLedgerState request");
			getLedgerStateResponse response = stub.getLedgerState(request);
			debug(String.format("Received getLedgerState response:%n%s",
			    response));

			return "OK\n" + response + "\n";
		} catch (StatusRuntimeException e) {
			return e.getStatus().getDescription() + "\n";
		}
	}

	/**
	 * Shuts down the AdminService. It shuts down the stub handler and the
	 * naming server service.
	 */
	public void shutdown() {
		stubHandler.shutdown();
		namingServerService.shutdown();
	}
}
