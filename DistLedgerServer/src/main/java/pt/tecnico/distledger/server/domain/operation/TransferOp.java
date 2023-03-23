package pt.tecnico.distledger.server.domain.operation;

import pt.tecnico.distledger.server.domain.ServerState;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

public class TransferOp extends Operation {
	private String destAccount;
	private int amount;

	public TransferOp(String fromAccount, String destAccount, int amount) {
		super(fromAccount);
		this.destAccount = destAccount;
		this.amount = amount;
	}

	public String getDestAccount() {
		return destAccount;
	}

	public void setDestAccount(String destAccount) {
		this.destAccount = destAccount;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public void execute(ServerState state) {
		state.addTransferOperation(this);
	}

	@Override
	public DistLedgerCommonDefinitions.Operation toProtobuf() {
		DistLedgerCommonDefinitions.Operation.Builder op = DistLedgerCommonDefinitions.Operation.newBuilder();
		op.setType(DistLedgerCommonDefinitions.OperationType.OP_TRANSFER_TO)
		  .setUserId(this.getAccount())
		  .setDestUserId(((TransferOp) this).getDestAccount())
		  .setAmount(((TransferOp) this).getAmount());

		return op.build();
	}

	public String toString() {
		return "TransferOp{" + "fromAccount='" + getAccount() + '\''
		    + "destAccount='" + destAccount + '\'' + ", amount=" + amount + '}';
	}

}
