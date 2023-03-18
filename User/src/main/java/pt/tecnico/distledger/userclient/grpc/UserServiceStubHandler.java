package pt.tecnico.distledger.userclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class UserServiceStubHandler implements AutoCloseable {
	private ManagedChannel channel;
	private UserServiceGrpc.UserServiceBlockingStub stub;

	public UserServiceStubHandler(String host, int port) {
		String target = host + ":" + port;
		this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		this.stub = UserServiceGrpc.newBlockingStub(channel);
	}

	public UserServiceGrpc.UserServiceBlockingStub getStub() {
		return stub;
	}

	@Override
	public void close() throws Exception {
		channel.shutdown();
	}
}
