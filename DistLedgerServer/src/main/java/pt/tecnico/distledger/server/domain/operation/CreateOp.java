package pt.tecnico.distledger.server.domain.operation;

import pt.tecnico.distledger.server.domain.ServerState;

public class CreateOp extends Operation {

	public CreateOp(String account) {
		super(account);
	}

	@Override
	public void execute(ServerState state) {
		state.addCreateOperation(this);
	}

	public String toString() {
		return "CreateOp{" +
				"account='" + getAccount() + '\'' +
				'}';
	}
}
