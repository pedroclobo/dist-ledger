package pt.tecnico.distledger.userclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class UserService {

	/**
	 * Set flag to true to print debug messages. The flag can be set using the
	 * -Ddebug command line option.
	 */
	private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

	/** Helper method to print debug messages. */
	private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
	}

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
			debug("Send balance request");
			BalanceResponse response = stub.balance(request);
			debug(String.format("Received balance response: %s", response));

			return "OK\n" + response;
		} catch (StatusRuntimeException e) {
			return e.getStatus().getDescription() + "\n";
		}
	}

	public String createAccount(String account) {
		try {
			CreateAccountRequest request = CreateAccountRequest.newBuilder().setUserId(account).build();
			debug("Send createAccount request");
			CreateAccountResponse response = stub.createAccount(request);
			debug("Received createAccount response");

			return "OK\n" + response;
		} catch (StatusRuntimeException e) {
			return e.getStatus().getDescription() + "\n";
		}
	}

	public String deleteAccount(String account) {
		try {
			DeleteAccountRequest request = DeleteAccountRequest.newBuilder().setUserId(account).build();
			debug("Send deleteAccount request");
			DeleteAccountResponse response = stub.deleteAccount(request);
			debug("Received deleteAccount response");

			return "OK\n" + response;
		} catch (StatusRuntimeException e) {
			return e.getStatus().getDescription() + "\n";
		}
	}

	public String transferTo(String accountFrom, String accountTo, int amount) {
		try {
			TransferToRequest request = TransferToRequest.newBuilder().setAccountFrom(accountFrom).setAccountTo(accountTo).setAmount(amount).build();
			debug("Send transferTo request");
			TransferToResponse response = stub.transferTo(request);
			debug("Received transferTo response");

			return "OK\n" + response;
		} catch (StatusRuntimeException e) {
			return e.getStatus().getDescription() + "\n";
		}
	}

	public void shutdown() {
		channel.shutdown();
	}
}
