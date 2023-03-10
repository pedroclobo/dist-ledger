package pt.tecnico.distledger.server.domain.operation;

import pt.tecnico.distledger.server.domain.ServerState;

public class Operation {
	private String account;

	public Operation(String fromAccount) {
		this.account = fromAccount;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getType() {
		return this.getClass().getSimpleName();
	}

	public void execute(ServerState state) {
	}

	public String toString() {
		return "Operation{" + "account='" + account + '\'' + '}';
	}

}
