package pt.tecnico.distledger.server.domain;

import pt.tecnico.distledger.server.domain.operation.CreateOp;
import pt.tecnico.distledger.server.domain.operation.DeleteOp;
import pt.tecnico.distledger.server.domain.operation.Operation;
import pt.tecnico.distledger.server.domain.operation.TransferOp;

import pt.tecnico.distledger.server.exceptions.AccountAlreadyExistsException;
import pt.tecnico.distledger.server.exceptions.AccountHasBalanceException;
import pt.tecnico.distledger.server.exceptions.AccountNotFoundException;
import pt.tecnico.distledger.server.exceptions.CreateBrokerException;
import pt.tecnico.distledger.server.exceptions.DeleteBrokerException;
import pt.tecnico.distledger.server.exceptions.InsufficientBalanceException;
import pt.tecnico.distledger.server.exceptions.ServerUnavailableException;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class ServerState {

	public enum ServerMode {
		ACTIVE,
		INACTIVE
	}

	private ServerMode mode;
	private List<Operation> ledger;
	private HashMap<String, Integer> accounts;

	private static boolean DEBUG_FLAG = false;

	public ServerState(boolean debug) {
		this();
		DEBUG_FLAG = debug;
	}

	public ServerState() {
		this.mode = ServerMode.ACTIVE;
		this.ledger = new ArrayList<>();
		this.accounts = new HashMap<>();

		// initialize broker account
		this.accounts.put("broker", 1000);

		debug("ServerState initialized");
	}

	private void checkMode() {
		if (this.mode == ServerMode.INACTIVE) {
			throw new RuntimeException("UNAVAILABLE");
		}
	}

	public ServerMode getServerMode() {
		return mode;
	}

	public void setServerMode(ServerMode mode) {
		this.mode = mode;
		debug("ServerMode changed to " + mode.toString());
	}

	public List<Operation> getLedger() {
		return ledger;
	}

	public void setLedger(List<Operation> ledger) {
		this.ledger = ledger;
		debug("Ledger changed to " + ledger.toString());
	}

	public int getAccountBalance(String account) {
		checkMode();
		return this.accounts.get(account);
	}

	public void addOperationToLedger(Operation operation) {
		this.ledger.add(operation);
		debug("Operation added to ledger: " + operation.toString());
	}

	public void addCreateOperation(CreateOp operation) {
		checkMode();

		String account = operation.getAccount();

		if (account.equals("broker")) {
			throw new CreateBrokerException();
		} else if (this.accounts.containsKey(account)) {
			throw new AccountAlreadyExistsException(account);
		}

		addOperationToLedger(operation);
		this.accounts.put(operation.getAccount(), 0);
		debug("Account created: " + operation.toString() + " with balance " + this.accounts.get(operation.getAccount()));
	}

	public void addDeleteOperation(DeleteOp operation) {
		checkMode();

		String account = operation.getAccount();
		int balance = this.accounts.get(account);

		if (account.equals("broker")) {
			throw new DeleteBrokerException();
		} else if (this.accounts.get(operation.getAccount()) != 0) {
			throw new AccountHasBalanceException(account, balance);
		}

		addOperationToLedger(operation);
		this.accounts.remove(operation.getAccount());
		debug("Account deleted: " + operation.toString());
	}

	public void addTransferOperation(TransferOp operation) {
		checkMode();

		String fromAccount = operation.getAccount();
		String toAccount = operation.getDestAccount();
		int amount = operation.getAmount();

		int fromAccountBalance = this.accounts.get(operation.getAccount());

		if (!this.accounts.containsKey(operation.getAccount())) {
			throw new AccountNotFoundException(fromAccount);
		} else if (!this.accounts.containsKey(operation.getDestAccount())) {
			throw new AccountNotFoundException(toAccount);
		} else if (fromAccountBalance < amount) {
			throw new InsufficientBalanceException(fromAccount, fromAccountBalance);
		}

		addOperationToLedger(operation);
		this.accounts.put(operation.getAccount(), this.accounts.get(operation.getAccount()) - operation.getAmount());
		this.accounts.put(operation.getDestAccount(), this.accounts.get(operation.getDestAccount()) + operation.getAmount());
		debug("Transfer operation executed: " + operation.toString());
	}

	/** Helper method to print debug messages. */
	private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
	}

}
