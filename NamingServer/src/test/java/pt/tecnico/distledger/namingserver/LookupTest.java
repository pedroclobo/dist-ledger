package pt.tecnico.distledger.namingserver;

import pt.tecnico.distledger.namingserver.domain.NamingServerState;
import pt.tecnico.distledger.namingserver.domain.ServerEntry;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class LookupTest {
	private static NamingServerState state;

	@BeforeEach
	public void setUp() {
		state = new NamingServerState();
	}

	@AfterEach
	public void tearDown() {
		state = null;
	}

	// Lookup a server
	@Test
	public void lookup() {
		state.register("DistLedger", "A", "localhost", 2001);
		state.register("DistLedger", "B", "localhost", 2002);
		state.register("DistLedger", "C", "localhost", 2003);
		state.register("Other Service", "A", "localhost", 2004);

		List<ServerEntry> servers = state.lookup("DistLedger", "A");

		assertEquals(1, servers.size());

		assertEquals("localhost", servers.get(0).getHost());
		assertEquals(2001, servers.get(0).getPort());
	}

	// The service name doesn't exist
	@Test
	public void serviceNameNotFound() {
		List<ServerEntry> servers = state.lookup("DistLedger", "A");

		assertEquals(0, servers.size());
	}

	// The qualifier doesn't exist
	@Test
	public void qualifierNotFound() {
		state.register("DistLedger", "A", "localhost", 2001);
		List<ServerEntry> servers = state.lookup("DistLedger", "B");

		assertEquals(0, servers.size());
	}

	// The qualifier is not specified
	@Test
	public void qualifierNotSpecified() {
		state.register("DistLedger", "A", "localhost", 2001);
		state.register("DistLedger", "B", "localhost", 2002);
		state.register("DistLedger", "C", "localhost", 2003);
		state.register("Other Service", "A", "localhost", 2004);

		List<ServerEntry> servers = state.lookup("DistLedger", "");

		assertEquals(3, servers.size());

		assertEquals("localhost", servers.get(0).getHost());
		assertEquals(2001, servers.get(0).getPort());

		assertEquals("localhost", servers.get(1).getHost());
		assertEquals(2002, servers.get(1).getPort());

		assertEquals("localhost", servers.get(2).getHost());
		assertEquals(2003, servers.get(2).getPort());
	}
}
