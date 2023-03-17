package pt.tecnico.distledger.namingserver;

import pt.tecnico.distledger.namingserver.domain.NamingServerState;
import pt.tecnico.distledger.namingserver.domain.ServerEntry;

import pt.tecnico.distledger.namingserver.exceptions.ServerAlreadyExistsException;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class RegisterTest {
	private static NamingServerState state;

	@BeforeEach
	public void setUp() {
		state = new NamingServerState();
	}

	@AfterEach
	public void tearDown() {
		state = null;
	}

	// Register a server
	@Test
	public void register() {
		state.register("DistLedger", "A", "localhost", 2001);
		List<ServerEntry> servers = state.lookup("DistLedger", "A");
		assertEquals(1, servers.size());

		ServerEntry server = servers.get(0);
		assertEquals("localhost", server.getHost());
		assertEquals(2001, server.getPort());
	}

	// Register a server with a service name that already exists
	@Test
	public void registerWithSameServiceName() {
		state.register("DistLedger", "A", "localhost", 2001);
		state.register("DistLedger", "A", "localhost", 2002);

		List<ServerEntry> servers = state.lookup("DistLedger", "A");
		assertEquals(2, servers.size());

		assertEquals("localhost", servers.get(0).getHost());
		assertEquals(2001, servers.get(0).getPort());

		assertEquals("localhost", servers.get(1).getHost());
		assertEquals(2002, servers.get(1).getPort());
	}

	// Register a server that already exists within the same service
	@Test
	public void registerWithSameServer() {
		state.register("DistLedger", "A", "localhost", 2001);

		assertThrows(ServerAlreadyExistsException.class, () -> state.register("DistLedger", "A", "localhost", 2001));

		List<ServerEntry> servers = state.lookup("DistLedger", "A");
		assertEquals(1, servers.size());

		ServerEntry server = servers.get(0);
		assertEquals("localhost", server.getHost());
		assertEquals(2001, server.getPort());
	}
}
