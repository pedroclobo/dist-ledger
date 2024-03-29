package pt.tecnico.distledger.server.domain;

import pt.tecnico.distledger.server.ServerTimestamp;
import pt.tecnico.distledger.server.domain.operation.CreateOp;
import pt.tecnico.distledger.server.domain.operation.Operation;
import pt.tecnico.distledger.server.domain.operation.TransferOp;
import pt.tecnico.distledger.server.domain.exceptions.AccountAlreadyExistsException;
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
	private ServerTimestamp timestamp;

	private static boolean DEBUG_FLAG = false;

	public ServerState(ServerTimestamp timestamp) {
		this.timestamp = timestamp;
		this.ledger = new ArrayList<>();
		this.accounts = new HashMap<>();

		// initialize broker account
		this.accounts.put("broker", 1000);

		DEBUG_FLAG = System.getProperty("debug") != null;
		debug("ServerState initialized");
	}

	public synchronized List<Operation> getLedger() {
		return new ArrayList<Operation>(ledger);
	}

	public synchronized void setLedger(List<Operation> ledger) {
		this.ledger = ledger;
		debug("Ledger changed to:%n" + ledger.toString());
	}

	public synchronized int getAccountBalance(String account) {
		if (!this.accounts.containsKey(account)) {
			throw new AccountNotFoundException(account);
		}

		return this.accounts.get(account);
	}

	public synchronized boolean OperationInLedger(Operation operation) {
		for (Operation op : this.ledger) {
			if (op.getTS()
			      .equals(operation.getTS())) {
				return true;
			}
		}
		return false;
	}

	public synchronized void addOperationToLedger(Operation operation) {
		this.ledger.add(operation);
		debug("Added operation to ledger: " + operation.toString());

		// Check if operation can be executed
		VectorClock valueTS = timestamp.getValueTS();
		if (valueTS.GE(operation.getPrev())) {
			operation.setStable();
			executeOperation(operation);
			timestamp.mergeValueTS(operation.getTS());
			debug("+ operation executed");
		}
		debug("" + timestamp.toStringPretty());
	}

	public synchronized void executeOperation(Operation operation) {
		try {
			switch (operation.getType()) {
			case "CreateOp":
				executeCreateOperation((CreateOp) operation);
				break;
			case "TransferOp":
				executeTransferOperation((TransferOp) operation);
				break;
			}
		} catch (RuntimeException e) {
			debug("Operation " + operation.toString() + " failed: " + e.getMessage());
		}
	}

	// execute operation and update valueTS
	public synchronized void executeCreateOperation(CreateOp operation) {
		String account = operation.getAccount();

		if (account.equals("broker")) {
			throw new CreateBrokerException();
		} else if (this.accounts.containsKey(account)) {
			throw new AccountAlreadyExistsException(account);
		}

		this.accounts.put(operation.getAccount(), 0);
	}

	public synchronized void executeTransferOperation(TransferOp operation) {
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

		this.accounts.put(fromAccount, this.accounts.get(fromAccount) - amount);
		this.accounts.put(toAccount, this.accounts.get(toAccount) + amount);
	}

	public synchronized void recomputeStability() {
		debug("Recomputing operations in ledger:");
		for (Operation op : ledger) {
			if (timestamp.getValueTS()
			             .GE(op.getPrev())
			    && !op.isStable()) {
				op.setStable();
				executeOperation(op);
				timestamp.mergeValueTS(op.getTS());
				debug("+ executed " + op.toString());
				debug("" + timestamp.toStringPretty());
			}
		}
		debug("Recomputation completed");
	}

	/** Helper method to print debug messages. */
	private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
	}

}
