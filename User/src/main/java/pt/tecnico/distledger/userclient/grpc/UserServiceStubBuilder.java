package pt.tecnico.distledger.userclient;

import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import pt.tecnico.distledger.sharedutils.StubBuilder;

import io.grpc.ManagedChannel;

public class UserServiceStubBuilder extends StubBuilder<UserServiceGrpc.UserServiceBlockingStub> {

	public UserServiceStubBuilder() {
		super();
	}

	public UserServiceGrpc.UserServiceBlockingStub build(ManagedChannel channel) {
		return UserServiceGrpc.newBlockingStub(channel);
	}
}
