// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: UserMessage.proto

package com.codebroker.demo.protobuf.user;

public final class UserMessage {
  private UserMessage() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_User_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_User_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\021UserMessage.proto\"b\n\004User\022\020\n\010fireTime\030" +
      "\001 \001(\005\022\014\n\004lang\030\002 \001(\t\022\020\n\010serverId\030\003 \001(\005\022\022\n" +
      "\nallianceId\030\004 \001(\005\022\024\n\014nationalFlag\030\005 \001(\005B" +
      "%\n!com.codebroker.demo.protobuf.userP\001b\006" +
      "proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_User_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_User_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_User_descriptor,
        new java.lang.String[] { "FireTime", "Lang", "ServerId", "AllianceId", "NationalFlag", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
