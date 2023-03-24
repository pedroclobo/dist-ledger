package pt.tecnico.distledger.userclient;

import pt.tecnico.distledger.userclient.grpc.UserService;
import pt.tecnico.distledger.namingserver.ClientNamingServerService;

import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import pt.tecnico.distledger.sharedutils.ClientFrontend;

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

		ClientFrontend<UserServiceGrpc.UserServiceBlockingStub> frontend = new ClientFrontend(
		    new UserServiceStubBuilder());

		UserService userService = new UserService(frontend);
		CommandParser parser = new CommandParser(userService);

		debug("Call parseInput()");
		parser.parseInput();

		frontend.shutdown();
	}
}
