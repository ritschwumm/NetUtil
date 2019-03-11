/*
 *  OSCException.scala
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

/**
 *  Exception thrown by some OSC related methods.
 *  Typical reasons are communication timeout and
 *  buffer underflows or overflows.
 */
public class OSCException
		extends IOException {
    /**
	 *  causeType : communication timeout
	 */
	public static final int TIMEOUT = 0;
	/**
	 *  causeType : SuperCollider replies "fail"
	 */
	public static final int FAILED  = 1;
	/**
	 *  causeType : buffer overflow or underflow
	 */
	public static final int BUFFER  = 2;
	/**
	 *  causeType : osc message has invalid format
	 */
	public static final int FORMAT  = 3;
	/**
	 *  causeType : osc message has invalid or unsupported type tags
	 */
	public static final int TYPETAG  = 4;
	/**
	 *  causeType : osc message cannot convert given java class to osc primitive
	 */
	public static final int JAVACLASS  = 5;
	/**
	 *  causeType : network error while receiving osc message
	 */
	public static final int RECEIVE  = 6;
	
	private					int			causeType;
	private static final	String[]	errMessages = {
        "errOSCTimeOut", "errOSCFailed", "errOSCBuffer", "errOSCFormat",
        "errOSCTypeTag", "errOSCArgClass", "errOSCReceive"
    };
	
	/**
	 *  Constructs a new <code>OSCException</code> with
	 *  the provided type of cause (e.g. <code>TIMEOUT</code>)
	 *  and descriptive message.
	 *
	 *  @param  causeType   cause of the exception
	 *  @param  message		human readable description of the exception,
     *                      may be <code>null</code>
	 */
    public OSCException(int causeType, String message) {
        super(NetUtil.getResourceString(errMessages[causeType]) +
                (message == null ? "" : (": " + message)));

        this.causeType = causeType;
    }
	
	/**
	 *  Queries the cause of the exception
	 *
	 *  @return cause of the exception, e.g. <code>BUFFER</code>
	 *			if a buffer underflow or overflow occurred
	 */
	public int getCauseType()
	{
		return causeType;
	}
	
	public String getLocalizedMessage()
	{
		return getMessage();
	}
}