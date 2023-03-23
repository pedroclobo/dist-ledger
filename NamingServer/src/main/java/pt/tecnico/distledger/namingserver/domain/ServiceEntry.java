package pt.tecnico.distledger.namingserver.domain;

import java.util.ArrayList;
import java.util.List;

public class ServiceEntry {
	private String name;
	private List<ServerEntry> servers = new ArrayList<>();

	public ServiceEntry(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ServerEntry> getServers() {
		return servers;
	}

	public void setServers(List<ServerEntry> servers) {
		this.servers = servers;
	}

	public void addServer(ServerEntry server) {
		this.servers.add(server);
	}

	public boolean containsServer(String host, int port) {
		return this.servers.stream()
		                   .anyMatch(server -> server.getHost()
		                                             .equals(host)
		                       && server.getPort() == port);
	}

	public void removeServer(String host, int port) {
		this.servers.removeIf(server -> server.getHost()
		                                      .equals(host)
		    && server.getPort() == port);
	}
}
