// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: UserMessage.proto

package com.codebroker.protobuff.user;

public interface FullTestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:FullTest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string name = 1;</code>
   * @return The name.
   */
  java.lang.String getName();
  /**
   * <code>string name = 1;</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString
      getNameBytes();

  /**
   * <code>int32 id = 2;</code>
   * @return The id.
   */
  int getId();

  /**
   * <code>string email = 3;</code>
   * @return The email.
   */
  java.lang.String getEmail();
  /**
   * <code>string email = 3;</code>
   * @return The bytes for email.
   */
  com.google.protobuf.ByteString
      getEmailBytes();

  /**
   * <code>repeated .FullTest.PhoneNumber phones = 4;</code>
   */
  java.util.List<com.codebroker.protobuff.user.FullTest.PhoneNumber> 
      getPhonesList();
  /**
   * <code>repeated .FullTest.PhoneNumber phones = 4;</code>
   */
  com.codebroker.protobuff.user.FullTest.PhoneNumber getPhones(int index);
  /**
   * <code>repeated .FullTest.PhoneNumber phones = 4;</code>
   */
  int getPhonesCount();
  /**
   * <code>repeated .FullTest.PhoneNumber phones = 4;</code>
   */
  java.util.List<? extends com.codebroker.protobuff.user.FullTest.PhoneNumberOrBuilder> 
      getPhonesOrBuilderList();
  /**
   * <code>repeated .FullTest.PhoneNumber phones = 4;</code>
   */
  com.codebroker.protobuff.user.FullTest.PhoneNumberOrBuilder getPhonesOrBuilder(
      int index);
}
