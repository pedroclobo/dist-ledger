package pt.tecnico.distledger.server.domain.exceptions;

public class InsufficientBalanceException extends RuntimeException {
	public InsufficientBalanceException(String account, int amount) {
		super("ERROR: The account " + account + " only has " + amount + " coins.");
	}
}
