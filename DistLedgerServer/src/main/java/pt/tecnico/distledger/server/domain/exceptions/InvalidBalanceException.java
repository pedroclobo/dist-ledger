package pt.tecnico.distledger.server.domain.exceptions;

public class InvalidBalanceException extends RuntimeException {
	public InvalidBalanceException(int amount) {
		super("ERROR: You can't transfer " + amount + " coins.");
	}
}
