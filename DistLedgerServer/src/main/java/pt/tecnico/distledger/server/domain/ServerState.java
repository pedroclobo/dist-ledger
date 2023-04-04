package pt.tecnico.distledger.server.domain;

import pt.tecnico.distledger.server.domain.operation.CreateOp;
import pt.tecnico.distledger.server.domain.operation.Operation;
import pt.tecnico.distledger.server.domain.operation.TransferOp;
import pt.tecnico.distledger.server.domain.exceptions.AccountAlreadyExistsException;
import pt.tecnico.distledger.server.domain.exceptions.AccountHasBalanceException;
import pt.tecnico.distledger.server.domain.exceptions.AccountNotFoundException;
import pt.tecnico.distledger.server.domain.exceptions.CreateBrokerException;
import pt.tecnico.distledger.server.domain.exceptions.InsufficientBalanceException;
import pt.tecnico.distledger.server.domain.exceptions.InvalidBalanceException;
import pt.tecnico.distledger.server.domain.exceptions.InvalidTransferException;

import pt.tecnico.distledger.sharedutils.VectorClock;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class ServerState {

	private List<Operation> ledger;
	private HashMap<String, Integer> accounts;
	private VectorClock valueTS;

	private static boolean DEBUG_FLAG = false;

	public ServerState() {
		DEBUG_FLAG = System.getProperty("debug") != null;
		debug("ServerState initialized");

		this.ledger = new ArrayList<>();
		this.accounts = new HashMap<>();
		this.valueTS = new VectorClock();

		// initialize broker account
		this.accounts.put("broker", 1000);
	}

	public synchronized List<Operation> getLedger() {
		return new ArrayList<Operation>(ledger);
	}

	public synchronized void setLedger(List<Operation> ledger) {
		this.ledger = ledger;
		debug("Ledger changed to " + ledger.toString());
	}

	public synchronized int getAccountBalance(String account) {
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

	public synchronized void addTransferOperation(TransferOp operation) {
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
