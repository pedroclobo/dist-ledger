package pt.tecnico.distledger.userclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger.LookupResponse;

import pt.tecnico.distledger.sharedutils.Frontend;
import pt.tecnico.distledger.sharedutils.VectorClock;
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

	private Frontend<UserServiceGrpc.UserServiceBlockingStub> frontend;
	private VectorClock prev;

	public UserService(Frontend<UserServiceGrpc.UserServiceBlockingStub> frontend) {
		this.frontend = frontend;
		this.prev = new VectorClock();
	}

	public String balance(String qualifier, String account) {
		try {
			UserServiceGrpc.UserServiceBlockingStub stub = frontend.getStub(qualifier);

			BalanceRequest request = BalanceRequest.newBuilder()
			                                       .setUserId(account)
			                                       .setPrevTS(this.prev.toProtobuf())
			                                       .build();
			debug("Send balance request");
			BalanceResponse response = stub.balance(request);
			debug(String.format("Received balance response: %s", response));

			this.prev.merge(VectorClock.fromProtobuf(response.getValueTS()));

			return "OK\n" + response;
		} catch (StatusRuntimeException e) {
			return e.getStatus()
			        .getDescription()
			    + "\n";
		}
	}

	public String createAccount(String qualifier, String account) {
		try {
			UserServiceGrpc.UserServiceBlockingStub stub = frontend.getStub(qualifier);

			CreateAccountRequest request = CreateAccountRequest.newBuilder()
			                                                   .setUserId(account)
			                                                   .setPrevTS(prev.toProtobuf())
			                                                   .build();
			debug("Send createAccount request");
			UpdateOperationResponse response = stub.createAccount(request);
			debug("Received createAccount response");

			this.prev.merge(VectorClock.fromProtobuf(response.getTS()));

			return "OK\n" + response;
		} catch (StatusRuntimeException e) {
			return e.getStatus()
			        .getDescription()
			    + "\n";
		}
	}

	public String transferTo(String qualifier, String accountFrom, String accountTo, int amount) {
		try {
			UserServiceGrpc.UserServiceBlockingStub stub = frontend.getStub(qualifier);

			TransferToRequest request = TransferToRequest.newBuilder()
			                                             .setAccountFrom(accountFrom)
			                                             .setAccountTo(accountTo)
			                                             .setAmount(amount)
			                                             .setPrevTS(prev.toProtobuf())
			                                             .build();
			debug("Send transferTo request");
			UpdateOperationResponse response = stub.transferTo(request);
			debug("Received transferTo response");

			this.prev.merge(VectorClock.fromProtobuf(response.getTS()));

			return "OK\n" + response;
		} catch (StatusRuntimeException e) {
			return e.getStatus()
			        .getDescription()
			    + "\n";
		}
	}
}
