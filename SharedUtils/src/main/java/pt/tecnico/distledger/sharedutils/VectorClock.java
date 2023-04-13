package pt.tecnico.distledger.sharedutils;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

public class VectorClock {
	private int timestamps[];

	private static int SIZE = 3;

	public VectorClock(VectorClock other) {
		timestamps = new int[SIZE];
		for (int i = 0; i < SIZE; i++) {
			timestamps[i] = other.getTS(i);
		}
	}

	public VectorClock() {
		timestamps = new int[SIZE];
		for (int i = 0; i < SIZE; i++) {
			timestamps[i] = 0;
		}
	}

	public Integer getTS(int index) {
		return timestamps[index];
	}

	public void setTS(int index, int value) {
		timestamps[index] = value;
	}

	public void incrementTS(int index) {
		this.setTS(index, this.getTS(index) + 1);
	}

	public void merge(VectorClock other) {
		for (int i = 0; i < SIZE; i++) {
			this.setTS(i, Math.max(this.getTS(i), other.getTS(i)));
		}
	}

	public void mergeOnIdx(VectorClock other, int idx) {
		this.setTS(idx, Math.max(this.getTS(idx), other.getTS(idx)));
	}

	public boolean GE(VectorClock other) {
		for (int i = 0; i < SIZE; i++) {
			if (this.getTS(i) < other.getTS(i)) {
				return false;
			}
		}
		return true;
	}

	public boolean equals(VectorClock other) {
		for (int i = 0; i < SIZE; i++) {
			if (this.getTS(i) != other.getTS(i)) {
				return false;
			}
		}
		return true;
	}

	public DistLedgerCommonDefinitions.VectorClock toProtobuf() {
		DistLedgerCommonDefinitions.VectorClock.Builder ts = DistLedgerCommonDefinitions.VectorClock.newBuilder();

		for (int i = 0; i < SIZE; i++) {
			ts.addTs(this.getTS(i));
		}

		return ts.build();
	}

	public static VectorClock fromProtobuf(DistLedgerCommonDefinitions.VectorClock ts) {
		VectorClock clock = new VectorClock();

		for (int i = 0; i < SIZE; i++) {
			clock.setTS(i, ts.getTs(i));
		}

		return clock;
	}
}
