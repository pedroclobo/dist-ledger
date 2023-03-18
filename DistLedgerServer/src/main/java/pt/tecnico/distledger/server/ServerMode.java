package pt.tecnico.distledger.server;

public class ServerMode {
	public enum Mode {
		ACTIVE, INACTIVE
	}

	private Mode mode;

	public ServerMode() {
		this.activate();
	}

	public void activate() {
		this.mode = Mode.ACTIVE;
	}

	public void deactivate() {
		this.mode = Mode.INACTIVE;
	}

	public boolean isActive() {
		return this.mode == Mode.ACTIVE;
	}

	public boolean isInactive() {
		return this.mode == Mode.INACTIVE;
	}
}
