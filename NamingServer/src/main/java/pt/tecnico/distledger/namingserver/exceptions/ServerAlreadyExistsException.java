package pt.tecnico.distledger.namingserver.exceptions;

public class ServerAlreadyExistsException extends RuntimeException {
	public ServerAlreadyExistsException(String host, int port) {
		super("ERROR: Server with host " + host + "and port " + port
		    + "already exists.");
	}
}
