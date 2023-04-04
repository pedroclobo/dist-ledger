package pt.tecnico.distledger.sharedutils;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

import java.util.List;
import java.util.ArrayList;

public class VectorClock {
	private List<Integer> timestamps;

	int DEFAULT_SIZE = 2;

	public VectorClock() {
		this.timestamps = new ArrayList<Integer>(DEFAULT_SIZE);
		for (int i = 0; i < DEFAULT_SIZE; i++) {
			this.timestamps.add(0);
		}
	}

	public Integer getTS(int index) {
		return this.timestamps.get(index);
	}

	public void setTS(int index, int value) {
		this.timestamps.set(index, value);
	}

	public void incrementTS(int index) {
		this.timestamps.set(index, this.timestamps.get(index) + 1);
	}

	public void merge(VectorClock other) {
		for (int i = 0; i < this.timestamps.size(); i++) {
			this.timestamps.set(i, Math.max(this.getTS(i), other.getTS(i)));
		}
	}

	public boolean GE(VectorClock other) {
		for (int i = 0; i < this.timestamps.size(); i++) {
			if (this.getTS(i) < other.getTS(i)) {
				return false;
			}
		}
		return true;
	}

	public DistLedgerCommonDefinitions.VectorClock toProtobuf() {
		DistLedgerCommonDefinitions.VectorClock.Builder ts = DistLedgerCommonDefinitions.VectorClock.newBuilder();
		ts.addAllTs(this.timestamps);

		return ts.build();
	}

	public static VectorClock fromProtobuf(DistLedgerCommonDefinitions.VectorClock ts) {
		VectorClock clock = new VectorClock();
		clock.timestamps = new ArrayList<Integer>(ts.getTsList());

		return clock;
	}

}
