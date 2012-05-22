/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2012 Janus Core Developers
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
package org.janusproject.kernel.mailbox;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;

import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.util.selector.Selector;

/**
 * This Mailbox provide a common interface to store and retreive mails.
 * <p>
 * All the mailbox implementations must ensure that, when a message should be added,
 * the comparator is invoked as: <code>comparator(newMessage, messageAlreadyInMailbox)</code>.
 * It means that the first parameter given to the comparator is always the
 * parameter of the addition function.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @mavenartifactid mailbox
 */
public interface Mailbox extends Serializable, Iterable<Message> {
	
	/**
	 * Add the specified mail to the inbox of this mailbox
	 * 
	 * @param msg - the message to add
	 * @return <tt>true</tt> if the specified message was correctly add to the inbox, <tt>false</tt> else.
	 */
	public boolean add(Message msg);
	
	/**
	 * Copy all the message from the given mailbox inside this mailbox.
	 * 
	 * @param mailbox is the mailbox to read.
	 */
	public void synchronize(Mailbox mailbox);

	/**
	 * Remove the specified message from the inbox of this mailbox.
	 * 
	 * @param msg - the message to add
	 * @return <tt>true</tt> if the specified message was correctly remove from the inbox, <tt>false</tt> else.
	 */
	public boolean remove(Message msg);

	/**
	 * Remove all the mails that are matching the given selector.
	 * 
	 * @param selector permits to select the mails.
	 * @return <tt>true</tt> if the inbox has changed due to this removal action.
	 */
	public boolean removeAll(Selector<? extends Message> selector);

	/**
	 * Returns <tt>true</tt> if the inbox contains the specified message
	 * 
	 * @param msg - the message to test
	 * @return <tt>true</tt> if the inbox contains the specified message
	 */
	public boolean contains(Message msg);

	/**
	 * Returns <tt>true</tt> if the inbox contains a mail matching the given selector.
	 * 
	 * @param selector permits to select the mails.
	 * @return <tt>true</tt> if the inbox contains a matching message.
	 */
	public boolean contains(Selector<? extends Message> selector);

	/**
     * Returns <tt>true</tt> if the inbox contains no elements.
     * @return <tt>true</tt> if the inbox contains no elements.
     */
	public boolean isEmpty();
	
    /**
     * Returns the number of message in the inbox.  If the inbox contains
     * more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     * @return the number of message in the inbox.
     */
	public int size();
	
	/** Remove all the mails.
	 */
	public void clear();
	
	/**
	 * Gets and remove the first message in the mailbox.
	 * 
	 * @return the first message or null if empty.
	 */
	public Message removeFirst();
	
	/**
	 * Gets the first message in the mailbox but do not remove it.
	 * 
	 * @return the first message or null if empty.
	 */
	public Message getFirst();
	
	/**
	 * Gets and remove the first message in the mailbox that is matching the given selector.
	 * 
     * @param <T> is the type of the messages to reply.
	 * @param selector permits to select the mails.
	 * @return the first message or null if empty.
	 */
	public <T extends Message> T removeFirst(Selector<T> selector);
	
	/**
	 * Gets the first message in the mailbox but do not remove it.
	 * 
     * @param <T> is the type of the messages to reply.
	 * @param selector permits to select the mails.
	 * @return the first message or null if empty.
	 */
	public <T extends Message> T getFirst(Selector<T> selector);	

	/**
	 * Gets and remove the message located at the specified index in the inbox if any, null else.
	 * @param index - the index of the message to retrieve
	 * @return the message located at the specified index in the inbox, or <code>null</code> if none.
	 */
	public Message remove(int index);
	
	/**
	 * Gets and remove the message located at the specified index in the inbox if any, null else.
	 * @param index - the index of the message to retrieve
	 * @return the message located at the specified index in the inbox, or <code>null</code> if none.
	 */
	public Message get(int index);

	/**
	 * Wait until a mail is available or a timeout is reached, then remove it from mailbox and reply it.
	 * 
	 * @param timeout is the tie to wait in milliseconds
	 * @return the first message or null if empty or timeout.
	 */
	public Message removeFirst(long timeout);
	
	/**
	 * Wait until a mail is available or a timeout is reached, then reply it without removal from mailbox.
	 * 
	 * @param timeout is the tie to wait in milliseconds
	 * @return the first message or null if empty.
	 */
	public Message getFirst(long timeout);
	
	/**
	 * Wait until a matching mail is available or a timeout is reached, then remove it from mailbox and reply it.
	 * 
     * @param <T> is the type of the messages to reply.
	 * @param timeout is the tie to wait in milliseconds
	 * @param selector permits to select the mails.
	 * @return the first message or null if empty.
	 */
	public <T extends Message> T removeFirst(Selector<T> selector, long timeout);
	
	/**
	 * Wait until a matching mail is available or a timeout is reached, then reply it without removal from mailbox.
	 * 
     * @param <T> is the type of the messages to reply.
	 * @param timeout is the tie to wait in milliseconds
	 * @param selector permits to select the mails.
	 * @return the first message or null if empty.
	 */
	public <T extends Message> T getFirst(Selector<T> selector, long timeout);
	
	/** Replies an iterator on all the messages matching the given selector.
	 * <p>
	 * <strong>Caution:</strong> The replied iterator consumes 
	 * the messages from the mailbox.
	 * It means that is is invoking {@link #remove(int)} at
	 * each <code>next</code> function invocation.
	 * 
     * @param <T> is the type of the messages to reply.
	 * @param selector permits to select the mails.
	 * @return an iterator on messages.
	 */
	public <T extends Message> Iterator<T> iterator(Selector<T> selector);

    /**
     * Replies an iterator on all the messages.
	 * <p>
	 * <strong>Caution:</strong> The replied iterator consumes 
	 * the messages from the mailbox.
	 * It means that is is invoking {@link #removeFirst()} at
	 * each <code>next</code> function invocation.
     * 
     * @return an Iterator.
     */
	@Override
    Iterator<Message> iterator();

	/** Replies an iterator on all the messages matching the given selector.
	 * <p>
	 * <strong>Caution:</strong> The replied iterator consumes 
	 * the messages from the mailbox.
	 * It means that is is invoking {@link #remove(int)} at
	 * each <code>next</code> function invocation.
	 * 
     * @param <T> is the type of the messages to reply.
	 * @param selector permits to select the mails.
     * @param consumeMails is <code>true</code> to remove the
     * elements replied by the iterator from ths mailbox, is
     * <code>false</code> to let the mailbox unchanged.
	 * @return an iterator on messages.
	 */
	public <T extends Message> Iterator<T> iterator(Selector<T> selector, boolean consumeMails);

    /**
     * Replies an iterator on all the messages.
     * 
     * @param consumeMails is <code>true</code> to remove the
     * elements replied by the iterator from ths mailbox, is
     * <code>false</code> to let the mailbox unchanged.
     * @return an Iterator.
     */
	public Iterator<Message> iterator(boolean consumeMails);
    
    /**
     * Replies an iterator on all the messages of a given type.
     * <p>
     * This function is equivalent to:
     * <pre><code>
     * this.iterator(new MailTypeSelector(type));
     * </code></pre>
     * 
     * @param <T> is the type of the messages to reply.
     * @param type is the type of a message.
     * @return an Iterator.
     * @since 0.5
     */
	public <T extends Message> Iterator<T> iterator(Class<T> type);

    /**
     * Replies an iterator on all the messages of a given type.
     * <p>
     * This function is equivalent to:
     * <pre><code>
     * this.iterable(new MailTypeSelector(type));
     * </code></pre>
     * 
     * @param <T> is the type of the messages to reply.
     * @param type is the type of a message.
     * @return an Iterator.
     * @since 0.5
     */
	public <T extends Message> Iterable<T> iterable(Class<T> type);

	/** Replies an iterator on all the messages matching the given selector.
	 * <p>
	 * <strong>Caution:</strong> The replied iterator consumes 
	 * the messages from the mailbox.
	 * It means that is is invoking {@link #remove(int)} at
	 * each <code>next</code> function invocation.
	 * 
     * @param <T> is the type of the messages to reply.
	 * @param selector permits to select the mails.
	 * @return an iterator on messages.
	 * @since 0.5
	 */
	public <T extends Message> Iterable<T> iterable(Selector<T> selector);

	/** Replies an iterator on all the messages matching the given selector.
	 * <p>
	 * <strong>Caution:</strong> The replied iterator consumes 
	 * the messages from the mailbox.
	 * It means that is is invoking {@link #remove(int)} at
	 * each <code>next</code> function invocation.
	 * 
	 * @param selector permits to select the mails.
     * @param consumeMails is <code>true</code> to remove the
     * elements replied by the iterator from ths mailbox, is
     * <code>false</code> to let the mailbox unchanged.
	 * @return an iterator on messages.
     * @since 0.5
	 */
	public <T extends Message> Iterable<T> iterable(Selector<T> selector, boolean consumeMails);

    /**
     * Replies an iterable on all the messages.
     * If the parameter is <code>true</code>, 
     * this object is directly replied;
     * otherwise an iterable object that
     * does not conusme the mails is returned.
     * 
     * @param consumeMails is <code>true</code> to remove the
     * elements replied by the iterator from ths mailbox, is
     * <code>false</code> to let the mailbox unchanged.
     * @return an Iterator.
     * @since 0.5
     */
    public Iterable<Message> iterable(boolean consumeMails);

    /** Replies the comparator that is used to sort the messages in the mailbox.
     * 
     * @return the comparator that is used to sort the messages in the mailbox, never <code>null</code>.
     * @since 0.5
     */
    public Comparator<? super Message> comparator();

}
