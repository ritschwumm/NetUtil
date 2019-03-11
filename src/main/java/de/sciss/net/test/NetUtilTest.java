/*
 *  NetUtilTest.scala
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

package de.sciss.net.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.sciss.net.OSCBundle;
import de.sciss.net.OSCChannel;
import de.sciss.net.OSCClient;
import de.sciss.net.OSCConnectionListener;
import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCReceiver;
import de.sciss.net.OSCTransmitter;
import de.sciss.net.OSCServer;

/**
 *	Some static test run methods.
 */
public abstract class NetUtilTest {
    protected static boolean pause = false;

    private NetUtilTest() { /* empty */ }

    /**
     *	Tests the client functionality on a given protocol.
     *	This assumes SuperCollider server (scsynth) is running
     *	on the local machine, listening at the given protocol and
     *	port 57110.
     *
     *	@param	protocol	<code>UDP</code> or <code>TCP</code>
     */
    public static void client(String protocol) {
        postln("NetUtilTest.client( \"" + protocol + "\" )\n");
        postln("talking to localhost port 57110");

        final Object		sync = new Object();
        final OSCClient		c;
        OSCBundle			bndl1, bndl2;
        int 				nodeID;

        try {
            c = OSCClient.newUsing(protocol);
            c.setTarget(new InetSocketAddress(InetAddress.getLocalHost(), 57110));
            postln("  start()");
            c.start();
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        c.addOSCListener(new OSCListener() {
            public void messageReceived(OSCMessage m, SocketAddress addr, long time) {
                if (m.getName().equals("/n_end")) {
                    synchronized (sync) {
                        sync.notifyAll();
                    }
                }
            }
        });
        c.dumpOSC(OSCChannel.kDumpBoth, System.err);

        try {
            c.send(new OSCMessage("/notify", new Object[]{1}));
        } catch (IOException e3) {
            e3.printStackTrace();
        }

        for (int i = 0; i < 4; i++) {
            bndl1 	= new OSCBundle(System.currentTimeMillis() + 50);
            bndl2 	= new OSCBundle(System.currentTimeMillis() + 1550);
            nodeID 	= 1001 + i;
            bndl1.addPacket(new OSCMessage("/s_new", new Object[]{"default", nodeID, 1, 0}));
            bndl1.addPacket(new OSCMessage("/n_set", new Object[]{nodeID, "freq", (float) (Math.pow(2, (float) i / 6) * 441)}));
            bndl2.addPacket(new OSCMessage("/n_set", new Object[]{nodeID, "gate", -3f}));
            try {
                c.send(bndl1);
                c.send(bndl2);

                synchronized (sync) {
                    sync.wait();
                }
            } catch (InterruptedException e1) { /* ignored */} catch (IOException e2) {
                e2.printStackTrace();
            }
        }

        try {
            c.send(new OSCMessage("/notify", new Object[]{0}));
        } catch (IOException e3) {
            e3.printStackTrace();
        }

        c.dispose();
    }

    /**
     *	Tests the server functionality on a given protocol.
     *	This opens a server listening at port 0x5454. Recognized
     *	messages are <code>/pause</code>, <code>/quit</code>, <code>/dumpOSC</code>.
     *	See <code>NetUtil_Tests.rtf</code> for a way to check the server.
     *
     *	@param	protocol	<code>UDP</code> or <code>TCP</code>
     */
    public static void server(String protocol) {
        postln("NetUtilTest.server( \"" + protocol + "\" )\n");
        postln("listening at port 21588. recognized commands: /pause, /quit, /dumpOSC");

        final Object sync = new Object();
        final OSCServer c;
        try {
            c = OSCServer.newUsing(protocol, 0x5454);
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

		c.addConnectionListener(new OSCConnectionListener() {
			public void onConnected(InetSocketAddress local, InetSocketAddress remote) {
				postln("onConnected - remote = " + remote);
			}

			public void onDisconnected(InetSocketAddress local, InetSocketAddress remote) {
				postln("onDisconnected - remote = " + remote);
			}
		});

		c.addOSCListener(new OSCListener() {
            public void messageReceived(OSCMessage m, SocketAddress addr, long time) {
                try {
                    postln("send " + addr);
                    c.send(new OSCMessage("/done", new Object[]{m.getName()}), addr);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                if (m.getName().equals("/pause")) {
                    pause = true;
                    synchronized (sync) {
                        sync.notifyAll();
                    }
                } else if (m.getName().equals("/quit")) {
                    synchronized (sync) {
                        sync.notifyAll();
                    }
                } else if (m.getName().equals("/dumpOSC")) {
                	Object arg0 = m.getArgCount() > 0 ? m.getArg(0) : null;
                	int mode = (arg0 instanceof Number) ? ((Number) arg0).intValue() : 1;
                    c.dumpOSC(mode, System.err);
                }
            }
        });

        try {
            do {
                if (pause) {
                    postln("  waiting four seconds...");
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e1) { /* ignored */ }
                    pause = false;
                }
                postln("  start()");
                c.start();
                try {
                    synchronized (sync) {
                        sync.wait();
                    }
                } catch (InterruptedException e1) { /* ignore */ }

                postln("  stop()");
                c.stop();
            } while (pause);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        c.dispose();
    }

    /**
     *	Tests the performance of OSCMessage encoding and decoding.
     *	A cyclic random list of messages get decoded and encoded
     *	for five seconds, the number of codec operations during this
     *	interval is printed.
     *
     *	Benchmarks for MacBook Pro 2.0 GHz, Mac OS X 10.4.8, 1.5.0_07:
     *	NetUtil 0.33 (build 07-May-07) vs. Illposed JavaOSC (20060402):
     *	encoding : NetUtil roughly 220% faster
     *	decoding : Illposed roughly 50% faster
     *
     *	NetUtil 0.32 vs. 0.33 are very similar, decoding is
     *	a few percent faster in v0.33
     */
    public static void codecSpeed() {
        final ByteBuffer	b 			= ByteBuffer.allocateDirect(65536);
        final List<Object>	args 		= new ArrayList<Object>(1024);
        final Object[][] 	argsArgs	= new Object[1024][];
        final Random 		rnd 		= new Random(0x1234578L);
        final ByteBuffer[] 	b2 			= new ByteBuffer[1024];
        byte[] 				bytes 		= new byte[16];
        long t1;
        int cnt;
        OSCMessage msg;
        ByteBuffer b3;

		postln("Testing OSCMessage encoding speed...");
		for (int i = 0; i < argsArgs.length; i++) {
			args.clear();
			for (int j = 0; j < (i % 1024); j++) {
				switch (j % 5) {
					case 0:
						args.add(rnd.nextInt());
						break;
					case 1:
						args.add(rnd.nextFloat());
						break;
					case 2:
						args.add(rnd.nextLong());
						break;
					case 3:
						args.add(rnd.nextDouble());
						break;
					case 4:
						rnd.nextBytes(bytes);
						for (int k = 0; k < bytes.length; k++) bytes[k] = (byte) (Math.max(32, bytes[k]) & 0x7F);
						args.add(new String(bytes));
						break;
				}
			}
			argsArgs[i] = args.toArray();
		}

		try {
			t1 = System.currentTimeMillis();
			for (cnt = 0; (System.currentTimeMillis() - t1) < 5000; cnt++) {
				b.clear();
				new OSCMessage("/test", argsArgs[cnt % argsArgs.length]).encode(b);
			}
			postln(cnt + " messages encoded in 5 seconds.");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		postln("Testing OSCMessage decoding speed...");
		try {
			for (int i = 0; i < b2.length; i++) {
				args.clear();
				for (int j = 0; j < (i % 1024); j++) {
					switch (j % 3) {
						case 0:
							args.add(rnd.nextInt());
							break;
						case 1:
							args.add(rnd.nextFloat());
							break;
						case 2:
							rnd.nextBytes(bytes);
							for (int k = 0; k < bytes.length; k++) bytes[k] = (byte) (Math.max(32, bytes[k]) & 0x7F);
							args.add(new String(bytes));
							break;
					}
				}
				msg = new OSCMessage("/test", args.toArray());
				b3 = ByteBuffer.allocateDirect(msg.getSize());
				msg.encode(b3);
				b2[i] = b3;
			}

			t1 = System.currentTimeMillis();
			for (cnt = 0; (System.currentTimeMillis() - t1) < 5000; cnt++) {
				b3 = b2[cnt % b2.length];
				b3.clear();
				b3.position(8);    // have to skip msg name
				OSCMessage.decodeMessage("/test", b3);
			}
			postln(cnt + " messages decoded in 5 seconds.");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }

    protected static volatile boolean received;

    /**
     *	Creates two receivers and two transmitters, one of each
     *	being restricted to loopback. Sends from each transmitter
     *	to each receiver. The expected result is that all messages
     *	arrive except those sent from the local host transmitter
     *	to loopback receiver (trns2 to rcv1, i.e. "five", "six").
     */
	public static void pingPong() {
        final String[]	protos	= { OSCChannel.UDP, OSCChannel.TCP };
        final String[]	words	= { "One", "Two", "Three", "Four", "Five", "Six",
                                    "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve" };
        final Object	sync	= new Object();
        int wordIdx = 0;
        int failures = 0;

		System.out.println("\n---------- RECEIVER / TRANSMITTER ----------");
		for (int i = 0; i < 1; i++) { // note: tcp requires a server socket
			final String proto = protos[i];
			System.out.println("---------- Testing protocol '" + proto + "' ----------");
			for (int j = 0; j < 4; j++) {
				final boolean rcvLoop = j / 2 == 0;
				final boolean trnsLoop = j % 2 == 0;
				final boolean shouldFail = !trnsLoop && rcvLoop;
				received = false;

				System.out.println("Receiver loopBack = " + rcvLoop + "; Transmitter loopBack = " + trnsLoop);

				OSCReceiver rcv = null;
				OSCTransmitter trns = null;

				try {
					rcv = OSCReceiver.newUsing(proto, 0, rcvLoop);
					trns = OSCTransmitter.newUsing(proto, 0, trnsLoop);

					rcv.dumpOSC(OSCChannel.kDumpText, System.out);
					rcv.startListening();
					rcv.addOSCListener(new OSCListener() {
						public void messageReceived(OSCMessage msg, SocketAddress addr, long when) {
							System.out.println("   Received msg '" + msg.getName() + "' from " + addr);
							synchronized (sync) {
								received = true;
								sync.notifyAll();
							}
						}
					});

					trns.connect();
					final int targetPort = rcv.getLocalAddress().getPort();
					final InetSocketAddress targetAddr = trnsLoop ?
							new InetSocketAddress("127.0.0.1", targetPort) :
							new InetSocketAddress(InetAddress.getLocalHost(), targetPort);
					trns.setTarget(targetAddr);
					trns.send(new OSCMessage("/test", new Object[]{words[wordIdx], wordIdx + 1}));

					try {
						synchronized (sync) {
							sync.wait(2000);
						}
					} catch (InterruptedException e1) { /* ignore */ }
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				if (received != shouldFail) {
					System.out.println("... OK");
				} else {
					System.out.println("\n... FAILED!!!\n");
					failures++;
				}

				if (rcv 	!= null) rcv	.dispose();
				if (trns	!= null) trns	.dispose();
				wordIdx = (wordIdx + 1) % words.length;
			}
		}

        System.out.println( "\n------------- CLIENT / SERVER --------------" );
		for (final String proto : protos) {
			System.out.println("---------- Testing protocol '" + proto + "' ----------");
			for (int j = 0; j < 4; j++) {
				final boolean clientLoop = j / 2 == 0;
				final boolean serverLoop = j % 2 == 0;
				final boolean shouldFail = !clientLoop && serverLoop;
				received = false;

				System.out.println("Client loopBack = " + clientLoop + "; Server loopBack = " + serverLoop);

				OSCClient client = null;
				OSCServer server = null;

				try {
					client = OSCClient.newUsing(proto, 0, clientLoop);
					server = OSCServer.newUsing(proto, 0, serverLoop);

					server.dumpOSC(OSCChannel.kDumpText, System.out);
					server.start();
					server.addOSCListener(new OSCListener() {
						public void messageReceived(OSCMessage msg, SocketAddress addr, long when) {
							System.out.println("   Received msg '" + msg.getName() + "' from " + addr);
							synchronized (sync) {
								received = true;
								sync.notifyAll();
							}
						}
					});

					final int targetPort = server.getLocalAddress().getPort();
					final InetSocketAddress targetAddr = clientLoop ?
							new InetSocketAddress("127.0.0.1", targetPort) :
							new InetSocketAddress(InetAddress.getLocalHost(), targetPort);
					client.setTarget(targetAddr);
					client.connect();
					client.send(new OSCMessage("/test", new Object[]{words[wordIdx], wordIdx + 1}));

					try {
						synchronized (sync) {
							sync.wait(2000);
						}
					} catch (InterruptedException e1) { /* ignore */ }
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				if (received != shouldFail) {
					System.out.println("... OK");
				} else {
					System.out.println("\n... FAILED!!!\n");
					failures++;
				}

				if (client != null) client.dispose();
				if (server != null) server.dispose();
				wordIdx = (wordIdx + 1) % words.length;
			}
		}

		System.out.println("\nNumber of tests failed: " + failures);
	}

	protected static void postln(String s) {
		System.err.println(s);
	}
}
