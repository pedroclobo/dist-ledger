package pt.tecnico.distledger.server;

import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.operation.*;

import io.grpc.stub.StreamObserver;
import static io.grpc.Status.INVALID_ARGUMENT;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

	private ServerState state;

    public UserServiceImpl(ServerState state) {
        this.state = state;
    }

	@Override
	public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
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
