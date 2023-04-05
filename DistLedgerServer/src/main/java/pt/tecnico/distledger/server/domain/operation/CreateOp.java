package pt.tecnico.distledger.server.domain.operation;

import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.sharedutils.VectorClock;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

public class CreateOp extends Operation {

	public CreateOp(String account) {
		super(account);
	}

	public CreateOp(String account, VectorClock prevTs, VectorClock ts, boolean stable) {
		super(account, prevTs, ts, stable);
	}

	@Override
	public DistLedgerCommonDefinitions.Operation toProtobuf() {
		DistLedgerCommonDefinitions.Operation.Builder op = DistLedgerCommonDefinitions.Operation.newBuilder();
		op.setType(DistLedgerCommonDefinitions.OperationType.OP_CREATE_ACCOUNT)
		  .setUserId(this.getAccount())
		  .setPrevTS(this.getPrev()
		                 .toProtobuf())
		  .setTS(this.getTS()
		             .toProtobuf())
		  .setStable(this.isStable());

		return op.build();
	}

	public String toString() {
		return "CreateOp{" + "account='" + getAccount() + '\'' + '}';
	}
}
