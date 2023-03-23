package pt.tecnico.distledger.namingserver;

import pt.tecnico.distledger.namingserver.domain.NamingServerState;
import pt.tecnico.distledger.namingserver.domain.ServerEntry;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class DeleteTest {
	private static NamingServerState state;

	@BeforeEach
	public void setUp() {
		state = new NamingServerState();
	}

	@AfterEach
	public void tearDown() {
		state = null;
	}

	// Delete a server
	@Test
	public void delete() {
		state.register("DistLedger", "A", "localhost", 2001);
		List<ServerEntry> servers = state.lookup("DistLedger", "A");

		assertEquals(1, servers.size());
		assertEquals("localhost", servers.get(0)
		                                 .getHost());
		assertEquals(2001, servers.get(0)
		                          .getPort());

		state.delete("DistLedger", "localhost", 2001);
		servers = state.lookup("DistLedger", "A");

		assertEquals(0, servers.size());
	}
}
