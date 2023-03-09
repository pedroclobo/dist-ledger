package pt.tecnico.distledger.adminclient;

import pt.tecnico.distledger.adminclient.grpc.AdminService;

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
	private static final String ACTIVATE = "activate";
	private static final String DEACTIVATE = "deactivate";
	private static final String GET_LEDGER_STATE = "getLedgerState";
	private static final String GOSSIP = "gossip";
	private static final String HELP = "help";
	private static final String EXIT = "exit";

	private final AdminService adminService;

	public CommandParser(AdminService adminService) {
		this.adminService = adminService;
	}

	void parseInput() {

		Scanner scanner = new Scanner(System.in);
		boolean exit = false;

		while (!exit) {
			System.out.print("> ");
			String line = scanner.nextLine().trim();
			String cmd = line.split(SPACE)[0];
			debug(String.format("input line: %s", line));
			debug(String.format("command: %s", cmd));

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

		}
	}

	private void activate(String line) {
		String[] split = line.split(SPACE);

		if (split.length != 2) {
			debug("Call printUsage()");
			this.printUsage();
			return;
		}
		String server = split[1];
		debug(String.format("server: %s", server));

		debug("Call adminService.activate()");
		System.out.println(adminService.activate());
	}

	private void deactivate(String line) {
		String[] split = line.split(SPACE);

		if (split.length != 2) {
			debug("Call printUsage()");
			this.printUsage();
			return;
		}
		String server = split[1];
		debug(String.format("server: %s", server));

		debug("Call adminService.deactivate()");
		System.out.println(adminService.deactivate());
	}

	private void dump(String line) {
		String[] split = line.split(SPACE);

		if (split.length != 2) {
			debug("Call printUsage()");
			this.printUsage();
			return;
		}
		String server = split[1];
		debug(String.format("server: %s", server));

		debug("Call adminService.getLedgerState()");
		System.out.printf(adminService.getLedgerState());
	}

	@SuppressWarnings("unused")
	private void gossip(String line) {
		/* TODO Phase-3 */
		System.out.println("TODO: implement gossip command (only for Phase-3)");
	}

	private void printUsage() {
		System.out.println("Usage:\n" + "- activate <server>\n" + "- deactivate <server>\n" + "- getLedgerState <server>\n" + "- gossip <server>\n" + "- exit\n");
	}

}
