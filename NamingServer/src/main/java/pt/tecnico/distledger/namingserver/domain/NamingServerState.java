package pt.tecnico.distledger.namingserver.domain;

import pt.tecnico.distledger.namingserver.exceptions.ServerAlreadyExistsException;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class NamingServerState {

	private Map<String, ServiceEntry> services = new HashMap<>();

	private static boolean DEBUG_FLAG = false;

	public NamingServerState() {
		DEBUG_FLAG = System.getProperty("debug") != null;
		debug("NamingServerState initialized");
	}

	public Map<String, ServiceEntry> getServices() {
		return new HashMap<String, ServiceEntry>(services);
	}

	public synchronized void setServices(Map<String, ServiceEntry> services) {
		this.services = services;
		debug("Services changed");
	}

	private synchronized boolean containsQualifier(String qualifier) {
		for (ServiceEntry service : services.values()) {
			for (ServerEntry server : service.getServers()) {
				if (server.getQualifier()
				          .equals(qualifier)) {
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
		debug(String.format("Added server with qualifier='%s', service='%s' and host:port='%s:%d'", serviceName,
		    qualifier, host, port));
	}

	public synchronized List<ServerEntry> lookup(String serviceName) {
		// Service doesn't exist
		if (!services.containsKey(serviceName)) {
			return new ArrayList<>();
		}

		return new ArrayList<ServerEntry>(services.get(serviceName)
		                                          .getServers());
	}

	public synchronized List<ServerEntry> lookup(String serviceName, String qualifier) {
		// Service or qualifier don't exist
		if (!services.containsKey(serviceName) || (!qualifier.equals("") && !containsQualifier(qualifier))) {
			return new ArrayList<>();
		}

		List<ServerEntry> servers = new ArrayList<>();

		if (qualifier.equals("")) {
			for (ServerEntry server : services.get(serviceName)
			                                  .getServers()) {
				servers.add(server);
			}
		} else {
			for (ServerEntry server : services.get(serviceName)
			                                  .getServers()) {
				if (server.getQualifier()
				          .equals(qualifier)) {
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
			debug(String.format("Removed server with service='%s' and host:port='%s:%d'", serviceName, host, port));
		}
	}

	/** Helper method to print debug messages. */
	private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
	}
}
