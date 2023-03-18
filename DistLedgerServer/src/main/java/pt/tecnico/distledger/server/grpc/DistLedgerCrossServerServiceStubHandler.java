package pt.tecnico.distledger.server.grpc;

import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class DistLedgerCrossServerServiceStubHandler implements AutoCloseable {
	private ManagedChannel channel;
	private DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub stub;

	public DistLedgerCrossServerServiceStubHandler(String host, int port) {
		String target = host + ":" + port;
		this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		this.stub = DistLedgerCrossServerServiceGrpc.newBlockingStub(channel);
	}

	public DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub getStub() {
		return stub;
	}

	@Override
	public void close() throws Exception {
		channel.shutdown();
	}
}
