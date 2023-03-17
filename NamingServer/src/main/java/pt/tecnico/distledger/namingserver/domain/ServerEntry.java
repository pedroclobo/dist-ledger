package pt.tecnico.distledger.namingserver.domain;

public class ServerEntry {
	private String qualifier;
	private String host;
	private int port;

	public ServerEntry(String qualifier, String host, int port) {
		this.qualifier = qualifier;
		this.host = host;
		this.port = port;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
