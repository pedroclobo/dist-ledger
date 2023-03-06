package pt.tecnico.distledger.server.domain;

import pt.tecnico.distledger.server.domain.operation.CreateOp;
import pt.tecnico.distledger.server.domain.operation.DeleteOp;
import pt.tecnico.distledger.server.domain.operation.Operation;
import pt.tecnico.distledger.server.domain.operation.TransferOp;

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

	public ServerState() {
		this.mode = ServerMode.ACTIVE;
		this.ledger = new ArrayList<>();
		this.accounts = new HashMap<>();

		// initialize broker account
		this.accounts.put("broker", 1000);
	}

	public ServerMode getServerMode() {
		return mode;
	}

	public void setServerMode(ServerMode mode) {
		this.mode = mode;
	}

	public List<Operation> getLedger() {
		return ledger;
	}

	public void setLedger(List<Operation> ledger) {
		this.ledger = ledger;
	}

	public int getAccountBalance(String account) {
		return this.accounts.get(account);
	}

	public void addOperationToLedger(Operation operation) {
		this.ledger.add(operation);
	}

	public void addCreateOperation(CreateOp operation) {
		addOperationToLedger(operation);
		this.accounts.put(operation.getAccount(), 0);
	}

	public void addDeleteOperation(DeleteOp operation) {
		addOperationToLedger(operation);
		this.accounts.remove(operation.getAccount());
	}

	public void addTransferOperation(TransferOp operation) {
		addOperationToLedger(operation);
		this.accounts.put(operation.getAccount(), this.accounts.get(operation.getAccount()) - operation.getAmount());
		this.accounts.put(operation.getDestAccount(), this.accounts.get(operation.getDestAccount()) + operation.getAmount());
	}

}
