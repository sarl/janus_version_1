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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.network.januschat.ChatUtil;
import org.janusproject.demos.network.januschat.agent.ChatChannel;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.GroupAddress;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/** Dialog to join a room.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JoinRoomDialog extends DialogFragment implements OnItemClickListener {

	private final List<Object> rooms = new ArrayList<Object>();
	private AgentAddress myself = null;
	
	/**
	 */
	public JoinRoomDialog() {
		super();
	}
	
	@Override
	public View onCreateView(
			LayoutInflater inflater, 
			ViewGroup container,
            Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.roomselection, container);
		
		ChatActivity chatActivity = (ChatActivity)container.getContext();
		
		this.myself = chatActivity.myself();
		
		setCancelable(true);
		getDialog().setTitle(Locale.getString("TITLE")); //$NON-NLS-1$
		
		ListView listView = (ListView)view.findViewById(R.id.roomList);
		listView.setAdapter(new ArrayAdapter<Object>(
				chatActivity,
				android.R.layout.simple_list_item_1,
				this.rooms));
		listView.setOnItemClickListener(this);
		
		return view;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		ChatChannel channel = ChatUtil.getChannelFor(this.myself);
		Collection<GroupAddress> myRooms = channel.getParticipatingChatrooms();
		this.rooms.clear();
		for(GroupAddress room : channel.getAllChatrooms()) {
			if (!myRooms.contains(room)) {
				this.rooms.add(room);
			}
		}
		ListView listView = (ListView)view.findViewById(R.id.roomList);
		listView.setEnabled(!this.rooms.isEmpty());
		if (this.rooms.isEmpty()) {
			this.rooms.add(Locale.getString("NO_ROOM")); //$NON-NLS-1$
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object roomId = this.rooms.get(position);
		if (roomId instanceof GroupAddress) {
			ChatChannel channel = ChatUtil.getChannelFor(this.myself);
			channel.joinChatroom((GroupAddress)roomId);
		}
		dismiss();
	}

}

