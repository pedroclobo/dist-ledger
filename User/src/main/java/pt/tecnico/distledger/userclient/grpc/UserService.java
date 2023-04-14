package pt.tecnico.distledger.userclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import pt.tecnico.distledger.sharedutils.Frontend;
import pt.tecnico.distledger.sharedutils.VectorClock;
import io.grpc.StatusRuntimeException;

public class UserService {

	/**
	 * Set flag to true to print debug messages. The flag can be set using the
	 * -Ddebug=true command line option.
	 */
	private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

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
			debug(String.format("Request server '%s' to get balance of account '%s'", qualifier, account));
			BalanceResponse response = stub.balance(request);
			debug(String.format("Request handled, balance = '%s'", response.getValue()));

			this.prev.merge(VectorClock.fromProtobuf(response.getValueTS()));
			debug(String.format("prev = %s", prev));

			return "OK\n" + response.getValue() + "\n";
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
			debug(String.format("Request server '%s' to create account '%s'", qualifier, account));
			UpdateOperationResponse response = stub.createAccount(request);
			debug("Request handled");

			this.prev.merge(VectorClock.fromProtobuf(response.getTS()));
			debug(String.format("prev = %s", prev));

			return "OK\n";
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
			debug(String.format("Request server '%s' to transfer '%d' from account '%s' to account '%s'", qualifier,
			    amount, accountFrom, accountTo));
			UpdateOperationResponse response = stub.transferTo(request);
			debug("Request handled");

			this.prev.merge(VectorClock.fromProtobuf(response.getTS()));
			debug(String.format("prev = %s", prev));

			return "OK\n";
		} catch (StatusRuntimeException e) {
			return e.getStatus()
			        .getDescription()
			    + "\n";
		}
	}

	/** Helper method to print debug messages. */
	private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
	}
}
