package pt.tecnico.distledger.server;

import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.operation.*;
import pt.tecnico.distledger.server.grpc.CrossServerService;

import pt.tecnico.distledger.server.exceptions.ServerUnavailableException;
import pt.tecnico.distledger.server.exceptions.ReadOnlyServerException;

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
				                                          .setValueTS(timestamp.getValueTS().toProtobuf())
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
	    StreamObserver<CreateAccountResponse> responseObserver) {
		String userId = request.getUserId();

		try {
			checkIfInactive();

			// Increment ReplicaTS
			timestamp.incrementReplicaTS();

			// Set operationTS
			CreateOp operation = new CreateOp(userId);
			operation.setTS(timestamp.getReplicaTS());

			// Reply to user
			CreateAccountResponse response = CreateAccountResponse.newBuilder()
			                                                      .setTS(timestamp.getReplicaTS()
			                                                                      .toProtobuf())
			                                                      .build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();

			state.addOperationToLedger(operation);

		} catch (RuntimeException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage())
			                                         .asRuntimeException());
		}
	}

	@Override
	public synchronized void transferTo(TransferToRequest request,
	    StreamObserver<TransferToResponse> responseObserver) {
		String accountFrom = request.getAccountFrom();
		String accountTo = request.getAccountTo();
		int amount = request.getAmount();

		try {
			checkIfInactive();

			// Increment ReplicaTS
			timestamp.incrementReplicaTS();

			// Set OperationTS
			TransferOp operation = new TransferOp(accountFrom, accountTo, amount);
			operation.setTS(timestamp.getReplicaTS());

			// Reply to user
			TransferToResponse response = TransferToResponse.newBuilder()
			                                                .setTS(timestamp.getReplicaTS()
			                                                                .toProtobuf())
			                                                .build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();

			state.addOperationToLedger(operation);

		} catch (RuntimeException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage())
			                                         .asRuntimeException());
		}
	}
}
