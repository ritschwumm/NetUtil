/*
 *  OSCListener.scala
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

import java.net.SocketAddress;

/**
 *  The <code>OSCListener</code>
 *  interface is used to register
 *  a client to an <code>OSCReceiver</code>
 *  object which will be notified when
 *  an incoming message was received.
 *	<p>
 *	See <code>OSCReceiver</code> for a code example.
 *	<P>
 *	Note that these methods are typically called from the OSC receiver thread
 *	which is not the regular AWT event dispatcher thread. You may often want
 *	to defer actual code to the event thread. You can do this by adding the
 *	received messages to a list and invoking the actual code using
 *	<code>EventQueue.invokeLater()</code>. This is particularly required when
 *	dealing with GUI processes which require methods to be called in the event
 *	thread. A future version of NetUtil may include a utility deferrer class.
 *
 *  @see	OSCReceiver
 *	@see	java.awt.EventQueue#invokeLater( Runnable )
 */
public interface OSCListener {
	/**
	 *  Called when a new OSC message
	 *  arrived at the receiving local socket.
	 *
	 *  @param  msg     the newly arrived and decoded message
     *  @param  sender  who sent the message
	 *	@param	time	the time tag as returned by <code>OSCBundle.getTimeTag()</code>
	 *					; or <code>OSCBundle.NOW</code> if no time tag was specified
	 *					or the message is expected to be processed immediately
	 */
	public void messageReceived(OSCMessage msg, SocketAddress sender, long time);
}
