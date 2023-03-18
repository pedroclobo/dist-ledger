package pt.tecnico.distledger.server;

import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.operation.*;

import io.grpc.stub.StreamObserver;
import static io.grpc.Status.INVALID_ARGUMENT;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

	private ServerState state;
	private ServerMode mode;

	public UserServiceImpl(ServerState state, ServerMode mode) {
		this.state = state;
		this.mode = mode;
	}

	@Override
	public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
		if (mode.isInactive()) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("UNAVAILABLE").asRuntimeException());
			return;
		}

		String userId = request.getUserId();

		try {
			int ammount = state.getAccountBalance(userId);

			BalanceResponse response = BalanceResponse.newBuilder().setValue(ammount).build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (RuntimeException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void createAccount(CreateAccountRequest request, StreamObserver<CreateAccountResponse> responseObserver) {
		if (mode.isInactive()) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("UNAVAILABLE").asRuntimeException());
			return;
		}

		String userId = request.getUserId();
		try {
			CreateOp operation = new CreateOp(userId);
			operation.execute(state);

			CreateAccountResponse response = CreateAccountResponse.newBuilder().build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (RuntimeException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void deleteAccount(DeleteAccountRequest request, StreamObserver<DeleteAccountResponse> responseObserver) {
		if (mode.isInactive()) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("UNAVAILABLE").asRuntimeException());
			return;
		}

		String userId = request.getUserId();

		try {
			DeleteOp operation = new DeleteOp(userId);
			operation.execute(state);

			DeleteAccountResponse response = DeleteAccountResponse.newBuilder().build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (RuntimeException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void transferTo(TransferToRequest request, StreamObserver<TransferToResponse> responseObserver) {
		if (mode.isInactive()) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription("UNAVAILABLE").asRuntimeException());
			return;
		}

		String accountFrom = request.getAccountFrom();
		String accountTo = request.getAccountTo();
		int amount = request.getAmount();

		try {
			TransferOp operation = new TransferOp(accountFrom, accountTo, amount);
			operation.execute(state);

			TransferToResponse response = TransferToResponse.newBuilder().build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (RuntimeException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}
}
