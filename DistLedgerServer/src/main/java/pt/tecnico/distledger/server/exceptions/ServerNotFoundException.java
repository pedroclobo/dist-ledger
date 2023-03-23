package pt.tecnico.distledger.server.exceptions;

public class ServerNotFoundException extends RuntimeException {
	public ServerNotFoundException(String qualifier) {
		super("ERROR: The server with qualifier " + qualifier
		    + " is not registered.");
	}
}
