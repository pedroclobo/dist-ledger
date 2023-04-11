package pt.tecnico.distledger.server.domain.operation;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.tecnico.distledger.sharedutils.VectorClock;

public abstract class Operation {
	private String account;

	private VectorClock prevTS;
	private VectorClock TS;
	private boolean stable;

	public Operation(String fromAccount) {
		this.account = fromAccount;
		this.prevTS = new VectorClock();
		this.TS = new VectorClock();
		this.stable = false;
	}

	public Operation(String fromAccount, VectorClock prevTs, VectorClock ts, boolean stable) {
		this.account = fromAccount;
		this.prevTS = prevTs;
		this.TS = ts;
		this.stable = stable;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public VectorClock getPrev() {
		return new VectorClock(prevTS);
	}

	public void setPrev(VectorClock prev) {
		prevTS = prev;
	}

	public VectorClock getTS() {
		return new VectorClock(TS);
	}

	public void setTS(VectorClock newTS) {
		TS = newTS;
	}

	public boolean isStable() {
		return stable;
	}

	public void setStable() {
		stable = true;
	}

	public String getType() {
		return this.getClass()
		           .getSimpleName();
	}

	public abstract DistLedgerCommonDefinitions.Operation toProtobuf();

	public static Operation fromProtobuf(DistLedgerCommonDefinitions.Operation op) {
		switch (op.getType()) {
		case OP_CREATE_ACCOUNT:
			return new CreateOp(op.getUserId(), VectorClock.fromProtobuf(op.getPrevTS()),
			    VectorClock.fromProtobuf(op.getTS()), op.getStable());
		case OP_TRANSFER_TO:
			return new TransferOp(op.getUserId(), op.getDestUserId(), op.getAmount(),
			    VectorClock.fromProtobuf(op.getPrevTS()), VectorClock.fromProtobuf(op.getTS()), op.getStable());
		default:
			return null;
		}
	};
}
