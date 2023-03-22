package pt.tecnico.distledger.server.exceptions;

public class ReadOnlyServerException extends RuntimeException {
	public ReadOnlyServerException() {
		super("READ-ONLY");
	}
}
