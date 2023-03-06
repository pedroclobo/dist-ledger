package pt.tecnico.distledger.server.exceptions;

public class AccountHasBalanceException extends RuntimeException {
	public AccountHasBalanceException(String account, int balance) {
		super("Can't delete an account with a balance in it. The account has " + balance + " coins.");
	}
}
