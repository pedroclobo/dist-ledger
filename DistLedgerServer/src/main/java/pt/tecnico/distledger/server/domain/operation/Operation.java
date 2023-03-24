package pt.tecnico.distledger.server.domain.operation;

import pt.tecnico.distledger.server.domain.ServerState;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

public abstract class Operation {
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
		return this.getClass()
		           .getSimpleName();
	}

	public void execute(ServerState state) {
	}

	public abstract DistLedgerCommonDefinitions.Operation toProtobuf();

	public static Operation fromProtobuf(DistLedgerCommonDefinitions.Operation op) {
		switch (op.getType()) {
		case OP_CREATE_ACCOUNT:
			return new CreateOp(op.getUserId());
		case OP_DELETE_ACCOUNT:
			return new DeleteOp(op.getUserId());
		case OP_TRANSFER_TO:
			return new TransferOp(op.getUserId(), op.getDestUserId(), op.getAmount());
		default:
			return null;
		}
	};
}
