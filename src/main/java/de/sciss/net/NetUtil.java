/*
 *  NetUtil.scala
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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import de.sciss.net.test.NetUtilTest;

/**
 *	A static class which contains information
 *	methods. In a future version it may contain
 *	more useful utility methods.
 */
public class NetUtil {
	private static final String VERSION = "1.1.0";
	private static final ResourceBundle resBundle = ResourceBundle.getBundle("NetUtilStrings");

	private NetUtil() { /* empty */ }

	/**
	 *	This method gets called when one tries
	 *	to start the .jar file directly.
	 *	It prints copyright information and
	 *	quits. It also offers to run some built-in tests.
	 */
	public static void main(String[] args) {
		boolean hasChoice = false;

		if (args.length == 1) {
			if (args[0].equals("--testTCPClient")) {
				hasChoice = true;
				NetUtilTest.client(OSCChannel.TCP);
			} else if (args[0].equals("--testUDPClient")) {
				hasChoice = true;
				NetUtilTest.client(OSCChannel.UDP);
			} else if (args[0].equals("--testTCPServer")) {
				hasChoice = true;
				NetUtilTest.server(OSCChannel.TCP);
			} else if (args[0].equals("--testUDPServer")) {
				hasChoice = true;
				NetUtilTest.server(OSCChannel.UDP);
			} else if (args[0].equals("--testCodecSpeed")) {
				hasChoice = true;
				NetUtilTest.codecSpeed();
			} else if (args[0].equals("--testPingPong")) {
				hasChoice = true;
				NetUtilTest.pingPong();
			}
		}

		if (!hasChoice) {
			System.err.println("\nNetUtil v" + VERSION + "\n" +
				getCopyrightString() + "\n\n" +
				getCreditsString() + "\n\n  " +
				getResourceString( "errIsALibrary" ));

			System.out.println("\nThe following demos are available:\n" +
				"  --testTCPClient\n" +
				"  --testUDPClient\n" +
				"  --testTCPServer\n" +
				"  --testUDPServer\n" +
				"  --testCodecSpeed\n" +
				"  --testPingPong\n"
			);
			System.exit(1);
		}
    }
	
	/**
	 *	Returns the library's version.
	 *
	 *	@return	the current version of NetUtil
	 */
	public static String getVersion() {
		return VERSION;
	}

	/**
	 *	Returns a copyright information string
	 *	about the library
	 *
	 *	@return	text string which can be displayed
	 *			in an about box
	 */
	public static String getCopyrightString() {
		return NetUtil.getResourceString("copyright");
	}

	/**
	 *	Returns a license and website information string
	 *	about the library
	 *
	 *	@return	text string which can be displayed
	 *			in an about box
	 */
	public static String getCreditsString() {
		return NetUtil.getResourceString("credits");
	}

	/**
	 *	Returns a string from the library's string
	 *	resource bundle (currently localized
	 *	english and german). This is used by the
	 *	classes of the library, you shouldn't use
	 *	it yourself.
	 *
	 *	@param	key	lookup dictionary key
	 *	@return	(localized) human readable string for
	 *			the given key or placeholder string if
	 *			the resource wasn't found
	 */
	public static String getResourceString(String key) {
		try {
			return resBundle.getString(key);
		} catch (MissingResourceException e1) {
			return ("[Missing Resource: " + key + "]");
		}
	}
}