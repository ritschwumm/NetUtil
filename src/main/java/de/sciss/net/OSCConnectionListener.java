package de.sciss.net;

import java.net.InetSocketAddress;

public interface OSCConnectionListener {

	void onConnected(InetSocketAddress local, InetSocketAddress remote);

	void onDisconnected(InetSocketAddress local, InetSocketAddress remote);
}
