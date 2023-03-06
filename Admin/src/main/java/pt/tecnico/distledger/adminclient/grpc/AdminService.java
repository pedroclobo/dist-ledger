package pt.tecnico.distledger.adminclient.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.LedgerState;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.ActivateRequest;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.DeactivateRequest;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.getLedgerStateRequest;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.getLedgerStateResponse;

public class AdminService {

	private final ManagedChannel channel;
	private AdminServiceGrpc.AdminServiceBlockingStub stub;

	public AdminService(String host, int port) {
		String target = host + ":" + port;
		channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		stub = AdminServiceGrpc.newBlockingStub(channel);
	}

	public String activate() {
		ActivateRequest request = ActivateRequest.newBuilder().build();
		stub.activate(request);

		return "OK";
	}

	public String deactivate() {
		DeactivateRequest request = DeactivateRequest.newBuilder().build();
		stub.deactivate(request);

		return "OK";
	}

	public String getLedgerState() {
		getLedgerStateRequest request = getLedgerStateRequest.newBuilder().build();
		getLedgerStateResponse response = stub.getLedgerState(request);

		return "OK%n" + response.toString();
	}

	public void shutdown() {
		channel.shutdown();
	}
}
