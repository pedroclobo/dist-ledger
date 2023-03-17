package pt.tecnico.distledger.adminclient;

import pt.tecnico.distledger.adminclient.grpc.AdminService;
import pt.tecnico.distledger.adminclient.grpc.NamingServerService;

public class AdminClientMain {

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

	public static void main(String[] args) {
		debug("Create CommandParser and AdminService");
		AdminService adminService = new AdminService(new NamingServerService("localhost", 5001));
		CommandParser parser = new CommandParser(adminService);

		debug("Call parseInput()");
		parser.parseInput();

		adminService.shutdown();
	}
}
