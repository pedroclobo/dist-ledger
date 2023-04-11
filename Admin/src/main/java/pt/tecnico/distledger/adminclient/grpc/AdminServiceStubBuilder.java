package pt.tecnico.distledger.adminclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;

import pt.tecnico.distledger.sharedutils.StubBuilder;

import io.grpc.ManagedChannel;

public class AdminServiceStubBuilder extends StubBuilder<AdminServiceGrpc.AdminServiceBlockingStub> {

	public AdminServiceStubBuilder() {
		super();
	}

	public AdminServiceGrpc.AdminServiceBlockingStub build(ManagedChannel channel) {
		return AdminServiceGrpc.newBlockingStub(channel);
	}
}
