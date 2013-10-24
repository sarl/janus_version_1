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

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.arakhne.afc.vmutil.Android;
import org.arakhne.afc.vmutil.Android.AndroidException;
import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.network.januschat.ChatUtil;
import org.janusproject.demos.network.januschat.ChatterListener;
import org.janusproject.demos.network.januschat.agent.ChatChannel;
import org.janusproject.demos.network.januschat.agent.IncomingChatListener;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.KernelEvent;
import org.janusproject.kernel.KernelListener;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.network.jxme.agent.JxtaJxmeKernelAgentFactory;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

/**
 * Activity for the simple chat on Android.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ChatActivity extends Activity {

	private static final String TAG = "janus-chat"; //$NON-NLS-1$

	private AgentAddress myself = null;
	private Listener listener = null;
	private final AtomicBoolean isWaiting = new AtomicBoolean(true);

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Default creation of the Android activity
		super.onCreate(savedInstanceState);

		// Create the listener on the chat events
		this.listener = new Listener();

		// Initialize the Arakhne-VM-utility library to
		// support this Android activity
		try {
			Android.initialize(this);
		} catch (AndroidException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}

		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

		Tab tab = actionBar.newTab();
		tab.setText(Locale.getString("IDDLE")); //$NON-NLS-1$
		tab.setTabListener(new TabListener<IddleFragment>(ChatActivity.this,
				"iddle", IddleFragment.class, null)); //$NON-NLS-1$
		actionBar.addTab(tab);

		if (isOnline()) {
			// Register this activity as listener on the chat events.
			ChatUtil.addChatterListener(this.listener);

			// Force to use a networking version of the Janus
			ExecutorService serv = Executors.newSingleThreadExecutor();
			serv.execute(new Initializer(this, this.listener));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options, menu);

		MenuItem item = menu.findItem(R.id.joinRoomMenuItem);
		item.setOnMenuItemClickListener(this.listener);

		item = menu.findItem(R.id.exitRoomMenuItem);
		item.setOnMenuItemClickListener(this.listener);

		item = menu.findItem(R.id.exitApplicationMenuItem);
		item.setOnMenuItemClickListener(this.listener);

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		ActionBar actionBar = getActionBar();
		int roomCount = (actionBar == null) ? 0 : actionBar.getTabCount();
		boolean hasSelectedTab = (actionBar == null) ? false : actionBar
				.getSelectedTab() != null;

		MenuItem item;

		item = menu.findItem(R.id.joinRoomMenuItem);
		item.setEnabled(!this.isWaiting.get());

		item = menu.findItem(R.id.exitRoomMenuItem);
		item.setEnabled(!this.isWaiting.get() && roomCount > 0
				&& hasSelectedTab);

		item = menu.findItem(R.id.exitApplicationMenuItem);
		item.setEnabled(!this.isWaiting.get() && roomCount > 0);

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save the currently-selected tab.
		outState.putInt("currentRoom", //$NON-NLS-1$
				getActionBar().getSelectedNavigationIndex());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onDestroy() {
		ChatUtil.removeChatterListener(this.listener);
		this.listener = null;

		// Kill all the Janus kernels to kill all the chatter agents
		Kernels.killAll();

		super.onDestroy();
	}

	/**
	 * Replies the preferred name for the user.
	 * 
	 * @return the preferred name for the user.
	 */
	public String getUsername() {
		AccountManager manager = AccountManager.get(this);
		Account[] accounts = manager.getAccountsByType("com.google"); //$NON-NLS-1$

		for (Account account : accounts) {
			if (account.name != null && !account.name.isEmpty()) {
				String[] parts = account.name.split("@"); //$NON-NLS-1$
				if (parts.length > 0 && parts[0] != null)
					return parts[0];
				return account.name;
			}
		}

		return Locale.getString(ChatActivity.class, "USERNAME"); //$NON-NLS-1$
	}

	/**
	 * Replies the address of the chatter supported by this activity.
	 * 
	 * @return the address of the chatter supported by this activity.
	 */
	protected AgentAddress myself() {
		return this.myself;
	}

	/**
	 * Set the address of the chatter supported by this activity.
	 * 
	 * @param adr
	 *            is the address of the chatter supported by this activity.
	 */
	protected void myself(AgentAddress adr) {
		this.myself = adr;
	}

	/**
	 * Exit from the current room.
	 * 
	 * @return the name of the room from which the chatter has exited; or
	 *         <code>null</code> if no room was found.
	 */
	public GroupAddress exitCurrentRoom() {
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			Tab tab = actionBar.getSelectedTab();
			if (tab != null) {
				Object tag = tab.getTag();
				if (tag instanceof GroupAddress) {
					GroupAddress room = (GroupAddress) tag;
					ChatChannel channel = ChatUtil.getChannelFor(myself());
					channel.exitChatroom(room);
					return room;
				}
			}
		}
		return null;
	}

	/**
	 * Exit from the application.
	 */
	public void exitActivity() {
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			ChatChannel channel = ChatUtil.getChannelFor(myself());
			while (actionBar.getTabCount() > 0) {
				Tab tab = actionBar.getTabAt(0);
				Object tag = tab.getTag();
				if (tag instanceof GroupAddress) {
					channel.exitChatroom((GroupAddress) tag);
				} else {
					actionBar.removeTabAt(0);
				}
			}
		}
		finish();
	}

	/**
	 * Select and join a room.
	 */
	public void joinRoom() {
		FragmentManager fm = getFragmentManager();
        JoinRoomDialog joinRoomDialog = new JoinRoomDialog();
        joinRoomDialog.show(fm, "fragment_join_room"); //$NON-NLS-1$
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class Listener implements KernelListener, ChatterListener,
			IncomingChatListener, OnMenuItemClickListener {

		/**
		 */
		public Listener() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onChatterCreated(AgentAddress chatter) {
			// The chatter was created, then
			// be listener on the chat room events
			myself(chatter);
			ChatChannel channel = ChatUtil.getChannelFor(chatter);
			channel.addIncomingChatListener(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void incomingMessage(GroupAddress chatroom, AgentAddress sender,
				String message) {
			// Ignore this event because this activity is not focussing on the
			// messages but on the creation and destruction of chat rooms.
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void chatroomError(GroupAddress chatroom, Throwable error) {
			// Ignore this event because this activity is not focussing on the
			// messages but on the creation and destruction of chat rooms.
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void joinChatroom(GroupAddress chatroom, AgentAddress joiner) {
			// Ignore this event because this activity is not focussing on the
			// messages but on the creation and destruction of chat rooms.
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void chatroomCreated(GroupAddress chatroom) {
			runOnUiThread(new TabCreator(chatroom));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void exitChatroom(GroupAddress chatroom, AgentAddress exiter) {
			if (exiter.equals(myself())) {
				runOnUiThread(new TabRemover(chatroom));
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.joinRoomMenuItem:
				joinRoom();
				return true;
			case R.id.exitRoomMenuItem:
				exitCurrentRoom();
				return true;
			case R.id.exitApplicationMenuItem:
				exitActivity();
				return true;
			default:
				return false;				
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void agentLaunched(KernelEvent event) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void agentKilled(KernelEvent event) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean exceptionUncatched(Throwable error) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void kernelAgentLaunched(KernelEvent event) {
			Kernel kernel = event.getSource();

			kernel.removeKernelListener(this);

			// Remove the waiting tab
			ChatActivity.this.isWaiting.set(false);
			runOnUiThread(new TabRemover());

			// Create the chatter and the associated rooms.
			ChatUtil.createChatter(kernel, getUsername());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void kernelAgentKilled(KernelEvent event) {
			Kernel kernel = event.getSource();
			kernel.removeKernelListener(this);
		}

	} // class Listener

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class TabCreator implements Runnable {

		private final GroupAddress chatroom;

		/**
		 * @param chatroom
		 */
		public TabCreator(GroupAddress chatroom) {
			this.chatroom = chatroom;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			String roomName = ChatUtil.getRoomName(this.chatroom);

			ActionBar actionBar = getActionBar();
			Tab tab = actionBar.newTab();
			tab.setText(roomName);
			tab.setTag(this.chatroom);
			Bundle args = new Bundle();
			args.putSerializable("room", this.chatroom); //$NON-NLS-1$
			args.putSerializable("chatter", myself()); //$NON-NLS-1$
			tab.setTabListener(new TabListener<RoomFragment>(ChatActivity.this,
					roomName, RoomFragment.class, args));
			actionBar.addTab(tab);
		}

	} // class TabCreator

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class TabRemover implements Runnable {

		private final GroupAddress chatroom;

		/**
		 * @param chatroom
		 */
		public TabRemover(GroupAddress chatroom) {
			this.chatroom = chatroom;
		}

		/**
		 */
		public TabRemover() {
			this.chatroom = null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			ActionBar actionBar = getActionBar();
			if (this.chatroom == null) {
				for (int i = 0; i < actionBar.getTabCount(); ++i) {
					Tab tab = actionBar.getTabAt(i);
					Object tag = tab.getTag();
					if (tag == null || !(tag instanceof GroupAddress)) {
						actionBar.removeTabAt(i);
						--i;
					}
				}
			} else {
				for (int i = 0; i < actionBar.getTabCount(); ++i) {
					Tab tab = actionBar.getTabAt(i);
					Object tag = tab.getTag();
					if (this.chatroom.equals(tag)) {
						actionBar.removeTabAt(i);
						return;
					}
				}
			}
		}

	} // class TabRemover

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class TabListener<T extends Fragment> implements
			ActionBar.TabListener {

		/**
		 * Fragmented activity.
		 */
		private final Activity activity;

		/**
		 * Tag for the activity fragment.
		 */
		private final String fragmentTag;

		/**
		 * Type of the fragment to create.
		 */
		private final Class<T> fragmentType;

		/**
		 * Arguments to pass to the fragment creator.
		 */
		private final Bundle fragmentInstanciationArguments;

		/**
		 * Portion of the UI that is supporting a Tab.
		 */
		private Fragment fragment;

		/**
		 * @param activity
		 *            is the activity that is fragmented.
		 * @param tag
		 *            is the tag of the activity fragment.
		 * @param type
		 *            is the type of the fragment to create.
		 * @param args
		 *            are the arguments to pass to the fragment creator.
		 */
		public TabListener(Activity activity, String tag, Class<T> type,
				Bundle args) {
			this.activity = activity;
			this.fragmentTag = tag;
			this.fragmentType = type;
			this.fragmentInstanciationArguments = args;

			// Check to see if we already have a fragment for this tab, probably
			// from a previously saved state. If so, deactivate it, because our
			// initial state is that a tab isn't shown.
			this.fragment = this.activity.getFragmentManager()
					.findFragmentByTag(this.fragmentTag);
			if (this.fragment != null && !this.fragment.isDetached()) {
				FragmentTransaction ft = this.activity.getFragmentManager()
						.beginTransaction();
				ft.detach(this.fragment);
				ft.commit();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (this.fragment == null) {
				// Create a fragment if never instancied.
				this.fragment = Fragment.instantiate(this.activity,
						this.fragmentType.getName(),
						this.fragmentInstanciationArguments);
				ft.add(android.R.id.content, this.fragment, this.fragmentTag);
			} else {
				// Attach an already created fragment
				ft.attach(this.fragment);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (this.fragment != null) {
				ft.detach(this.fragment);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			//
		}

	} // class TabListener

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Initializer implements Runnable {

		private final WeakReference<ChatActivity> activity;
		private final WeakReference<Listener> listener;

		public Initializer(ChatActivity activity, Listener listener) {
			this.activity = new WeakReference<ChatActivity>(activity);
			this.listener = new WeakReference<Listener>(listener);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			ChatActivity a = this.activity.get();
			Listener l = this.listener.get();
			if (a != null && l != null) {
				JxtaJxmeKernelAgentFactory factory = new JxtaJxmeKernelAgentFactory();
				Kernels.create(factory, l);
			}
		}

	} // class Initializer

}
