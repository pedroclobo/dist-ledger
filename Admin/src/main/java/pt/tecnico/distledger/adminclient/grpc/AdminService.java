package pt.tecnico.distledger.adminclient.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.ActivateRequest;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.DeactivateRequest;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.getLedgerStateRequest;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.getLedgerStateResponse;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger.LookupResponse;

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

	public AdminService(NamingServerService namingServerService) {
		this.namingServerService = namingServerService;
	}

	public String activate(String qualifier) {
		LookupResponse serverResponse = namingServerService.lookup("DistLedger", qualifier);
		String target = serverResponse.getServer(0).getHost() + ":" + serverResponse.getServer(0).getPort();

		ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		AdminServiceGrpc.AdminServiceBlockingStub stub = AdminServiceGrpc.newBlockingStub(channel);

		ActivateRequest request = ActivateRequest.newBuilder().build();
		debug("Send activate request");
		stub.activate(request);
		debug("Received activate response");

		channel.shutdown();

		return "OK\n";
	}

	public String deactivate(String qualifier) {
		LookupResponse serverResponse = namingServerService.lookup("DistLedger", qualifier);
		String target = serverResponse.getServer(0).getHost() + ":" + serverResponse.getServer(0).getPort();

		ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		AdminServiceGrpc.AdminServiceBlockingStub stub = AdminServiceGrpc.newBlockingStub(channel);

		DeactivateRequest request = DeactivateRequest.newBuilder().build();
		debug("Send deactivate request");
		stub.deactivate(request);
		debug("Received deactivate response");

		channel.shutdown();

		return "OK\n";
	}

	public String getLedgerState(String qualifier) {
		LookupResponse serverResponse = namingServerService.lookup("DistLedger", qualifier);
		String target = serverResponse.getServer(0).getHost() + ":" + serverResponse.getServer(0).getPort();

		ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		AdminServiceGrpc.AdminServiceBlockingStub stub = AdminServiceGrpc.newBlockingStub(channel);

		getLedgerStateRequest request = getLedgerStateRequest.newBuilder().build();
		debug("Send getLedgerState request");
		getLedgerStateResponse response = stub.getLedgerState(request);
		debug(String.format("Received getLedgerState response:%n%s", response));

		channel.shutdown();

		return "OK\n" + response + "\n";
	}

	public void shutdown() {
		namingServerService.shutdown();
	}
}
