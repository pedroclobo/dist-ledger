package pt.tecnico.distledger.server;

import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;

import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.grpc.CrossServerService;
import pt.tecnico.distledger.server.grpc.DistLedgerCrossServerServiceStubBuilder;
import pt.tecnico.distledger.sharedutils.ServerFrontend;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.util.Scanner;
import java.io.IOException;

public class ServerMain {

	private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

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

		// Initialize services.
		final ServerTimestamp timestamp = new ServerTimestamp(qualifer);
		final ServerState state = new ServerState(timestamp);
		final ServerMode mode = new ServerMode();

		ServerFrontend<DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub> frontend = new ServerFrontend(
		    qualifer, "localhost", port, new DistLedgerCrossServerServiceStubBuilder());

		final CrossServerService crossServerService = new CrossServerService(frontend);
		final BindableService admin = new AdminServiceImpl(state, mode, timestamp, crossServerService);
		final BindableService cross = new CrossServerServiceImpl(state, mode, timestamp);
		final BindableService user = new UserServiceImpl(state, mode, timestamp, crossServerService);

		// Create a new server to listen on port.
		Server server = ServerBuilder.forPort(port)
		                             .addService(admin)
		                             .addService(user)
		                             .addService(cross)
		                             .build();

		// Start the server
		server.start();

		System.out.println("Press enter to shutdown");
		debug("\n");
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
		scanner.close();

		frontend.shutdown();
		server.shutdown();
	}

	/** Helper method to print debug messages. */
	private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
	}
}
