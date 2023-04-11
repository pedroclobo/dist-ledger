package pt.tecnico.distledger.server.grpc;

import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.LedgerState;

import pt.tecnico.distledger.server.domain.operation.*;
import pt.tecnico.distledger.sharedutils.ServerFrontend;
import pt.tecnico.distledger.sharedutils.VectorClock;

import java.util.List;
import io.grpc.StatusRuntimeException;

public class CrossServerService {
	private ServerFrontend<DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub> frontend;

	public CrossServerService(
	    ServerFrontend<DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub> frontend) {
		this.frontend = frontend;
	}

	public void propagateState(VectorClock replicaTS, List<Operation> ledger, String qualifier) {
		try {
			DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub stub = frontend.getStub(
			    qualifier);

			LedgerState.Builder ledgerState = LedgerState.newBuilder();

			for (Operation operation : ledger) {
				ledgerState.addLedger(operation.toProtobuf());
			}

			PropagateStateRequest request = PropagateStateRequest.newBuilder()
			                                                     .setState(ledgerState)
			                                                     .setReplicaTS(replicaTS.toProtobuf())
			                                                     .build();
			stub.propagateState(request);
		} catch (StatusRuntimeException e) {
			throw e;
		}
	}
}
