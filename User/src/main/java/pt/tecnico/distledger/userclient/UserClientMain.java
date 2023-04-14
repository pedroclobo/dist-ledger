package pt.tecnico.distledger.userclient;

import pt.tecnico.distledger.userclient.grpc.UserService;
import pt.tecnico.distledger.namingserver.ClientNamingServerService;

import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import pt.tecnico.distledger.sharedutils.ClientFrontend;

public class UserClientMain {

	public static void main(String[] args) {
		ClientFrontend<UserServiceGrpc.UserServiceBlockingStub> frontend = new ClientFrontend(
		    new UserServiceStubBuilder());

		UserService userService = new UserService(frontend);
		CommandParser parser = new CommandParser(userService);

		parser.parseInput();

		frontend.shutdown();
	}
}
