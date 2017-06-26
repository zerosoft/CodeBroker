syntax = "proto2";
option java_package = "com.message.protocol";
option java_outer_classname = "Message";

message PB {

	enum SystemKey{
		CS_USER_CONNECT_TO_SERVER = 1;
		SC_USER_CONNECT_TO_SERVER_SUCCESS = 2;
		SC_USER_CONNECT_TO_SERVER_FAIL = 3;

		CS_USER_DISCONNECT = 4;

		CS_USER_RECONNECTION_TRY = 5;
		SC_USER_RECONNECTION_SUCCESS = 6;
		SC_USER_RECONNECTION_FAIL = 7;

		CS_USER_ENTER_WORLD =8;
		SC_USER_ENTER_WORLD =9;
	}

	enum MessageKey {
		<#list FieldSeqence as item>
			${item};
		</#list>
	}

}