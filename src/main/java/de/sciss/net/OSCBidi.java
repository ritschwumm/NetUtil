/*
 *  OSCBidi.scala
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

import java.io.IOException;
import java.io.PrintStream;

/**
 *	An interface describing common functionality in bidirectional OSC communicators.
 */
public interface OSCBidi
		extends OSCChannel {
	/**
	 *	Starts the communicator.
	 *
	 *	@throws	IOException	if a networking error occurs
	 */
	public void start() throws IOException;

	/**
	 *	Checks whether the communicator is active (was started) or not (is stopped).
	 *
	 *	@return	<code>true</code> if the communicator is active, <code>false</code> otherwise
	 */
	public boolean isActive();

	/**
	 *	Stops the communicator.
	 *
	 *	@throws	IOException	if a networking error occurs
	 */
	public void stop() throws IOException;

	/**
	 *	Changes the way incoming messages are dumped
	 *	to the console. By default incoming messages are not
	 *	dumped. Incoming messages are those received
	 *	by the client from the server, before they
	 *	get delivered to registered <code>OSCListener</code>s.
	 *
	 *	@param	mode	see <code>dumpOSC( int )</code> for details
	 *	@param	stream	the stream to print on, or <code>null</code> which
	 *					is shorthand for <code>System.err</code>
	 *
	 *	@see	#dumpOSC( int, PrintStream )
	 *	@see	#dumpOutgoingOSC( int, PrintStream )
	 */
	public void dumpIncomingOSC(int mode, PrintStream stream);

	/**
	 *	Changes the way outgoing messages are dumped
	 *	to the console. By default outgoing messages are not
	 *	dumped. Outgoing messages are those send via
	 *	<code>send</code>.
	 *
	 *	@param	mode	see <code>dumpOSC( int )</code> for details
	 *	@param	stream	the stream to print on, or <code>null</code> which
	 *					is shorthand for <code>System.err</code>
	 *
	 *	@see	#dumpOSC( int, PrintStream )
	 *	@see	#dumpIncomingOSC( int, PrintStream )
	 */
	public void dumpOutgoingOSC(int mode, PrintStream stream);
}
