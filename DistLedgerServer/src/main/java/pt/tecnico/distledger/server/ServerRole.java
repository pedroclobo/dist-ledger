package pt.tecnico.distledger.server;

public class ServerRole {
	public enum Role {
		PRIMARY, SECONDARY
	}

	private Role role;

	public ServerRole(String qualifier) {
		if (qualifier.equals("A")) {
			this.setPrimary();
		} else {
			this.setSecondary();
		}
	}

	public void setPrimary() {
		this.role = Role.PRIMARY;
	}

	public void setSecondary() {
		this.role = Role.SECONDARY;
	}

	public boolean isPrimary() {
		return this.role == Role.PRIMARY;
	}

	public boolean isSecondary() {
		return this.role == Role.SECONDARY;
	}
}
