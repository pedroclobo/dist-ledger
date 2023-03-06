package pt.tecnico.distledger.userclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class UserService {

	private final ManagedChannel channel;
	private UserServiceGrpc.UserServiceBlockingStub stub;

	public UserService(String host, int port) {
		String target = host + ":" + port;
		channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		stub = UserServiceGrpc.newBlockingStub(channel);
	}

	public String balance(String account) {
		try {
			BalanceRequest request = BalanceRequest.newBuilder().setUserId(account).build();
			BalanceResponse response = stub.balance(request);

			return String.valueOf(response.getValue());
		} catch (StatusRuntimeException e) {
			return e.getStatus().getDescription();
		}
	}

	public String createAccount(String account) {
		try {
			CreateAccountRequest request = CreateAccountRequest.newBuilder().setUserId(account).build();
			CreateAccountResponse response = stub.createAccount(request);
		} catch (StatusRuntimeException e) {
			return e.getStatus().getDescription();
		}

		return "OK";
	}

	public String deleteAccount(String account) {
		try {
			DeleteAccountRequest request = DeleteAccountRequest.newBuilder().setUserId(account).build();
			DeleteAccountResponse response = stub.deleteAccount(request);
		} catch (StatusRuntimeException e) {
			return e.getStatus().getDescription();
		}

		return "OK";
	}

	public String transferTo(String accountFrom, String accountTo, int amount) {
		try {
			TransferToRequest request = TransferToRequest.newBuilder().
				setAccountFrom(accountFrom).
				setAccountTo(accountTo).
				setAmount(amount).
				build();

			TransferToResponse response = stub.transferTo(request);
		} catch (StatusRuntimeException e) {
			return e.getStatus().getDescription();
		}

		return "OK";
	}
}
