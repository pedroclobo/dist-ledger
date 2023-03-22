package pt.tecnico.distledger.server;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.tecnico.distledger.server.domain.ServerState;

import pt.tecnico.distledger.server.grpc.NamingServerService;
import pt.tecnico.distledger.server.grpc.CrossServerService;

import java.util.Scanner;
import java.io.IOException;

public class ServerMain {

	private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

	/** Helper method to print debug messages. */
	private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		// Receive and print arguments.
		debug(String.format("Received %d arguments", args.length));
		for (int i = 0; i < args.length; i++) {
			debug(String.format("arg[%d] = %s", i, args[i]));
		}

		// Check arguments.
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port qualifier%n", ServerMain.class.getName());
			return;
		}

		// Get arguments.
		final int port = Integer.parseInt(args[0]);
		final String qualifer = args[1];

		// Register server on naming server.
		final NamingServerService namingServerService = new NamingServerService("localhost", 5001);
		namingServerService.register("DistLedger", qualifer, "localhost", port);

		// Initialize services.
		final ServerState state = new ServerState();
		final ServerMode mode = new ServerMode();
		final ServerRole role = new ServerRole(qualifer);
		final CrossServerService crossServerService = new CrossServerService(namingServerService);
		final BindableService admin = new AdminServiceImpl(state, mode);
		final BindableService cross = new CrossServerServiceImpl(state, mode);
		final BindableService user = new UserServiceImpl(state, mode, role, crossServerService);

		// Create a new server to listen on port.
		Server server = ServerBuilder.forPort(port).addService(admin).addService(user).addService(cross).build();

		// Start the server
		server.start();

		System.out.println("Press enter to shutdown");
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();

		// Unregister server from naming server.
		namingServerService.delete("DistLedger", "localhost", port);
		namingServerService.shutdown();

		crossServerService.shutdown();

		server.shutdown();
	}

}
