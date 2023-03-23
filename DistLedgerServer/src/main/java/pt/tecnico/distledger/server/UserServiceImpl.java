package pt.tecnico.distledger.server;

import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.operation.*;
import pt.tecnico.distledger.server.grpc.CrossServerService;

import pt.tecnico.distledger.server.exceptions.ServerUnavailableException;
import pt.tecnico.distledger.server.exceptions.ReadOnlyServerException;

import io.grpc.stub.StreamObserver;
import static io.grpc.Status.INVALID_ARGUMENT;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

	private ServerState state;
	private ServerMode mode;
	private ServerRole role;
	private CrossServerService crossServerService;

	public UserServiceImpl(ServerState state, ServerMode mode, ServerRole role,
	    CrossServerService crossServerService) {
		this.state = state;
		this.mode = mode;
		this.role = role;
		this.crossServerService = crossServerService;
	}

	private void checkIfInactive() {
		if (mode.isInactive()) {
			throw new ServerUnavailableException();
		}
	}

	private void checkIfSecondary() {
		if (!role.isPrimary()) {
			throw new ReadOnlyServerException();
		}
	}

	@Override
	public void balance(BalanceRequest request,
	    StreamObserver<BalanceResponse> responseObserver) {
		String userId = request.getUserId();

		try {
			checkIfInactive();

			int ammount = state.getAccountBalance(userId);

			BalanceResponse response = BalanceResponse.newBuilder()
			                                          .setValue(ammount)
			                                          .build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (RuntimeException e) {
			responseObserver.onError(
			    INVALID_ARGUMENT.withDescription(e.getMessage())
			                    .asRuntimeException());
		}
	}

	@Override
	public void createAccount(CreateAccountRequest request,
	    StreamObserver<CreateAccountResponse> responseObserver) {
		String userId = request.getUserId();

		try {
			checkIfSecondary();
			checkIfInactive();

			CreateOp operation = new CreateOp(userId);
			// propagate to other servers
			crossServerService.propagateState(operation);

			operation.execute(state);

			CreateAccountResponse response = CreateAccountResponse.newBuilder()
			                                                      .build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (RuntimeException e) {
			responseObserver.onError(
			    INVALID_ARGUMENT.withDescription(e.getMessage())
			                    .asRuntimeException());
		}
	}

	@Override
	public void deleteAccount(DeleteAccountRequest request,
	    StreamObserver<DeleteAccountResponse> responseObserver) {
		String userId = request.getUserId();

		try {
			checkIfSecondary();
			checkIfInactive();

			DeleteOp operation = new DeleteOp(userId);
			// propagate to other servers
			crossServerService.propagateState(operation);

			operation.execute(state);

			DeleteAccountResponse response = DeleteAccountResponse.newBuilder()
			                                                      .build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (RuntimeException e) {
			responseObserver.onError(
			    INVALID_ARGUMENT.withDescription(e.getMessage())
			                    .asRuntimeException());
		}
	}

	@Override
	public void transferTo(TransferToRequest request,
	    StreamObserver<TransferToResponse> responseObserver) {
		String accountFrom = request.getAccountFrom();
		String accountTo = request.getAccountTo();
		int amount = request.getAmount();

		try {
			checkIfSecondary();
			checkIfInactive();

			TransferOp operation = new TransferOp(accountFrom, accountTo,
			    amount);
			// propagate to other servers
			crossServerService.propagateState(operation);

			operation.execute(state);

			TransferToResponse response = TransferToResponse.newBuilder()
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
