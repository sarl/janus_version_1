/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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
package org.janusproject.demos.network.androidchat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.network.januschat.ChatUtil;
import org.janusproject.demos.network.januschat.agent.ChatChannel;
import org.janusproject.demos.network.januschat.agent.IncomingChatListener;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.util.directaccess.ListUtil;

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/** Application fragment that is supporting
 * thie room.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RoomFragment extends Fragment {

	private final List<AgentAddress> members = new ArrayList<AgentAddress>();
	
	private Listener listener = null;
	
	private AgentAddress chatter = null;
	private GroupAddress room = null;
	
	/** Replies the address of the chatter supported by this activity.
	 * 
	 * @return the address of the chatter supported by this activity.
	 */
	protected AgentAddress myself() {
		return this.chatter;
	}

	/** Replies the address of the room supported by this activity.
	 * 
	 * @return the address of the room supported by this activity.
	 */
	protected GroupAddress room() {
		return this.room;
	}

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState If the activity is being re-initialized after 
	 * previously being shut down then this Bundle contains the data it most 
	 * recently supplied in onSaveInstanceState(Bundle).
	 * <b>Note: Otherwise it is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Default creation of the Android activity
		super.onCreate(savedInstanceState);
		
		this.listener = new Listener();
		
		// Retreive the room address from the creation arguments.
		Serializable s = getArguments().getSerializable("room"); //$NON-NLS-1$
		if (s instanceof GroupAddress) {
			this.room = (GroupAddress)s;
		}
		else {
			this.room = null;
		}
		
		// Retreive the chatter address.
		s = getArguments().getSerializable("chatter"); //$NON-NLS-1$
		if (s instanceof AgentAddress) {
			this.chatter = (AgentAddress)s;
		}
		else {
			this.chatter = null;
		}
		
		if (this.chatter!=null) {
			ChatChannel channel = ChatUtil.getChannelFor(this.chatter);
			if (channel!=null) {
				Iterator<AgentAddress> iterator = channel.getChatroomParticipants(this.room);
				AgentAddress adr;
				while (iterator.hasNext()) {
					adr = iterator.next();
					if (!adr.equals(myself())) {
						ListUtil.dichotomicAdd(this.members,
								MemberComparator.SINGLETON,
								adr,
								false);
					}
				}
				channel.addIncomingChatListener(this.listener);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.room, container, false);
		
		Button button = (Button)view.findViewById(R.id.sendButton);
		button.setText(Locale.getString(RoomFragment.class, "SEND")); //$NON-NLS-1$
		
		ListView memberList = (ListView)view.findViewById(R.id.roomMembers);
		memberList.setLongClickable(false);
		memberList.setAdapter(new ArrayAdapter<AgentAddress>(
				getActivity(),
				android.R.layout.simple_list_item_1,
				this.members));
		memberList.setOnItemClickListener(this.listener);
		
		TextView messageArea = (TextView)view.findViewById(R.id.chatText);
		String roomName = ChatUtil.getRoomName(this.room);
		messageArea.setText(Locale.getString("WELCOME", roomName)); //$NON-NLS-1$
		messageArea.append("\n"); //$NON-NLS-1$
		
		Button sendButton = (Button)view.findViewById(R.id.sendButton);
		sendButton.setOnClickListener(this.listener);
		
		EditText messageEditor = (EditText)view.findViewById(R.id.messageEditor);
		messageEditor.setOnEditorActionListener(this.listener);
		
		return view;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDestroy() {
		try {
			if (this.listener!=null && this.chatter!=null) {
				ChatChannel channel = ChatUtil.getChannelFor(this.chatter);
				if (channel!=null) {
					channel.removeIncomingChatListener(this.listener);
				}
			}
		}
		catch(Throwable _) {
			//
		}
		
		this.listener = null;
		this.chatter = null;
		this.room = null;
		super.onDestroy();
	}
	
	/** Replies the member of the room at the given position.
	 * 
	 * @param position
	 * @return the member, never <code>null</code>.
	 */
	protected AgentAddress getMemberAt(int position) {
		return this.members.get(position);
	}
	
	/** Replies the area where messages may be put.
	 * 
	 * @return the message area.
	 */
	protected final TextView getMessageArea() {
		return (TextView)getView().findViewById(R.id.chatText);
	}
	
	/** Replies the editor of messages.
	 * 
	 * @return the message editor.
	 */
	protected final EditText getMessageEditor() {
		return (EditText)getView().findViewById(R.id.messageEditor);
	}

	/** Add a member of the room in the list of the members.
	 * 
	 * @param member is the new member.
	 */
	protected synchronized void addRoomMember(AgentAddress member) {
		ListUtil.dichotomicAdd(this.members, MemberComparator.SINGLETON, member, false);
	}
	
	/** Remove a member from the room in the list of the members.
	 * 
	 * @param member is the member to remove.
	 */
	protected synchronized void removeRoomMember(AgentAddress member) {
		ListUtil.dichotomicRemove(this.members, MemberComparator.SINGLETON, member);
	}

	/** Add a message into the room.
	 * 
	 * @param sender is the sender of the message.
	 * @param message is the message itself.
	 */
	protected void addText(String sender, String message) {
		getActivity().runOnUiThread(
				new TextUpdater(R.color.standardColor, Locale.getString(RoomFragment.class,
				"STANDARD", sender, message))); //$NON-NLS-1$
	}

	/** Notify about an error in the room.
	 * 
	 * @param error describes the error.
	 */
	protected void addError(Throwable error) {
		getActivity().runOnUiThread(
				new TextUpdater(R.color.errorColor, Locale.getString(RoomFragment.class,
				"ERROR", error))); //$NON-NLS-1$
	}

	/** Notify about the arrival in the room.
	 * 
	 * @param joiner is the people that has joined the room.
	 */
	protected void addJoinMessage(String joiner) {
		getActivity().runOnUiThread(
				new TextUpdater(R.color.notificationColor, Locale.getString(RoomFragment.class,
				"JOIN", joiner))); //$NON-NLS-1$
	}

	/** Notify about the departure from the room.
	 * 
	 * @param exiter is the people that has exited the room.
	 */
	protected void addExitMessage(String exiter) {
		getActivity().runOnUiThread(
				new TextUpdater(R.color.notificationColor, Locale.getString(RoomFragment.class,
				"EXIT", exiter))); //$NON-NLS-1$
	}
	
	/** Send the current text.
	 */
	protected void sendMessage() {
		EditText textEditor = getMessageEditor();
		String text = textEditor.getText().toString();
		text = text.trim();
		if (!text.isEmpty()) {
			ChatChannel channel = ChatUtil.getChannelFor(myself());
			channel.postMessage(room(), text);
			textEditor.setText(""); //$NON-NLS-1$
		}
	}

	/** 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class Listener implements IncomingChatListener, OnItemClickListener,
			OnClickListener, OnEditorActionListener {

		/**
		 */
		public Listener() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void incomingMessage(GroupAddress chatroom, AgentAddress sender, String message) {
			if (chatroom.equals(room())) {
				addText(ChatUtil.getChatterName(sender), message);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void chatroomError(GroupAddress chatroom, Throwable error) {
			if (chatroom.equals(room())) {
				addError(error);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void joinChatroom(GroupAddress chatroom, AgentAddress joiner) {
			if (chatroom.equals(room())) {
				addRoomMember(joiner);
				addJoinMessage(ChatUtil.getChatterName(joiner));
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void exitChatroom(GroupAddress chatroom, AgentAddress exiter) {
			if (chatroom.equals(room())) {
				removeRoomMember(exiter);
				addExitMessage(ChatUtil.getChatterName(exiter));
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void chatroomCreated(GroupAddress chatroom) {
			// Ignore this event because this activity is not focussing on the
			// room but on the messages.
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			AgentAddress adr = getMemberAt(position);
			EditText messageEditor = getMessageEditor();
			String text = Locale.getString(RoomFragment.class, "SEND_TO", //$NON-NLS-1$
					ChatUtil.getChatterName(adr));
			messageEditor.setText(text);
			messageEditor.setSelection(text.length());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onClick(View view) {
			if (view instanceof Button) {
				// Send button
				sendMessage();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean onEditorAction(TextView view, int id, KeyEvent key) {
			if (key.getKeyCode()==KeyEvent.KEYCODE_ENTER) {
				sendMessage();
				return true;
			}
			return false;
		}

	} // class Listener

	/** 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class TextUpdater implements Runnable {

		private final String text;
		
		/**
		 * @param color
		 * @param text
		 */
		public TextUpdater(int color, String text) {
			this.text = text;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			TextView v = getMessageArea();
			v.append(this.text);
			v.append("\n"); //$NON-NLS-1$
		}
		
	}
	
}

