/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-12 Janus Core Developers
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.janusproject.kernel.network.jxse.jxta.impl;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.JxtaBiDiPipe;
import net.jxta.util.JxtaServerPipe;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.network.jxse.jxta.JXTANetworkHandler;
import org.janusproject.kernel.util.random.RandomNumber;
import org.janusproject.kernel.util.throwable.Throwables;


/**
 * The abstract class of a JXTA group used inside Janus.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavenartifactid $ArtifactId$
 * @mavengroupid $GroupId$
 * @see JanusGroupJxtaGroup
 * @see ApplicationJxtaGroup
 */
abstract class JanusJXTAGroup extends JxtaGroup {
	
	private static final String MSG_NAMESPACE_SENDING = "JanusMessageSending"; //$NON-NLS-1$
	private static final String MSG_ELEM_OBJ = "JanusMessage:Object"; //$NON-NLS-1$
	private static final String MSG_ELEM_COMM_TYPE = "JanusMessage:CommunicationType"; //$NON-NLS-1$
	private static final String MSG_ELEM_REVEICER_RETURN_ADDRESS = "JanusMessage:ReceiverAddress"; //$NON-NLS-1$

	private static final String COMM_TYPE_BROADCAST = "Broadcast"; //$NON-NLS-1$
	private static final String COMM_TYPE_DIRECT_SPECIFIED = "DirectSpecified"; //$NON-NLS-1$
	private static final String COMM_TYPE_DIRECT_RANDOM = "DirectRandom"; //$NON-NLS-1$
	
	private final static int MAX_SEND_MESSAGE_RETRY = 10;
	
	private Boolean joined = false;
	
	/**
	 * @param adapter is the JXTA network handler supporting this Janus JXTA group.
	 * @param peerGroup is the JXTA group.
	 * @param parent the the parent JXTA group.
	 */
	public JanusJXTAGroup(JXTANetworkHandler adapter, PeerGroup peerGroup, JxtaGroup parent) {
		super(adapter,peerGroup,parent);
	}
	
	/**
	 * Join the group.
	 * 
	 * @throws Exception
	 */
	public void join() throws Exception {
		synchronized (this.joined) {
			if (!this.joined) {
				PipeAdvertisement pipeAdvertisment = PipeUtil.getAdvertisement(getPeerGroup(), getPeerGroup().getPeerName(), PipeService.UnicastType, null);

				DiscoveryService ds = getPeerGroup().getDiscoveryService();
				ds.publish(pipeAdvertisment);
				ds.remotePublish(pipeAdvertisment);

				ExecutorService executors = this.networkHandler.getJXTAExecutorService();
				
				executors.submit(new Server());
				executors.submit(new PipeDiscover());

				getPeerGroup().getRendezVousService().setAutoStart(true, 12000);
				this.joined = true;
			}
		}

	}
	
	/**
	 * Leave the group.
	 * 
	 * @throws Exception
	 */
	public void leave() throws Exception {
		// FIXME complete this method
	}
	
	/**
	 * Send the message in the JXTA group.
	 * 
	 * @param message describes the message and its context.
	 * @return the adress of the receiver agent.
	 * @throws IOException
	 */
	public Address sendMessage(Message message) throws IOException {
		for (int i = 0; i < MAX_SEND_MESSAGE_RETRY; i++) {
			PipeAdvertisement pAdv = findCandidateKernel();
			if (pAdv != null) {
				Address receiver = sendMessage(message, false, pAdv);
				if (receiver != null) {
					return receiver;
				}
			}
		}
		return null;
	}

	private Address sendMessage(Message message, boolean isBroadcast, PipeAdvertisement pAdv) throws IOException {
		OutgoingConnectionHandler handler = new OutgoingConnectionHandler(pAdv);
		net.jxta.endpoint.Message msg = buildJxtaMessage(message, isBroadcast);
		return handler.send(msg, isBroadcast);
	}

	/** Broadcast the given message.
	 * 
	 * @param message describes the message and its context.
	 * @throws IOException
	 */
	public void broadcastMessage(Message message) throws IOException {
		// very hard implementation, will modified to use propagate
		List<PipeAdvertisement> list = PipeUtil.getAdvertisements(getPeerGroup(), null);
		for (PipeAdvertisement pipeAdvertisement : list) {
			// make sure it is not us
			if (!this.networkHandler.getKernelAddress().toString().equals(pipeAdvertisement.getName())) {
				sendMessage(message, true, pipeAdvertisement);
			}

		}
	}

	private static net.jxta.endpoint.Message buildJxtaMessage(Message message, boolean isBroadcast) throws IOException {
		net.jxta.endpoint.Message msg = new net.jxta.endpoint.Message();
		if (isBroadcast) {
			MessageUtils.addStringToMessage(msg, MSG_NAMESPACE_SENDING, MSG_ELEM_COMM_TYPE, COMM_TYPE_BROADCAST);
		}
		else {
			Address adr = message.getReceiver();
			if (adr == null) {
				MessageUtils.addStringToMessage(msg, MSG_NAMESPACE_SENDING, MSG_ELEM_COMM_TYPE, COMM_TYPE_DIRECT_RANDOM);
			}
			else {
				MessageUtils.addStringToMessage(msg, MSG_NAMESPACE_SENDING, MSG_ELEM_COMM_TYPE, COMM_TYPE_DIRECT_SPECIFIED);
			}
		}

		MessageUtils.addObjectToMessage(msg, MSG_NAMESPACE_SENDING, MSG_ELEM_OBJ, message);
		return msg;
	}

	private PipeAdvertisement findCandidateKernel() {
		List<PipeAdvertisement> list = PipeUtil.getAdvertisements(getPeerGroup(), null);
		while (!list.isEmpty()) {
			int idx = RandomNumber.nextInt(list.size());
			PipeAdvertisement a = list.get(idx);
			if (this.networkHandler.getKernelAddress().toString().equals(a.getName())) {
				list.remove(idx);
			} else {
				return a;
			}

		}
		return null;
	}
	
	/**
	 * Invoked when a message coming from another peer should be processed.
	 * 
	 * @param janusMessage is the icoming message.
	 * @param isBroadcast indicates if the message was broadcasted by the source.
	 * @return the address of the receiver agent or role.
	 */
	protected abstract Address processIncomingMessage(Message janusMessage, boolean isBroadcast);
	
	/**
	 * @author $Author: srodriguez$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class Server implements Runnable {
		JxtaServerPipe serverPipe = null;

		public Server() {
			// Empty constructor to avoid synthetic accessor
		}

		/** {@inheritDoc}
		 */
		@Override
		public void run() {
			try {
				Thread.currentThread().setName(
						Locale.getString(JanusJXTAGroup.class,
								"SERVER_THREADS_NAME", //$NON-NLS-1$
								getPeerGroup().getPeerName()));
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				//
			}
			try {
				if (this.serverPipe == null) {
					this.serverPipe = new JxtaServerPipe(
							getPeerGroup(), 
							PipeUtil.getAdvertisement(
									getPeerGroup(), 
									getPeerGroup().getPeerName(), 
									PipeService.UnicastType, 
									null));
					// lock forever
					this.serverPipe.setPipeTimeout(0);
				}

				while (true) {
					JxtaBiDiPipe bidiPipe = this.serverPipe.accept();
					if (bidiPipe != null) {
						IncommingConnectionHandler handler = new IncommingConnectionHandler();
						handler.attachBiDiPipe(bidiPipe);
					}
				}

			} catch (AssertionError ae) {
				throw ae;
			} catch (IOException e) {
				Logger.getLogger(getClass().getName()).severe(Throwables.toString(e));
			}

		}

	}
	
	/**
	 * @author $Author: srodriguez$
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class IncommingConnectionHandler implements PipeMsgListener {

		private JxtaBiDiPipe pipe = null;

		public IncommingConnectionHandler() {
			//
		}

		/** Associate this handler to a BiDiPipe.
		 * 
		 * @param pipe
		 */
		public void attachBiDiPipe(JxtaBiDiPipe pipe) {
			assert(pipe!=null); 
			this.pipe = pipe;
			pipe.setMessageListener(this);
		}

		/** {@inheritDoc}
		 */
		@Override
		public void pipeMsgEvent(PipeMsgEvent event) {
			net.jxta.endpoint.Message message = event.getMessage();
			if (message == null || this.pipe==null) {
				return;
			}
			try {
				Message janusMessage = (Message) MessageUtils.getObjectFromMessage(
						message, 
						JanusJXTAGroup.MSG_NAMESPACE_SENDING, 
						JanusJXTAGroup.MSG_ELEM_OBJ);
				boolean isBroadcast = COMM_TYPE_BROADCAST.equals(MessageUtils.getStringFromMessage(message, MSG_NAMESPACE_SENDING, MSG_ELEM_COMM_TYPE));
				

				Address receiver = JanusJXTAGroup.this.processIncomingMessage(janusMessage, isBroadcast);
				
				if (!isBroadcast) {
					// REPLAY the receivers address
					net.jxta.endpoint.Message replay = new net.jxta.endpoint.Message();
					MessageUtils.addObjectToMessage(replay, 
							MSG_NAMESPACE_SENDING, 
							MSG_ELEM_REVEICER_RETURN_ADDRESS, 
							receiver);
					this.pipe.sendMessage(replay);
				}
				this.pipe.close();
				this.pipe.setMessageListener(null);
				this.pipe = null;
			}
			catch (AssertionError ae) {
				throw ae;
			}
			catch (Exception e) {
				Logger.getLogger(getClass().getName()).severe(Throwables.toString(e));
			}
		}

	}
	
	/**
	 * @author $Author: srodriguez$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class OutgoingConnectionHandler implements PipeMsgListener {

		private JxtaBiDiPipe pipe = null;
		private Object responseLock = new Object();
		Address responseAddress = null;

		OutgoingConnectionHandler(PipeAdvertisement pAdv) throws IOException {
			this.pipe = new JxtaBiDiPipe(getPeerGroup(), pAdv,this);			
		}

		/**
		 * @param msg
		 * @param isBroadcast
		 * @return the address of the receiver agent
		 * @throws IOException
		 */
		public Address send(net.jxta.endpoint.Message msg, boolean isBroadcast) throws IOException {
			try {
				this.pipe.sendMessage(msg);

				// wait the secs if nothing comes back assume failure

				if (!isBroadcast) {
					synchronized (this.responseLock) {
						// assume failure affet 10 secs
						this.responseLock.wait(10000);
					}
				}
				return this.responseAddress;
			}
			catch (AssertionError ae) {
				throw ae;
			}
			catch (InterruptedException e) {
				Logger.getLogger(getClass().getName()).severe(Throwables.toString(e));
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void pipeMsgEvent(PipeMsgEvent event) {
			net.jxta.endpoint.Message message = event.getMessage();
			if (message == null) return;
			try {

				this.responseAddress = (Address) MessageUtils.getObjectFromMessage(
						message, 
						JanusJXTAGroup.MSG_NAMESPACE_SENDING, 
						JanusJXTAGroup.MSG_ELEM_REVEICER_RETURN_ADDRESS);
				synchronized (this.responseLock) {
					this.responseLock.notifyAll();
				}
			}
			catch (AssertionError ae) {
				throw ae;
			}
			catch (Exception e) {
				Logger.getLogger(getClass().getName()).severe(Throwables.toString(e));
			}
		}

	}
	

	/**
	 * @author $Author: srodriguez$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class PipeDiscover implements Runnable, DiscoveryListener {

		public PipeDiscover() {
			// Empty constructor to avoid synthetic accessor
		}

		/** {@inheritDoc}
		 */
		@Override
		public void discoveryEvent(DiscoveryEvent event) {
			//
		}

		/** {@inheritDoc}
		 */
		@Override
		public void run() {
			while (true) {
				try {
					Thread.currentThread().setName(
							Locale.getString(JanusJXTAGroup.class,
									"DISCOVER_THREAD_NAME", //$NON-NLS-1$
									getPeerGroup().getPeerName()));
				}
				catch(AssertionError e) {
					throw e;
				}
				catch(Throwable _) {
					//
				}
				// TODO make all this configurations
				try {
					PipeUtil.discoverAdvertisements(getPeerGroup(), null, this);
					Thread.sleep(1000);
				}
				catch (AssertionError ae) {
					throw ae;
				}
				catch (InterruptedException e) {
					//
				}
			}

		}

	}
}
