package pt.tecnico.distledger.server.exceptions;

public class DeleteBrokerException extends RuntimeException {
	public DeleteBrokerException() {
		super("ERROR: Can't delete the 'broker' account.");
	}
}
