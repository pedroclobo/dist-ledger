package pt.tecnico.distledger.server;

import pt.tecnico.distledger.server.domain.ServerState;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateServerStateTest {
	private static ServerState state;

	@BeforeEach
	public void setUp() {
		state = new ServerState();
	}

	@AfterEach
	public void tearDown() {
		state = null;
	}

	@Test
	public void initState() {
		assertEquals(state.getLedger()
		                  .size(),
		    0);
	}
}
