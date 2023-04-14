package pt.tecnico.distledger.adminclient;

import pt.tecnico.distledger.adminclient.grpc.AdminService;

import java.util.Scanner;

/**
 * The CommandParser class is responsible for parsing user input and calling the
 * corresponding methods of the UserService class based on the provided command.
 */
public class CommandParser {

	/**
	 * Set flag to true to print debug messages. The flag can be set using the
	 * -Ddebug=true command line option.
	 */
	private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

	private static final String SPACE = " ";
	private static final String ACTIVATE = "activate";
	private static final String DEACTIVATE = "deactivate";
	private static final String GET_LEDGER_STATE = "getLedgerState";
	private static final String GOSSIP = "gossip";
	private static final String HELP = "help";
	private static final String EXIT = "exit";

	private final AdminService adminService;

	/**
	 * Constructs and initilizes the CommandParser
	 *
	 * @param adminService adminService
	 */
	public CommandParser(AdminService adminService) {
		this.adminService = adminService;
	}

	/**
	 * Parses user input from the console and executes corresponding commands.
	 */
	void parseInput() {

		Scanner scanner = new Scanner(System.in);
		boolean exit = false;

		while (!exit) {
			System.out.print("> ");
			String line = scanner.nextLine()
			                     .trim();
			String cmd = line.split(SPACE)[0];
			debug(String.format("input line: %s", line));

			try {
				switch (cmd) {
				case ACTIVATE:
					this.activate(line);
					break;

				case DEACTIVATE:
					this.deactivate(line);
					break;

				case GET_LEDGER_STATE:
					this.dump(line);
					break;

				case GOSSIP:
					this.gossip(line);
					break;

				case HELP:
					this.printUsage();
					break;

				case EXIT:
					exit = true;
					break;

				default:
					System.out.println("Command '" + cmd + "' doesn't exist\n");
					break;
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
		scanner.close();
	}

	/**
	 * This method activates a server and prints out the result of calling the
	 * adminService.activate() method.
	 *
	 * @param line the input line that contains the command and server to activate
	 */
	private void activate(String line) {
		String[] split = line.split(SPACE);

		if (split.length != 2) {
			this.printUsage();
			return;
		}
		String server = split[1];

		System.out.println(adminService.activate(server));
	}

	/**
	 * This method deactivates a server and prints out the result of calling the
	 * adminService.activate() method.
	 *
	 * @param line the input line that contains the command and server to activate
	 */
	private void deactivate(String line) {
		String[] split = line.split(SPACE);

		if (split.length != 2) {
			this.printUsage();
			return;
		}
		String server = split[1];

		System.out.println(adminService.deactivate(server));
	}

	/**
	 * This method prints the ledger state of a server.
	 *
	 * @param line the input command
	 */
	private void dump(String line) {
		String[] split = line.split(SPACE);

		if (split.length != 2) {
			this.printUsage();
			return;
		}
		String server = split[1];

		System.out.printf(adminService.getLedgerState(server));
	}

	private void gossip(String line) {
		String[] split = line.split(SPACE);

		if (split.length != 3) {
			this.printUsage();
			return;
		}
		String fromServer = split[1];
		String toServer = split[2];

		System.out.println(adminService.gossip(fromServer, toServer));
	}

	/**
	 * This method prints the available commands.
	 */
	private void printUsage() {
		System.out.println("Usage:\n" + "- activate <server>\n" + "- deactivate <server>\n"
		    + "- getLedgerState <server>\n" + "- gossip <fromServer> <toServer>\n" + "- exit\n");
	}

	/**
	 * Helper method to print debug messages.
	 */
	private static void debug(String debugMessage) {
		if (DEBUG_FLAG) {
			System.err.println(debugMessage);
		}
	}
}
