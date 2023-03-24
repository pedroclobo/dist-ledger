package pt.tecnico.distledger.server.domain.exceptions;

public class InvalidTransferException extends RuntimeException {
	public InvalidTransferException() {
		super("ERROR: You can't transfer coins from an account to the same account.");
	}
}
