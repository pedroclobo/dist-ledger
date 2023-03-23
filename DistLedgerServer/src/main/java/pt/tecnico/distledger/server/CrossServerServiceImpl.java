package pt.tecnico.distledger.server;

import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.operation.*;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;

import pt.tecnico.distledger.server.exceptions.ServerUnavailableException;

import java.util.ArrayList;
import java.util.List;

import io.grpc.stub.StreamObserver;
import static io.grpc.Status.INVALID_ARGUMENT;

public class CrossServerServiceImpl extends
    DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceImplBase {

	private ServerState state;
	private ServerMode mode;

	public CrossServerServiceImpl(ServerState state, ServerMode mode) {
		this.state = state;
		this.mode = mode;
	}

	private void checkIfInactive() {
		if (mode.isInactive()) {
			throw new ServerUnavailableException();
		}
	}

	@Override
	public void propagateState(PropagateStateRequest request,
	    StreamObserver<PropagateStateResponse> responseObserver) {
		try {
			checkIfInactive();

			Operation operation = Operation.fromProtobuf(request.getState()
			                                                    .getLedgerList()
			                                                    .get(0));
			operation.execute(state);

			PropagateStateResponse response = PropagateStateResponse.newBuilder()
			                                                        .build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (RuntimeException e) {
			responseObserver.onError(
			    INVALID_ARGUMENT.withDescription(e.getMessage())
			                    .asRuntimeException());
		}
	}
}
