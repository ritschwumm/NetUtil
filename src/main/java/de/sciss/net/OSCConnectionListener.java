/*
 *  OSCConnectionListener.scala
 *  (NetUtil)
 *
 *  Copyright (c) 2004-2018 Hanns Holger Rutz. All rights reserved.
 *
 *	This software is published under the GNU Lesser General Public License v2.1+
 *
 *
 *	For further information, please contact Hanns Holger Rutz at
 *	contact@sciss.de
 */

package de.sciss.net;

import java.net.InetSocketAddress;

public interface OSCConnectionListener {
	/** Invoked when the OSC client or server have made a connection to a target. */
	public void onConnected(InetSocketAddress local, InetSocketAddress remote);

	/** Invoked when the OSC client or server have dsiconnected from a target.
	 *	This also happens when disposing the object.
	 *
	 * @param remote	the former connection's remote address. May be <code>null</code>!
	 */
	public void onDisconnected(InetSocketAddress local, InetSocketAddress remote);
}
