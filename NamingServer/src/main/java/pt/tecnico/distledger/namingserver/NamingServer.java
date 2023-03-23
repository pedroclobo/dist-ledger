package pt.tecnico.distledger.namingserver;

import pt.tecnico.distledger.namingserver.domain.NamingServerState;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.Scanner;

public class NamingServer {

	public static void main(String[] args) throws IOException, InterruptedException {

		// Get port.
		final int port = Integer.parseInt(args[0]);

		// Initialize services.
		final NamingServerState state = new NamingServerState();
		final BindableService namingServer = new NamingServerServiceImpl(state);

		// Create a new server to listen on port.
		Server server = ServerBuilder.forPort(port).addService(namingServer).build();

		// Start the server
		server.start();

		System.out.println("Press enter to shutdown");
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();

		// Do not exit the main thread. Wait until server is terminated.
		server.shutdown();
	}

}
