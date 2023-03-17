package pt.tecnico.distledger.userclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger.LookupResponse;

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

	private NamingServerService namingServerService;

	public UserService(NamingServerService namingServerService) {
		this.namingServerService = namingServerService;
	}

	public String balance(String qualifier, String account) {
		LookupResponse serverResponse = namingServerService.lookup("DistLedger", qualifier);
		String target = serverResponse.getServer(0).getHost() + ":" + serverResponse.getServer(0).getPort();

		ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);

		try {
			BalanceRequest request = BalanceRequest.newBuilder().setUserId(account).build();
			debug("Send balance request");
			BalanceResponse response = stub.balance(request);
			debug(String.format("Received balance response: %s", response));

			channel.shutdown();

			return "OK\n" + response;
		} catch (StatusRuntimeException e) {
			channel.shutdown();
			return e.getStatus().getDescription() + "\n";
		}
	}

	public String createAccount(String qualifier, String account) {
		LookupResponse serverResponse = namingServerService.lookup("DistLedger", qualifier);
		String target = serverResponse.getServer(0).getHost() + ":" + serverResponse.getServer(0).getPort();

		ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);

		try {
			CreateAccountRequest request = CreateAccountRequest.newBuilder().setUserId(account).build();
			debug("Send createAccount request");
			CreateAccountResponse response = stub.createAccount(request);
			debug("Received createAccount response");

			channel.shutdown();

			return "OK\n" + response;
		} catch (StatusRuntimeException e) {
			return e.getStatus().getDescription() + "\n";
		}
	}

	public String deleteAccount(String qualifier, String account) {
		LookupResponse serverResponse = namingServerService.lookup("DistLedger", qualifier);
		String target = serverResponse.getServer(0).getHost() + ":" + serverResponse.getServer(0).getPort();

		ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);

		try {
			DeleteAccountRequest request = DeleteAccountRequest.newBuilder().setUserId(account).build();
			debug("Send deleteAccount request");
			DeleteAccountResponse response = stub.deleteAccount(request);
			debug("Received deleteAccount response");

			channel.shutdown();

			return "OK\n" + response;
		} catch (StatusRuntimeException e) {
			return e.getStatus().getDescription() + "\n";
		}
	}

	public String transferTo(String qualifier, String accountFrom, String accountTo, int amount) {
		LookupResponse serverResponse = namingServerService.lookup("DistLedger", qualifier);
		String target = serverResponse.getServer(0).getHost() + ":" + serverResponse.getServer(0).getPort();

		ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);

		try {
			TransferToRequest request = TransferToRequest.newBuilder().setAccountFrom(accountFrom).setAccountTo(accountTo).setAmount(amount).build();
			debug("Send transferTo request");
			TransferToResponse response = stub.transferTo(request);
			debug("Received transferTo response");

			channel.shutdown();

			return "OK\n" + response;
		} catch (StatusRuntimeException e) {
			return e.getStatus().getDescription() + "\n";
		}
	}

	public void shutdown() {
		namingServerService.shutdown();
	}
}
