package pt.tecnico.distledger.adminclient;

import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;

import pt.tecnico.distledger.adminclient.grpc.AdminService;
import pt.tecnico.distledger.adminclient.grpc.AdminServiceStubBuilder;
import pt.tecnico.distledger.sharedutils.ClientFrontend;

/**
 * Entry point for the Admin user.
 */
public class AdminClientMain {

	/**
	 * Set flag to true to print debug messages. The flag can be set using the
	 * -Ddebug command line option.
	 */
	private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

	/**
	 * Helper method to print debug messages.
	 *
	 * @param debugMessage message to be printed
	 */
	private static void debug(String debugMessage) {
		if (DEBUG_FLAG) {
			System.err.println(debugMessage);
		}
	}

	/**
	 * Creates an instance of the AdminService class and a CommandParser instance
	 * with it, calls the parseInput() method on the CommandParser instance to
	 * handle user input, and finally shuts down the AdminService instance.
	 */
	public static void main(String args[]) {
		debug("Create CommandParser and AdminService");

		ClientFrontend<AdminServiceGrpc.AdminServiceBlockingStub> frontend = new ClientFrontend<>(
		    new AdminServiceStubBuilder());

		AdminService adminService = new AdminService(frontend);
		CommandParser parser = new CommandParser(adminService);

		debug("Call parseInput()");
		parser.parseInput();

		frontend.shutdown();
	}
}
