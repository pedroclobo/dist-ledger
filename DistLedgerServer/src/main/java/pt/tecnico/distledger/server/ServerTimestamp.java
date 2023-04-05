package pt.tecnico.distledger.server;

import pt.tecnico.distledger.sharedutils.VectorClock;

public class ServerTimestamp {
	public String qualifier;
	public VectorClock valueTS;
	public VectorClock replicaTS;

	public ServerTimestamp(String qualifier) {
		this.qualifier = qualifier;
		this.replicaTS = new VectorClock();
		this.valueTS = new VectorClock();
	}

	public synchronized VectorClock getReplicaTS() {
		return replicaTS;
	}

	public synchronized void setReplicaTS(VectorClock replicaTS) {
		this.replicaTS = replicaTS;
	}

	public synchronized void incrementReplicaTS() {
		switch (qualifier) {
		case "A":
			replicaTS.incrementTS(0);
			break;
		case "B":
			replicaTS.incrementTS(1);
			break;
		}
	}

	public synchronized void mergeReplicaTS(VectorClock other) {
		replicaTS.merge(other);
	}

	public synchronized VectorClock getValueTS() {
		return valueTS;
	}

	public synchronized void setValueTS(VectorClock valueTS) {
		this.valueTS = valueTS;
	}

	public synchronized void mergeValueTS(VectorClock other) {
		valueTS.merge(other);
	}
}
