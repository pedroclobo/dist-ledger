package pt.tecnico.distledger.namingserver.domain;

import pt.tecnico.distledger.namingserver.exceptions.ServerAlreadyExistsException;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class NamingServerState {
	private Map<String, ServiceEntry> services = new HashMap<>();

	public NamingServerState() {
	}

	public Map<String, ServiceEntry> getServices() {
		return new HashMap<String, ServiceEntry>(services);
	}

	public synchronized void setServices(Map<String, ServiceEntry> services) {
		this.services = services;
	}

	private synchronized boolean containsQualifier(String qualifier) {
		for (ServiceEntry service : services.values()) {
			for (ServerEntry server : service.getServers()) {
				if (server.getQualifier().equals(qualifier)) {
					return true;
				}
			}
		}

		return false;
	}

	public synchronized void register(String serviceName, String qualifier, String host, int port) {
		if (services.containsKey(serviceName)) {
			ServiceEntry serviceEntry = services.get(serviceName);
			if (serviceEntry.containsServer(host, port)) {
				throw new ServerAlreadyExistsException(host, port);
			}

			serviceEntry.addServer(new ServerEntry(qualifier, host, port));
		} else {
			ServiceEntry serviceEntry = new ServiceEntry(serviceName);
			ServerEntry serverEntry = new ServerEntry(qualifier, host, port);
			serviceEntry.addServer(serverEntry);

			services.put(serviceName, serviceEntry);
		}
	}

	public synchronized List<ServerEntry> lookup(String serviceName, String qualifier) {
		// Service or qualifier don't exist
		if (!services.containsKey(serviceName) || (!qualifier.equals("") && !containsQualifier(qualifier))) {
			return new ArrayList<>();
		}

		List<ServerEntry> servers = new ArrayList<>();

		if (qualifier.equals("")) {
			for (ServerEntry server : services.get(serviceName).getServers()) {
				servers.add(server);
			}
		} else {
			for (ServerEntry server : services.get(serviceName).getServers()) {
				if (server.getQualifier().equals(qualifier)) {
					servers.add(server);
				}
			}
		}

		return new ArrayList<ServerEntry>(servers);
	}

	public synchronized void delete(String serviceName, String host, int port) {
		if (services.containsKey(serviceName)) {
			ServiceEntry serviceEntry = services.get(serviceName);
			serviceEntry.removeServer(host, port);
		}
	}
}
