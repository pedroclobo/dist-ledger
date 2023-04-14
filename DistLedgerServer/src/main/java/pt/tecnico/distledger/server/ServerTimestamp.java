package pt.tecnico.distledger.server;

import pt.tecnico.distledger.sharedutils.VectorClock;

public class ServerTimestamp {
	public String qualifier;
	public int serverIdx;
	public VectorClock valueTS;
	public VectorClock replicaTS;

	public ServerTimestamp(String qualifier) {
		this.qualifier = qualifier;
		this.serverIdx = qualifier.charAt(0) - 'A'; // server qualifiers start at 'A' and go in alphabetical order
		this.replicaTS = new VectorClock();
		this.valueTS = new VectorClock();
	}

	public synchronized int getServerIdx() {
		return this.serverIdx;
	}

	public synchronized void setServerIdx(int idx) {
		this.serverIdx = idx;
	}

	public synchronized VectorClock getReplicaTS() {
		return replicaTS;
	}

	public synchronized void setReplicaTS(VectorClock replicaTS) {
		this.replicaTS = replicaTS;
	}

	public synchronized void incrementReplicaTS() {
		replicaTS.incrementTS(serverIdx);
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

	public String toStringPretty() {
		String serverId = "           ";
		for (int i = 0; i < serverIdx; i++) {
			serverId += "   ";
		}
		serverId += qualifier;
		return serverId + "\nReplicaTS " + replicaTS + "\nValueTS   " + valueTS;
	}
}
