package pt.tecnico.distledger.server.domain.exceptions;

public class AccountAlreadyExistsException extends RuntimeException {
	public AccountAlreadyExistsException(String account) {
		super("ERROR: Account " + account + " already exists.");
	}
}
