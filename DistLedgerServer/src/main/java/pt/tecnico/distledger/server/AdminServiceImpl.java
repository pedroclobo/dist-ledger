package pt.tecnico.distledger.server;

import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.Operation;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.operation.*;
import pt.tecnico.distledger.server.ServerMode;
import pt.tecnico.distledger.server.ServerMode.Mode;
// import pt.tecnico.distledger.server.domain.operation.Operation;

import io.grpc.stub.StreamObserver;

public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {

	private ServerState state;
	private ServerMode mode;

	public AdminServiceImpl(ServerState state, ServerMode mode) {
		this.state = state;
		this.mode = mode;
	}

	@Override
	public void activate(ActivateRequest request, StreamObserver<ActivateResponse> responseObserver) {
		mode.activate();

		ActivateResponse response = ActivateResponse.newBuilder().build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void deactivate(DeactivateRequest request, StreamObserver<DeactivateResponse> responseObserver) {
		mode.deactivate();

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

			switch (op.getType()) {
			case "CreateOp":
				operation.setType(OperationType.OP_CREATE_ACCOUNT).setUserId(op.getAccount());
				break;
			case "DeleteOp":
				operation.setType(OperationType.OP_DELETE_ACCOUNT).setUserId(op.getAccount());
				break;
			case "TransferOp":
				operation.setType(OperationType.OP_TRANSFER_TO).setUserId(op.getAccount()).setDestUserId(((TransferOp) op).getDestAccount()).setAmount(((TransferOp) op).getAmount());
				break;
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
