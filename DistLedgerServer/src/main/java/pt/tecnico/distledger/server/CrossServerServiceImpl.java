package pt.tecnico.distledger.server;

import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.operation.*;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;

import java.util.ArrayList;
import java.util.List;

import io.grpc.stub.StreamObserver;

public class CrossServerServiceImpl extends DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceImplBase {

	private ServerState state;

	public CrossServerServiceImpl(ServerState state) {
		this.state = state;
	}

	@Override
	public void propagateState(PropagateStateRequest request, StreamObserver<PropagateStateResponse> responseObserver) {
		List<Operation> newLedgerState = new ArrayList<>();

		for (DistLedgerCommonDefinitions.Operation op : request.getState().getLedgerList()) {
			switch (op.getType()) {
			case OP_CREATE_ACCOUNT:
				newLedgerState.add(new CreateOp(op.getUserId()));
				break;
			case OP_DELETE_ACCOUNT:
				newLedgerState.add(new DeleteOp(op.getUserId()));
				break;
			case OP_TRANSFER_TO:
				newLedgerState.add(new TransferOp(op.getUserId(), op.getDestUserId(), op.getAmount()));
				break;
			case OP_UNSPECIFIED:
				break;
			default:
				break;
			}
		}
		this.state.setLedger(newLedgerState);

		PropagateStateResponse response = PropagateStateResponse.newBuilder().build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}
