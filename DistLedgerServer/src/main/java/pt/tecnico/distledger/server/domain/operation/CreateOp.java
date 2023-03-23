package pt.tecnico.distledger.server.domain.operation;

import pt.tecnico.distledger.server.domain.ServerState;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

public class CreateOp extends Operation {

	public CreateOp(String account) {
		super(account);
	}

	@Override
	public void execute(ServerState state) {
		state.addCreateOperation(this);
	}

	@Override
	public DistLedgerCommonDefinitions.Operation toProtobuf() {
		DistLedgerCommonDefinitions.Operation.Builder op = DistLedgerCommonDefinitions.Operation.newBuilder();
		op.setType(DistLedgerCommonDefinitions.OperationType.OP_CREATE_ACCOUNT)
		  .setUserId(this.getAccount());

		return op.build();
	}

	public String toString() {
		return "CreateOp{" + "account='" + getAccount() + '\'' + '}';
	}
}
