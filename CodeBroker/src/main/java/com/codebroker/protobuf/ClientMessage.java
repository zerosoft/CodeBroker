// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: LoginMessage.proto

package com.codebroker.protobuf;

public final class ClientMessage {
  private ClientMessage() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Login_C_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Login_C_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Login_S_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Login_S_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\023ClientMessage.proto\"n\n\007Login_C\022\017\n\007prot" +
      "oId\030\001 \001(\005\022\016\n\006openId\030\002 \001(\t\022\013\n\003uid\030\003 \001(\003\022\017" +
      "\n\007version\030\004 \001(\t\022\020\n\010deviceId\030\005 \001(\t\022\022\n\nser" +
      "verName\030\006 \001(\t\"\032\n\007Login_S\022\017\n\007protoId\030\001 \001(" +
      "\005B\033\n\027com.codebroker.protobufP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_Login_C_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_Login_C_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Login_C_descriptor,
        new String[] { "ProtoId", "OpenId", "Uid", "Version", "DeviceId", "ServerName", });
    internal_static_Login_S_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_Login_S_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Login_S_descriptor,
        new String[] { "ProtoId", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}