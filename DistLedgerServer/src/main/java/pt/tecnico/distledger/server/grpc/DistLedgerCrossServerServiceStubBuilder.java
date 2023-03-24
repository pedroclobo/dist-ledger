package pt.tecnico.distledger.server.grpc;

import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;

import pt.tecnico.distledger.sharedutils.StubBuilder;

import io.grpc.ManagedChannel;

public class DistLedgerCrossServerServiceStubBuilder extends StubBuilder {

	public DistLedgerCrossServerServiceStubBuilder() {
		super();
	}

	public DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub build(ManagedChannel channel) {
		return DistLedgerCrossServerServiceGrpc.newBlockingStub(channel);
	}
}
