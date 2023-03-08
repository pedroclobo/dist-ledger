package pt.tecnico.distledger.server;

import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.Operation;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.ServerState.ServerMode;
import pt.tecnico.distledger.server.domain.operation.*;
// import pt.tecnico.distledger.server.domain.operation.Operation;

import io.grpc.stub.StreamObserver;

public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {

	private ServerState state;

	public AdminServiceImpl(ServerState state) {
		this.state = state;
	}

	@Override
	public void activate(ActivateRequest request, StreamObserver<ActivateResponse> responseObserver) {
		state.setServerMode(ServerMode.ACTIVE);

		ActivateResponse response = ActivateResponse.newBuilder().build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void deactivate(DeactivateRequest request, StreamObserver<DeactivateResponse> responseObserver) {
		state.setServerMode(ServerMode.INACTIVE);

		DeactivateResponse response = DeactivateResponse.newBuilder().build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void getLedgerState(getLedgerStateRequest request, StreamObserver<getLedgerStateResponse> responseObserver) {
		getLedgerStateResponse.Builder responseBuilder = getLedgerStateResponse.newBuilder();
		LedgerState.Builder ledgerBuilder = LedgerState.newBuilder();

		for (pt.tecnico.distledger.server.domain.operation.Operation op : state.getLedger()) {
			Operation.Builder operation = Operation.newBuilder();

			if (op instanceof CreateOp) {
				operation.setType(OperationType.OP_CREATE_ACCOUNT).setUserId(op.getAccount());
			} else if (op instanceof DeleteOp) {
				operation.setType(OperationType.OP_DELETE_ACCOUNT).setUserId(op.getAccount());
			} else if (op instanceof TransferOp) {
				operation.setType(OperationType.OP_TRANSFER_TO).setUserId(op.getAccount()).setDestUserId(((TransferOp) op).getDestAccount()).setAmount(((TransferOp) op).getAmount());
			}
			ledgerBuilder.addLedger(operation);
		}

		getLedgerStateResponse response = responseBuilder.setLedgerState(ledgerBuilder).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void gossip(GossipRequest request, StreamObserver<GossipResponse> responseObserver) {
		// TODO: implement gossip service (only for Phase-3)
	}
}
