package pt.tecnico.distledger.server.exceptions;

public class AccountNotFoundException extends RuntimeException {
	public AccountNotFoundException(String account) {
		super("ERROR: The account " + account + " doesn't exist.");
	}
}
