package pt.tecnico.distledger.server.grpc;

import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.LedgerState;

import pt.tecnico.distledger.server.domain.operation.*;
import pt.tecnico.distledger.sharedutils.ServerFrontend;

import io.grpc.StatusRuntimeException;

public class CrossServerService {
	private ServerFrontend<DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub> frontend;

	public CrossServerService(
	    ServerFrontend<DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub> frontend) {
		this.frontend = frontend;
	}

	public void propagateState(Operation operation) {
		try {
			DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub stub = frontend.getStub("B");

			LedgerState.Builder ledgerState = LedgerState.newBuilder();
			ledgerState.addLedger(operation.toProtobuf());
			PropagateStateRequest request = PropagateStateRequest.newBuilder()
			                                                     .setState(ledgerState)
			                                                     .build();
			stub.propagateState(request);
		} catch (StatusRuntimeException e) {
			throw e;
		}
	}
}
