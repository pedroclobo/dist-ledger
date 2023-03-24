package pt.tecnico.distledger.sharedutils;

import io.grpc.ManagedChannel;

public abstract class StubBuilder<T> {
	public abstract T build(ManagedChannel channel);
}
