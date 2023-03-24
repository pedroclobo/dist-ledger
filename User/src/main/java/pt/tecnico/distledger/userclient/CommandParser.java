package pt.tecnico.distledger.userclient;

import pt.tecnico.distledger.userclient.grpc.UserService;

import java.util.Scanner;

public class CommandParser {

	/**
	 * Set flag to true to print debug messages. The flag can be set using the
	 * -Ddebug command line option.
	 */
	private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

	/** Helper method to print debug messages. */
	private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
	}

	private static final String SPACE = " ";
	private static final String CREATE_ACCOUNT = "createAccount";
	private static final String DELETE_ACCOUNT = "deleteAccount";
	private static final String TRANSFER_TO = "transferTo";
	private static final String BALANCE = "balance";
	private static final String HELP = "help";
	private static final String EXIT = "exit";

	private final UserService userService;

	public CommandParser(UserService userService) {
		this.userService = userService;
	}

	void parseInput() {

		Scanner scanner = new Scanner(System.in);
		boolean exit = false;

		while (!exit) {
			System.out.print("> ");
			String line = scanner.nextLine()
			                     .trim();
			String cmd = line.split(SPACE)[0];
			debug(String.format("input line: %s", line));
			debug(String.format("command: %s", cmd));

			try {
				switch (cmd) {
				case CREATE_ACCOUNT:
					this.createAccount(line);
					break;

				case DELETE_ACCOUNT:
					this.deleteAccount(line);
					break;

				case TRANSFER_TO:
					this.transferTo(line);
					break;

				case BALANCE:
					this.balance(line);
					break;

				case HELP:
					debug("Call printUsage()");
					this.printUsage();
					break;

				case EXIT:
					exit = true;
					debug("Exiting");
					break;

				default:
					debug("Command doesn't exist");
					break;
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}

	private void createAccount(String line) {
		String[] split = line.split(SPACE);

		if (split.length != 3) {
			debug("Call printUsage()");
			this.printUsage();
			return;
		}

		String server = split[1];
		String username = split[2];
		debug(String.format("server: %s", server));
		debug(String.format("username: %s", username));

		debug("Call userService.createAccount()");
		System.out.println(userService.createAccount(server, username));
	}

	private void deleteAccount(String line) {
		String[] split = line.split(SPACE);

		if (split.length != 3) {
			debug("Call printUsage()");
			this.printUsage();
			return;
		}
		String server = split[1];
		String username = split[2];
		debug(String.format("server: %s", server));
		debug(String.format("username: %s", username));

		debug("Call userService.deleteAccount()");
		System.out.println(userService.deleteAccount(server, username));
	}

	private void balance(String line) {
		String[] split = line.split(SPACE);

		if (split.length != 3) {
			debug("Call printUsage()");
			this.printUsage();
			return;
		}
		String server = split[1];
		String username = split[2];
		debug(String.format("server: %s", server));
		debug(String.format("username: %s", username));

		debug("Call userService.balance()");
		System.out.println(userService.balance(server, username));
	}

	private void transferTo(String line) {
		String[] split = line.split(SPACE);

		if (split.length != 5) {
			debug("Call printUsage()");
			this.printUsage();
			return;
		}
		String server = split[1];
		String from = split[2];
		String dest = split[3];
		Integer amount = Integer.valueOf(split[4]);
		debug(String.format("server: %s", server));
		debug(String.format("from: %s", from));
		debug(String.format("dest: %s", dest));
		debug(String.format("amount: %s", amount));

		debug("Call userService.transferTo()");
		System.out.println(userService.transferTo(server, from, dest, amount));
	}

	private void printUsage() {
		System.out.println("Usage:\n" + "- createAccount <server> <username>\n"
		    + "- deleteAccount <server> <username>\n" + "- balance <server> <username>\n"
		    + "- transferTo <server> <username_from> <username_to> <amount>\n" + "- exit\n");
	}
}
