package pt.tecnico.distledger.server.domain.exceptions;

public class CreateBrokerException extends RuntimeException {
	public CreateBrokerException() {
		super("ERROR: Can't create an account called 'broker'.");
	}
}
