package com.codebroker.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 区域创建配置
 * 
 * @author xl
 *
 */
public class ZoneSettings {
	private static final AtomicInteger idGenerator = new AtomicInteger();
	private transient Integer id;
	public String name;
	public boolean isCustomLogin;
	public boolean isForceLogout;
	public boolean isFilterUserNames;
	public boolean isFilterRoomNames;
	public boolean isFilterPrivateMessages;
	public boolean isFilterBuddyMessages;
	public int maxUsers;
	public int maxUserVariablesAllowed;
	public int maxRoomVariablesAllowed;
	public int minRoomNameChars;
	public int maxRoomNameChars;
	public int maxRooms;
	public int maxRoomsCreatedPerUser;
	public int userCountChangeUpdateInterval;
	public int userReconnectionSeconds;
	public int overrideMaxUserIdleTime;
	public boolean allowGuestUsers;
	public String guestUserNamePrefix;
	public String publicRoomGroups;
	public String defaultRoomGroups;
	public String defaultPlayerIdGeneratorClass;
	public List<ZoneSettings.RoomSettings> rooms;
	public List<String> disabledSystemEvents;
	public static final String DENIABLE_REQUESTS = "JoinRoom,CreateRoom,ChangeRoomName,ChangeRoomPassword,ObjectMessage,SetRoomVariables,SetUserVariables,LeaveRoom,SubscribeRoomGroup,UnsubscribeRoomGroup,SpectatorToPlayer,PlayerToSpectator,ChangeRoomCapacity,PublicMessage,PrivateMessage,FindRooms,FindUsers,InitBuddyList,AddBuddy,BlockBuddy,RemoveBuddy,SetBuddyVariables,GoOnline,BuddyMessage,InviteUser,InvitationReply,CreateSFSGame,QuickJoinGame";
	public static final String DB_EXHAUSTED_POOL_MODES = String.format("%s,%s,%s",
			new Object[] { "BLOCK", "FAIL", "GROW" });

	public ZoneSettings() {
		this.name = "";
		this.isCustomLogin = false;
		this.isForceLogout = true;
		this.isFilterUserNames = true;
		this.isFilterRoomNames = true;
		this.isFilterPrivateMessages = true;
		this.isFilterBuddyMessages = true;
		this.maxUsers = 1000;
		this.maxUserVariablesAllowed = 5;
		this.maxRoomVariablesAllowed = 5;
		this.minRoomNameChars = 3;
		this.maxRoomNameChars = 10;
		this.maxRooms = 500;
		this.maxRoomsCreatedPerUser = 3;
		this.userCountChangeUpdateInterval = 1000;
		this.userReconnectionSeconds = 0;
		this.overrideMaxUserIdleTime = 120;
		this.allowGuestUsers = true;
		this.guestUserNamePrefix = "Guest#";
		this.publicRoomGroups = "default";
		this.defaultRoomGroups = "default";
		this.defaultPlayerIdGeneratorClass = "";
		this.getId();
	}

	public ZoneSettings(String name) {
		this();
		this.name = name;
		this.rooms = new ArrayList<RoomSettings>();
		this.disabledSystemEvents = new ArrayList<String>();
	}

	private static int setUniqueId() {
		return idGenerator.getAndIncrement();
	}

	public synchronized int getId() {
		if (this.id == null) {
			this.id = Integer.valueOf(setUniqueId());
		}
		return this.id.intValue();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCustomLogin() {
		return isCustomLogin;
	}

	public void setCustomLogin(boolean isCustomLogin) {
		this.isCustomLogin = isCustomLogin;
	}

	public boolean isForceLogout() {
		return isForceLogout;
	}

	public void setForceLogout(boolean isForceLogout) {
		this.isForceLogout = isForceLogout;
	}

	public boolean isFilterUserNames() {
		return isFilterUserNames;
	}

	public void setFilterUserNames(boolean isFilterUserNames) {
		this.isFilterUserNames = isFilterUserNames;
	}

	public boolean isFilterRoomNames() {
		return isFilterRoomNames;
	}

	public void setFilterRoomNames(boolean isFilterRoomNames) {
		this.isFilterRoomNames = isFilterRoomNames;
	}

	public boolean isFilterPrivateMessages() {
		return isFilterPrivateMessages;
	}

	public void setFilterPrivateMessages(boolean isFilterPrivateMessages) {
		this.isFilterPrivateMessages = isFilterPrivateMessages;
	}

	public boolean isFilterBuddyMessages() {
		return isFilterBuddyMessages;
	}

	public void setFilterBuddyMessages(boolean isFilterBuddyMessages) {
		this.isFilterBuddyMessages = isFilterBuddyMessages;
	}

	public int getMaxUsers() {
		return maxUsers;
	}

	public void setMaxUsers(int maxUsers) {
		this.maxUsers = maxUsers;
	}

	public int getMaxUserVariablesAllowed() {
		return maxUserVariablesAllowed;
	}

	public void setMaxUserVariablesAllowed(int maxUserVariablesAllowed) {
		this.maxUserVariablesAllowed = maxUserVariablesAllowed;
	}

	public int getMaxRoomVariablesAllowed() {
		return maxRoomVariablesAllowed;
	}

	public void setMaxRoomVariablesAllowed(int maxRoomVariablesAllowed) {
		this.maxRoomVariablesAllowed = maxRoomVariablesAllowed;
	}

	public int getMinRoomNameChars() {
		return minRoomNameChars;
	}

	public void setMinRoomNameChars(int minRoomNameChars) {
		this.minRoomNameChars = minRoomNameChars;
	}

	public int getMaxRoomNameChars() {
		return maxRoomNameChars;
	}

	public void setMaxRoomNameChars(int maxRoomNameChars) {
		this.maxRoomNameChars = maxRoomNameChars;
	}

	public int getMaxRooms() {
		return maxRooms;
	}

	public void setMaxRooms(int maxRooms) {
		this.maxRooms = maxRooms;
	}

	public int getMaxRoomsCreatedPerUser() {
		return maxRoomsCreatedPerUser;
	}

	public void setMaxRoomsCreatedPerUser(int maxRoomsCreatedPerUser) {
		this.maxRoomsCreatedPerUser = maxRoomsCreatedPerUser;
	}

	public int getUserCountChangeUpdateInterval() {
		return userCountChangeUpdateInterval;
	}

	public void setUserCountChangeUpdateInterval(int userCountChangeUpdateInterval) {
		this.userCountChangeUpdateInterval = userCountChangeUpdateInterval;
	}

	public int getUserReconnectionSeconds() {
		return userReconnectionSeconds;
	}

	public void setUserReconnectionSeconds(int userReconnectionSeconds) {
		this.userReconnectionSeconds = userReconnectionSeconds;
	}

	public int getOverrideMaxUserIdleTime() {
		return overrideMaxUserIdleTime;
	}

	public void setOverrideMaxUserIdleTime(int overrideMaxUserIdleTime) {
		this.overrideMaxUserIdleTime = overrideMaxUserIdleTime;
	}

	public boolean isAllowGuestUsers() {
		return allowGuestUsers;
	}

	public void setAllowGuestUsers(boolean allowGuestUsers) {
		this.allowGuestUsers = allowGuestUsers;
	}

	public String getGuestUserNamePrefix() {
		return guestUserNamePrefix;
	}

	public void setGuestUserNamePrefix(String guestUserNamePrefix) {
		this.guestUserNamePrefix = guestUserNamePrefix;
	}

	public String getPublicRoomGroups() {
		return publicRoomGroups;
	}

	public void setPublicRoomGroups(String publicRoomGroups) {
		this.publicRoomGroups = publicRoomGroups;
	}

	public String getDefaultRoomGroups() {
		return defaultRoomGroups;
	}

	public void setDefaultRoomGroups(String defaultRoomGroups) {
		this.defaultRoomGroups = defaultRoomGroups;
	}

	public String getDefaultPlayerIdGeneratorClass() {
		return defaultPlayerIdGeneratorClass;
	}

	public void setDefaultPlayerIdGeneratorClass(String defaultPlayerIdGeneratorClass) {
		this.defaultPlayerIdGeneratorClass = defaultPlayerIdGeneratorClass;
	}

	public List<ZoneSettings.RoomSettings> getRooms() {
		return rooms;
	}

	public void setRooms(List<ZoneSettings.RoomSettings> rooms) {
		this.rooms = rooms;
	}

	public List<String> getDisabledSystemEvents() {
		return disabledSystemEvents;
	}

	public void setDisabledSystemEvents(List<String> disabledSystemEvents) {
		this.disabledSystemEvents = disabledSystemEvents;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public static class RoomSettings {
		public static final String EVENTS = "USER_ENTER_EVENT,USER_EXIT_EVENT,USER_COUNT_CHANGE_EVENT,USER_VARIABLES_UPDATE_EVENT";
		private static final AtomicInteger idGenerator = new AtomicInteger();
		private transient Integer id;
		public String name;
		public String groupId;
		public String password;
		public int maxUsers;
		public int maxSpectators;
		public boolean isDynamic;
		public boolean isGame;
		public boolean isHidden;
		public String autoRemoveMode;
		// public ZoneSettings.RoomPermissions permissions;
		public String events;
		// public ZoneSettings.BadWordsFilterSettings badWordsFilter;
		// public List<ZoneSettings.RoomVariableDefinition> roomVariables;
		// public ZoneSettings.ExtensionSettings extension;

		public RoomSettings() {
			this.name = null;
			this.groupId = "default";
			this.password = null;
			this.maxUsers = 20;
			this.maxSpectators = 0;
			this.isDynamic = false;
			this.isGame = false;
			this.isHidden = false;
			this.autoRemoveMode = "DEFAULT";
			// this.permissions = new ZoneSettings.RoomPermissions();
			this.events = "USER_ENTER_EVENT,USER_EXIT_EVENT,USER_COUNT_CHANGE_EVENT,USER_VARIABLES_UPDATE_EVENT";
			// this.badWordsFilter = new ZoneSettings.BadWordsFilterSettings();
			this.getId();
		}

		public RoomSettings(String name) {
			this();
			this.name = name;
			this.password = "";
			// this.roomVariables = new ArrayList();
			// this.extension = new ZoneSettings.ExtensionSettings();
		}

		public int getId() {
			if (this.id == null) {
				this.id = getUniqueId();
			}

			return this.id.intValue();
		}

		private static int getUniqueId() {
			return idGenerator.getAndIncrement();
		}

		public String getAvailableEvents() {
			return "USER_ENTER_EVENT,USER_EXIT_EVENT,USER_COUNT_CHANGE_EVENT,USER_VARIABLES_UPDATE_EVENT";
		}
	}

}
