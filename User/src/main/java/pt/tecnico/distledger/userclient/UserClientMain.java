package pt.tecnico.distledger.userclient;

import pt.tecnico.distledger.userclient.grpc.UserService;
import pt.tecnico.distledger.userclient.grpc.NamingServerService;

public class UserClientMain {

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
		debug("Create CommandParser and UserService");
		UserService userService = new UserService(new NamingServerService("localhost", 5001));
		CommandParser parser = new CommandParser(userService);

		debug("Call parseInput()");
		parser.parseInput();

		userService.shutdown();
	}
}
