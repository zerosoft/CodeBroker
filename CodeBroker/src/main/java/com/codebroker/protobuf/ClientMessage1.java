// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: UserMessage.proto

package com.codebroker.protobuf;

public final class ClientMessage1 {
  private ClientMessage1() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Login_C1_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Login_C1_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Login_S1_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Login_S1_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\024ClientMessage1.proto\032\023ClientMessage.pr" +
      "oto\"\206\001\n\010Login_C1\022\017\n\007protoId\030\001 \001(\005\022\016\n\006ope" +
      "nId\030\002 \001(\t\022\013\n\003uid\030\003 \001(\003\022\017\n\007version\030\004 \001(\t\022" +
      "\020\n\010deviceId\030\005 \001(\t\022\022\n\nserverName\030\006 \001(\t\022\025\n" +
      "\003ccc\030\010 \001(\0132\010.Login_C\"\033\n\010Login_S1\022\017\n\007prot" +
      "oId\030\001 \001(\005B\033\n\027com.codebroker.protobufP\001b\006" +
      "proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          ClientMessage.getDescriptor(),
        });
    internal_static_Login_C1_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_Login_C1_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Login_C1_descriptor,
        new String[] { "ProtoId", "OpenId", "Uid", "Version", "DeviceId", "ServerName", "Ccc", });
    internal_static_Login_S1_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_Login_S1_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Login_S1_descriptor,
        new String[] { "ProtoId", });
    ClientMessage.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}