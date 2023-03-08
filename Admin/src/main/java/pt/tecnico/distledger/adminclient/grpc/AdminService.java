package pt.tecnico.distledger.adminclient.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.ActivateRequest;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.DeactivateRequest;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.getLedgerStateRequest;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.getLedgerStateResponse;

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

	private final ManagedChannel channel;
	private AdminServiceGrpc.AdminServiceBlockingStub stub;

	public AdminService(String host, int port) {
		String target = host + ":" + port;
		channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		stub = AdminServiceGrpc.newBlockingStub(channel);
	}

	public String activate() {
		ActivateRequest request = ActivateRequest.newBuilder().build();
		debug("Send activate request");
		stub.activate(request);
		debug("Received activate response");

		return "OK";
	}

	public String deactivate() {
		DeactivateRequest request = DeactivateRequest.newBuilder().build();
		debug("Send deactivate request");
		stub.deactivate(request);
		debug("Received deactivate response");

		return "OK";
	}

	public String getLedgerState() {
		getLedgerStateRequest request = getLedgerStateRequest.newBuilder().build();
		debug("Send getLedgerState request");
		getLedgerStateResponse response = stub.getLedgerState(request);
		debug(String.format("Received getLedgerState response:%n%s", response));

		return "OK%n" + response;
	}

	public void shutdown() {
		channel.shutdown();
	}
}
