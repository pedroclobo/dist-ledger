package pt.tecnico.distledger.adminclient.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.ActivateRequest;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.DeactivateRequest;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.getLedgerStateRequest;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.getLedgerStateResponse;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger.LookupResponse;

import java.util.Map;
import java.util.HashMap;

public class AdminService {

	/**
	 * Set flag to true to print debug messages. The flag can be set using the
	 * -Ddebug command line option.
	 */
	private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

	/** Helper method to print debug messages. */
	private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
	}

	private NamingServerService namingServerService;
	private Map<String, AdminServiceStubHandler> stubHandlers;

	public AdminService(NamingServerService namingServerService) {
		this.namingServerService = namingServerService;
		this.stubHandlers = namingServerService.getHandlers();
	}

	public String activate(String qualifier) {
		AdminServiceGrpc.AdminServiceBlockingStub stub = stubHandlers.get(qualifier).getStub();

		ActivateRequest request = ActivateRequest.newBuilder().build();
		debug("Send activate request");
		stub.activate(request);
		debug("Received activate response");

		return "OK\n";
	}

	public String deactivate(String qualifier) {
		AdminServiceGrpc.AdminServiceBlockingStub stub = stubHandlers.get(qualifier).getStub();

		DeactivateRequest request = DeactivateRequest.newBuilder().build();
		debug("Send deactivate request");
		stub.deactivate(request);
		debug("Received deactivate response");

		return "OK\n";
	}

	public String getLedgerState(String qualifier) {
		AdminServiceGrpc.AdminServiceBlockingStub stub = stubHandlers.get(qualifier).getStub();

		getLedgerStateRequest request = getLedgerStateRequest.newBuilder().build();
		debug("Send getLedgerState request");
		getLedgerStateResponse response = stub.getLedgerState(request);
		debug(String.format("Received getLedgerState response:%n%s", response));

		return "OK\n" + response + "\n";
	}

	public void shutdown() {
		stubHandlers.values().forEach(AdminServiceStubHandler::shutdown);
		namingServerService.shutdown();
	}
}
