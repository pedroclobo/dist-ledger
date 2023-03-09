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
import pt.tecnico.distledger.server.exceptions.InvalidBalanceException;
import pt.tecnico.distledger.server.exceptions.InvalidTransferException;
import pt.tecnico.distledger.server.exceptions.ServerUnavailableException;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class ServerState {

	public enum ServerMode {
		ACTIVE, INACTIVE
	}

	private ServerMode mode;
	private List<Operation> ledger;
	private HashMap<String, Integer> accounts;

	private static boolean DEBUG_FLAG = false;

	public ServerState() {
		DEBUG_FLAG = System.getProperty("debug") != null;
		debug("ServerState initialized");

		this.mode = ServerMode.ACTIVE;
		this.ledger = new ArrayList<>();
		this.accounts = new HashMap<>();

		// initialize broker account
		this.accounts.put("broker", 1000);
	}

	private synchronized void checkMode() {
		if (this.mode == ServerMode.INACTIVE) {
			throw new ServerUnavailableException();
		}
	}

	public synchronized ServerMode getMode() {
		return mode;
	}

	public synchronized void setMode(ServerMode mode) {
		this.mode = mode;
		debug("ServerMode changed to " + mode.toString());
	}

	public synchronized List<Operation> getLedger() {
		return ledger;
	}

	public synchronized void setLedger(List<Operation> ledger) {
		this.ledger = ledger;
		debug("Ledger changed to " + ledger.toString());
	}

	public synchronized int getAccountBalance(String account) {
		checkMode();
		if (!this.accounts.containsKey(account)) {
			throw new AccountNotFoundException(account);
		}

		return this.accounts.get(account);
	}

	public synchronized void addOperationToLedger(Operation operation) {
		this.ledger.add(operation);
		debug("Operation added to ledger: " + operation);
	}

	public synchronized void addCreateOperation(CreateOp operation) {
		checkMode();

		String account = operation.getAccount();

		if (account.equals("broker")) {
			throw new CreateBrokerException();
		} else if (this.accounts.containsKey(account)) {
			throw new AccountAlreadyExistsException(account);
		}

		addOperationToLedger(operation);
		this.accounts.put(operation.getAccount(), 0);
		debug("Account created: " + operation);
	}

	public synchronized void addDeleteOperation(DeleteOp operation) {
		checkMode();

		String account = operation.getAccount();

		if (account.equals("broker")) {
			throw new DeleteBrokerException();
		} else if (!this.accounts.containsKey(operation.getAccount())) {
			throw new AccountNotFoundException(account);
		} else if (this.accounts.get(operation.getAccount()) != 0) {
			int balance = this.accounts.get(operation.getAccount());
			throw new AccountHasBalanceException(account, balance);
		}

		addOperationToLedger(operation);
		this.accounts.remove(operation.getAccount());
		debug("Account deleted: " + operation.toString());
	}

	public synchronized void addTransferOperation(TransferOp operation) {
		checkMode();

		String fromAccount = operation.getAccount();
		String toAccount = operation.getDestAccount();
		int amount = operation.getAmount();

		if (!this.accounts.containsKey(operation.getAccount())) {
			throw new AccountNotFoundException(fromAccount);
		} else if (!this.accounts.containsKey(operation.getDestAccount())) {
			throw new AccountNotFoundException(toAccount);
		} else if (fromAccount.equals(toAccount)) {
			throw new InvalidTransferException();
		} else if (amount <= 0) {
			throw new InvalidBalanceException(amount);
		} else if (this.accounts.get(operation.getAccount()) < amount) {
			int fromAccountBalance = this.accounts.get(operation.getAccount());
			throw new InsufficientBalanceException(fromAccount, fromAccountBalance);
		}

		addOperationToLedger(operation);
		this.accounts.put(fromAccount, this.accounts.get(fromAccount) - amount);
		this.accounts.put(toAccount, this.accounts.get(toAccount) + amount);
		debug("Transfer operation executed: " + operation);
	}

	/** Helper method to print debug messages. */
	private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
	}

}
