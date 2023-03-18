package pt.tecnico.distledger.adminclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class AdminServiceStubHandler implements AutoCloseable {
	private ManagedChannel channel;
	private AdminServiceGrpc.AdminServiceBlockingStub stub;

	public AdminServiceStubHandler(String host, int port) {
		String target = host + ":" + port;
		this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		this.stub = AdminServiceGrpc.newBlockingStub(channel);
	}

	public AdminServiceGrpc.AdminServiceBlockingStub getStub() {
		return stub;
	}

	@Override
	public void close() throws Exception {
		channel.shutdown();
	}
}
