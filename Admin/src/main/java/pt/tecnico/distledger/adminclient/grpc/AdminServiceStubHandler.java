package pt.tecnico.distledger.adminclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

/**
 * The AdminServiceStubHandler class encapsulates a gRPC channel and stub for
 * the AdminService service.
 */
public class AdminServiceStubHandler {
	private ManagedChannel channel;
	private AdminServiceGrpc.AdminServiceBlockingStub stub;

	/**
	 * Constructs a new AdminServiceStubHandler.
	 *
	 * @param host the host of the server
	 * @param port the port of the server
	 */
	public AdminServiceStubHandler(String host, int port) {
		String target = host + ":" + port;
		this.channel = ManagedChannelBuilder.forTarget(target)
		                                    .usePlaintext()
		                                    .build();
		this.stub = AdminServiceGrpc.newBlockingStub(channel);
	}

	/**
	 * Returns the gRPC stub for the AdminService service.
	 */
	public AdminServiceGrpc.AdminServiceBlockingStub getStub() {
		return stub;
	}

	public ManagedChannel getChannel() {
		return channel;
	}

	/**
	 * Shuts down the gRPC channel.
	 */
	public void shutdown() {
		channel.shutdown();
	}
}
