package pt.tecnico.distledger.adminclient.exceptions;

/**
 * Exception thrown when the server with the specified qualifier is not found
 * among the registered servers.
 */
public class ServerNotFoundException extends RuntimeException {

	/**
	 * Constructs a new ServerNotFoundException with the given qualifier.
	 *
	 * @param qualifier the qualifier of the server that was not found
	 */
	public ServerNotFoundException(String qualifier) {
		super("ERROR: The server with qualifier " + qualifier
		    + " is not registered.");
	}

}
