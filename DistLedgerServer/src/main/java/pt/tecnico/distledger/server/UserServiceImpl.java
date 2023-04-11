package pt.tecnico.distledger.server;

import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.operation.*;
import pt.tecnico.distledger.server.grpc.CrossServerService;

import pt.tecnico.distledger.server.exceptions.ServerUnavailableException;

import pt.tecnico.distledger.sharedutils.VectorClock;

import io.grpc.stub.StreamObserver;
import static io.grpc.Status.INVALID_ARGUMENT;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

	private ServerState state;
	private ServerMode mode;
	private ServerTimestamp timestamp;
	private CrossServerService crossServerService;

	public UserServiceImpl(ServerState state, ServerMode mode, ServerTimestamp timestamp,
	    CrossServerService crossServerService) {
		this.state = state;
		this.mode = mode;
		this.timestamp = timestamp;
		this.crossServerService = crossServerService;
	}

	private void checkIfInactive() {
		if (mode.isInactive()) {
			throw new ServerUnavailableException();
		}
	}

	@Override
	public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
		String userId = request.getUserId();

		try {
			checkIfInactive();

			VectorClock prev = VectorClock.fromProtobuf(request.getPrevTS());

			if (timestamp.getValueTS()
			             .GE(prev)) {

				int amount = state.getAccountBalance(userId);

				BalanceResponse response = BalanceResponse.newBuilder()
				                                          .setValue(amount)
				                                          .setValueTS(timestamp.getValueTS()
				                                                               .toProtobuf())
				                                          .build();
				responseObserver.onNext(response);
				responseObserver.onCompleted();
			} else {
				responseObserver.onError(INVALID_ARGUMENT.withDescription("ERROR: You are too up to date!")
				                                         .asRuntimeException());
			}

		} catch (RuntimeException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage())
			                                         .asRuntimeException());
		}
	}

	@Override
	public synchronized void createAccount(CreateAccountRequest request,
	    StreamObserver<UpdateOperationResponse> responseObserver) {

		String userId = request.getUserId();
		VectorClock prev = VectorClock.fromProtobuf(request.getPrevTS());

		try {
			checkIfInactive();

			// Increment ReplicaTS
			timestamp.incrementReplicaTS();

			// Create operationTS
			VectorClock operationTS = new VectorClock(prev);
			operationTS.mergeOnIdx(timestamp.getReplicaTS(), timestamp.getServerIdx());

			// Reply to user
			UpdateOperationResponse response = UpdateOperationResponse.newBuilder()
			                                                          .setTS(operationTS.toProtobuf())
			                                                          .build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();

			// Create the new Operation
			CreateOp op = new CreateOp(userId);
			op.setPrev(prev);
			op.setTS(operationTS);

			// Add op to Ledger and try to execute it
			state.addOperationToLedger(op);

		} catch (RuntimeException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage())
			                                         .asRuntimeException());
		}
	}

	@Override
	public synchronized void transferTo(TransferToRequest request,
	    StreamObserver<UpdateOperationResponse> responseObserver) {

		String accountFrom = request.getAccountFrom();
		String accountTo = request.getAccountTo();
		int amount = request.getAmount();
		VectorClock prev = VectorClock.fromProtobuf(request.getPrevTS());

		try {
			checkIfInactive();

			// Increment ReplicaTS
			timestamp.incrementReplicaTS();

			// Create operationTS
			VectorClock operationTS = new VectorClock(prev);
			operationTS.mergeOnIdx(timestamp.getReplicaTS(), timestamp.getServerIdx());

			// Reply to user
			UpdateOperationResponse response = UpdateOperationResponse.newBuilder()
			                                                          .setTS(operationTS.toProtobuf())
			                                                          .build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();

			// Create the new Operation
			TransferOp op = new TransferOp(accountFrom, accountTo, amount);
			op.setPrev(prev);
			op.setTS(operationTS);

			// Add op to Ledger and try to execute it
			state.addOperationToLedger(op);

		} catch (RuntimeException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage())
			                                         .asRuntimeException());
		}
	}
}
