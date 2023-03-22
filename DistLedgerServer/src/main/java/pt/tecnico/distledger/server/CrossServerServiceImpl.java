package pt.tecnico.distledger.server;

import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.operation.*;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;

import java.util.ArrayList;
import java.util.List;

import io.grpc.stub.StreamObserver;
import static io.grpc.Status.INVALID_ARGUMENT;

public class CrossServerServiceImpl extends DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceImplBase {

	private ServerState state;
	private ServerMode mode;

	public CrossServerServiceImpl(ServerState state, ServerMode mode) {
		this.state = state;
		this.mode = mode;
	}

	@Override
	public void propagateState(PropagateStateRequest request, StreamObserver<PropagateStateResponse> responseObserver) {
		if (mode.isInactive()) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("UNAVAILABLE").asRuntimeException());
			return;
		}

		Operation operation = Operation.fromProtobuf(request.getState().getLedgerList().get(0));
		operation.execute(state);

		PropagateStateResponse response = PropagateStateResponse.newBuilder().build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}
