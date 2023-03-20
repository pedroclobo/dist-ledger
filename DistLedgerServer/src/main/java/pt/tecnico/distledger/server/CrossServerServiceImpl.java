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

		Operation operation = null;

		// TODO: convert to Operation
		for (DistLedgerCommonDefinitions.Operation op : request.getState().getLedgerList()) {
			switch (op.getType()) {
			case OP_CREATE_ACCOUNT:
				operation = new CreateOp(op.getUserId());
				break;
			case OP_DELETE_ACCOUNT:
				operation = new DeleteOp(op.getUserId());
				break;
			case OP_TRANSFER_TO:
				operation = new TransferOp(op.getUserId(), op.getDestUserId(), op.getAmount());
				break;
			case OP_UNSPECIFIED:
				break;
			default:
				break;
			}
		}
		operation.execute(state);

		PropagateStateResponse response = PropagateStateResponse.newBuilder().build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}
