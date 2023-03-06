package pt.tecnico.distledger.server;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.tecnico.distledger.server.domain.ServerState;

import java.io.IOException;

public class ServerMain {

	public static void main(String[] args) throws IOException, InterruptedException {
		// Receive and print arguments.
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
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
		final ServerState state = new ServerState();
		final BindableService admin = new AdminServiceImpl(state);
		final BindableService user =  new UserServiceImpl(state);

		// Create a new server to listen on port.
		Server server = ServerBuilder.forPort(port).addService(admin).addService(user).build();

		// Start the server
		server.start();

		// Do not exit the main thread. Wait until server is terminated.
		server.awaitTermination();
	}

}

