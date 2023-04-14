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
	 * Creates an instance of the AdminService class and a CommandParser instance
	 * with it, calls the parseInput() method on the CommandParser instance to
	 * handle user input, and finally shuts down the AdminService instance.
	 */
	public static void main(String args[]) {
		ClientFrontend<AdminServiceGrpc.AdminServiceBlockingStub> frontend = new ClientFrontend<>(
		    new AdminServiceStubBuilder());

		AdminService adminService = new AdminService(frontend);
		CommandParser parser = new CommandParser(adminService);

		parser.parseInput();

		frontend.shutdown();
	}
}
