/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.bamisu.log.sdkthrift.entities;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.11.0)", date = "2022-06-11")
public class TLinkAccountResult implements org.apache.thrift.TBase<TLinkAccountResult, TLinkAccountResult._Fields>, java.io.Serializable, Cloneable, Comparable<TLinkAccountResult> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TLinkAccountResult");

  private static final org.apache.thrift.protocol.TField USER_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("userID", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField SERVER_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("serverID", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField SOCIAL_NETWORK_FIELD_DESC = new org.apache.thrift.protocol.TField("socialNetwork", org.apache.thrift.protocol.TType.I32, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new TLinkAccountResultStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new TLinkAccountResultTupleSchemeFactory();

  public long userID; // required
  public int serverID; // required
  public int socialNetwork; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    USER_ID((short)1, "userID"),
    SERVER_ID((short)2, "serverID"),
    SOCIAL_NETWORK((short)3, "socialNetwork");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // USER_ID
          return USER_ID;
        case 2: // SERVER_ID
          return SERVER_ID;
        case 3: // SOCIAL_NETWORK
          return SOCIAL_NETWORK;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __USERID_ISSET_ID = 0;
  private static final int __SERVERID_ISSET_ID = 1;
  private static final int __SOCIALNETWORK_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.USER_ID, new org.apache.thrift.meta_data.FieldMetaData("userID", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "long")));
    tmpMap.put(_Fields.SERVER_ID, new org.apache.thrift.meta_data.FieldMetaData("serverID", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32        , "int")));
    tmpMap.put(_Fields.SOCIAL_NETWORK, new org.apache.thrift.meta_data.FieldMetaData("socialNetwork", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32        , "int")));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TLinkAccountResult.class, metaDataMap);
  }

  public TLinkAccountResult() {
  }

  public TLinkAccountResult(
    long userID,
    int serverID,
    int socialNetwork)
  {
    this();
    this.userID = userID;
    setUserIDIsSet(true);
    this.serverID = serverID;
    setServerIDIsSet(true);
    this.socialNetwork = socialNetwork;
    setSocialNetworkIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TLinkAccountResult(TLinkAccountResult other) {
    __isset_bitfield = other.__isset_bitfield;
    this.userID = other.userID;
    this.serverID = other.serverID;
    this.socialNetwork = other.socialNetwork;
  }

  public TLinkAccountResult deepCopy() {
    return new TLinkAccountResult(this);
  }

  @Override
  public void clear() {
    setUserIDIsSet(false);
    this.userID = 0;
    setServerIDIsSet(false);
    this.serverID = 0;
    setSocialNetworkIsSet(false);
    this.socialNetwork = 0;
  }

  public long getUserID() {
    return this.userID;
  }

  public TLinkAccountResult setUserID(long userID) {
    this.userID = userID;
    setUserIDIsSet(true);
    return this;
  }

  public void unsetUserID() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __USERID_ISSET_ID);
  }

  /** Returns true if field userID is set (has been assigned a value) and false otherwise */
  public boolean isSetUserID() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __USERID_ISSET_ID);
  }

  public void setUserIDIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __USERID_ISSET_ID, value);
  }

  public int getServerID() {
    return this.serverID;
  }

  public TLinkAccountResult setServerID(int serverID) {
    this.serverID = serverID;
    setServerIDIsSet(true);
    return this;
  }

  public void unsetServerID() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __SERVERID_ISSET_ID);
  }

  /** Returns true if field serverID is set (has been assigned a value) and false otherwise */
  public boolean isSetServerID() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __SERVERID_ISSET_ID);
  }

  public void setServerIDIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __SERVERID_ISSET_ID, value);
  }

  public int getSocialNetwork() {
    return this.socialNetwork;
  }

  public TLinkAccountResult setSocialNetwork(int socialNetwork) {
    this.socialNetwork = socialNetwork;
    setSocialNetworkIsSet(true);
    return this;
  }

  public void unsetSocialNetwork() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __SOCIALNETWORK_ISSET_ID);
  }

  /** Returns true if field socialNetwork is set (has been assigned a value) and false otherwise */
  public boolean isSetSocialNetwork() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __SOCIALNETWORK_ISSET_ID);
  }

  public void setSocialNetworkIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __SOCIALNETWORK_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case USER_ID:
      if (value == null) {
        unsetUserID();
      } else {
        setUserID((java.lang.Long)value);
      }
      break;

    case SERVER_ID:
      if (value == null) {
        unsetServerID();
      } else {
        setServerID((java.lang.Integer)value);
      }
      break;

    case SOCIAL_NETWORK:
      if (value == null) {
        unsetSocialNetwork();
      } else {
        setSocialNetwork((java.lang.Integer)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case USER_ID:
      return getUserID();

    case SERVER_ID:
      return getServerID();

    case SOCIAL_NETWORK:
      return getSocialNetwork();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case USER_ID:
      return isSetUserID();
    case SERVER_ID:
      return isSetServerID();
    case SOCIAL_NETWORK:
      return isSetSocialNetwork();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof TLinkAccountResult)
      return this.equals((TLinkAccountResult)that);
    return false;
  }

  public boolean equals(TLinkAccountResult that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_userID = true;
    boolean that_present_userID = true;
    if (this_present_userID || that_present_userID) {
      if (!(this_present_userID && that_present_userID))
        return false;
      if (this.userID != that.userID)
        return false;
    }

    boolean this_present_serverID = true;
    boolean that_present_serverID = true;
    if (this_present_serverID || that_present_serverID) {
      if (!(this_present_serverID && that_present_serverID))
        return false;
      if (this.serverID != that.serverID)
        return false;
    }

    boolean this_present_socialNetwork = true;
    boolean that_present_socialNetwork = true;
    if (this_present_socialNetwork || that_present_socialNetwork) {
      if (!(this_present_socialNetwork && that_present_socialNetwork))
        return false;
      if (this.socialNetwork != that.socialNetwork)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(userID);

    hashCode = hashCode * 8191 + serverID;

    hashCode = hashCode * 8191 + socialNetwork;

    return hashCode;
  }

  @Override
  public int compareTo(TLinkAccountResult other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetUserID()).compareTo(other.isSetUserID());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUserID()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.userID, other.userID);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetServerID()).compareTo(other.isSetServerID());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetServerID()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.serverID, other.serverID);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetSocialNetwork()).compareTo(other.isSetSocialNetwork());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSocialNetwork()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.socialNetwork, other.socialNetwork);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("TLinkAccountResult(");
    boolean first = true;

    sb.append("userID:");
    sb.append(this.userID);
    first = false;
    if (!first) sb.append(", ");
    sb.append("serverID:");
    sb.append(this.serverID);
    first = false;
    if (!first) sb.append(", ");
    sb.append("socialNetwork:");
    sb.append(this.socialNetwork);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TLinkAccountResultStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TLinkAccountResultStandardScheme getScheme() {
      return new TLinkAccountResultStandardScheme();
    }
  }

  private static class TLinkAccountResultStandardScheme extends org.apache.thrift.scheme.StandardScheme<TLinkAccountResult> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TLinkAccountResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // USER_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.userID = iprot.readI64();
              struct.setUserIDIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // SERVER_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.serverID = iprot.readI32();
              struct.setServerIDIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // SOCIAL_NETWORK
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.socialNetwork = iprot.readI32();
              struct.setSocialNetworkIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TLinkAccountResult struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(USER_ID_FIELD_DESC);
      oprot.writeI64(struct.userID);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(SERVER_ID_FIELD_DESC);
      oprot.writeI32(struct.serverID);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(SOCIAL_NETWORK_FIELD_DESC);
      oprot.writeI32(struct.socialNetwork);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TLinkAccountResultTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TLinkAccountResultTupleScheme getScheme() {
      return new TLinkAccountResultTupleScheme();
    }
  }

  private static class TLinkAccountResultTupleScheme extends org.apache.thrift.scheme.TupleScheme<TLinkAccountResult> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TLinkAccountResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetUserID()) {
        optionals.set(0);
      }
      if (struct.isSetServerID()) {
        optionals.set(1);
      }
      if (struct.isSetSocialNetwork()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetUserID()) {
        oprot.writeI64(struct.userID);
      }
      if (struct.isSetServerID()) {
        oprot.writeI32(struct.serverID);
      }
      if (struct.isSetSocialNetwork()) {
        oprot.writeI32(struct.socialNetwork);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TLinkAccountResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.userID = iprot.readI64();
        struct.setUserIDIsSet(true);
      }
      if (incoming.get(1)) {
        struct.serverID = iprot.readI32();
        struct.setServerIDIsSet(true);
      }
      if (incoming.get(2)) {
        struct.socialNetwork = iprot.readI32();
        struct.setSocialNetworkIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

