// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: UserMessage.proto

package com.codebroker.demo.protobuf.user;

public interface UserOrBuilder extends
    // @@protoc_insertion_point(interface_extends:User)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 fireTime = 1;</code>
   * @return The fireTime.
   */
  int getFireTime();

  /**
   * <code>string lang = 2;</code>
   * @return The lang.
   */
  java.lang.String getLang();
  /**
   * <code>string lang = 2;</code>
   * @return The bytes for lang.
   */
  com.google.protobuf.ByteString
      getLangBytes();

  /**
   * <code>int32 serverId = 3;</code>
   * @return The serverId.
   */
  int getServerId();

  /**
   * <code>int32 allianceId = 4;</code>
   * @return The allianceId.
   */
  int getAllianceId();

  /**
   * <code>int32 nationalFlag = 5;</code>
   * @return The nationalFlag.
   */
  int getNationalFlag();
}
