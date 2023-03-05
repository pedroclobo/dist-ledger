package pt.tecnico.distledger.userclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class UserService {

	private final ManagedChannel channel;
	private UserServiceGrpc.UserServiceBlockingStub stub;

	public UserService(String host, int port) {
		String target = host + ":" + port;
		channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		stub = UserServiceGrpc.newBlockingStub(channel);
	}

	public int balance(String account) {
		BalanceRequest request = BalanceRequest.newBuilder().setUserId(account).build();
		BalanceResponse response = stub.balance(request);

		return response.getValue();
	}

	public String createAccount(String account) {
		CreateAccountRequest request = CreateAccountRequest.newBuilder().setUserId(account).build();
		CreateAccountResponse response = stub.createAccount(request);

		return "OK";
	}

	public String deleteAccount(String account) {
		DeleteAccountRequest request = DeleteAccountRequest.newBuilder().setUserId(account).build();
		DeleteAccountResponse response = stub.deleteAccount(request);

		return "OK";
	}

	public String transferTo(String accountFrom, String accountTo, int amount) {
		TransferToRequest request = TransferToRequest.newBuilder().
			setAccountFrom(accountFrom).
			setAccountTo(accountTo).
			setAmount(amount).
			build();

		TransferToResponse response = stub.transferTo(request);

		return "OK";
	}
}
