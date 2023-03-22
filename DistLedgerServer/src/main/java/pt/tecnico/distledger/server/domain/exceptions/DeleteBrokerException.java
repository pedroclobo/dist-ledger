package pt.tecnico.distledger.server.domain.exceptions;

public class DeleteBrokerException extends RuntimeException {
	public DeleteBrokerException() {
		super("ERROR: Can't delete the 'broker' account.");
	}
}
