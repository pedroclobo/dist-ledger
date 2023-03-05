package pt.tecnico.distledger.server.domain.operation;

import pt.tecnico.distledger.server.domain.ServerState;

public class DeleteOp extends Operation {

	public DeleteOp(String account) {
		super(account);
	}

	@Override
	public void execute(ServerState state) {
		state.addDeleteOperation(this);
	}

}
