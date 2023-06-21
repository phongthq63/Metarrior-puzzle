/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.bamisu.log.sdkthrift.entities;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.11.0)", date = "2022-06-11")
public class TSwitchAccountResult implements org.apache.thrift.TBase<TSwitchAccountResult, TSwitchAccountResult._Fields>, java.io.Serializable, Cloneable, Comparable<TSwitchAccountResult> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TSwitchAccountResult");

  private static final org.apache.thrift.protocol.TField LOGIN_KEY_FIELD_DESC = new org.apache.thrift.protocol.TField("loginKey", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField ADDR_FIELD_DESC = new org.apache.thrift.protocol.TField("addr", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField PORT_FIELD_DESC = new org.apache.thrift.protocol.TField("port", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField ZONE_FIELD_DESC = new org.apache.thrift.protocol.TField("zone", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField SERVER_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("serverID", org.apache.thrift.protocol.TType.I32, (short)5);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new TSwitchAccountResultStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new TSwitchAccountResultTupleSchemeFactory();

  public java.lang.String loginKey; // required
  public java.lang.String addr; // required
  public int port; // required
  public java.lang.String zone; // required
  public int serverID; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    LOGIN_KEY((short)1, "loginKey"),
    ADDR((short)2, "addr"),
    PORT((short)3, "port"),
    ZONE((short)4, "zone"),
    SERVER_ID((short)5, "serverID");

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
        case 1: // LOGIN_KEY
          return LOGIN_KEY;
        case 2: // ADDR
          return ADDR;
        case 3: // PORT
          return PORT;
        case 4: // ZONE
          return ZONE;
        case 5: // SERVER_ID
          return SERVER_ID;
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
  private static final int __PORT_ISSET_ID = 0;
  private static final int __SERVERID_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.LOGIN_KEY, new org.apache.thrift.meta_data.FieldMetaData("loginKey", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ADDR, new org.apache.thrift.meta_data.FieldMetaData("addr", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PORT, new org.apache.thrift.meta_data.FieldMetaData("port", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32        , "int")));
    tmpMap.put(_Fields.ZONE, new org.apache.thrift.meta_data.FieldMetaData("zone", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.SERVER_ID, new org.apache.thrift.meta_data.FieldMetaData("serverID", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32        , "int")));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TSwitchAccountResult.class, metaDataMap);
  }

  public TSwitchAccountResult() {
  }

  public TSwitchAccountResult(
    java.lang.String loginKey,
    java.lang.String addr,
    int port,
    java.lang.String zone,
    int serverID)
  {
    this();
    this.loginKey = loginKey;
    this.addr = addr;
    this.port = port;
    setPortIsSet(true);
    this.zone = zone;
    this.serverID = serverID;
    setServerIDIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TSwitchAccountResult(TSwitchAccountResult other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetLoginKey()) {
      this.loginKey = other.loginKey;
    }
    if (other.isSetAddr()) {
      this.addr = other.addr;
    }
    this.port = other.port;
    if (other.isSetZone()) {
      this.zone = other.zone;
    }
    this.serverID = other.serverID;
  }

  public TSwitchAccountResult deepCopy() {
    return new TSwitchAccountResult(this);
  }

  @Override
  public void clear() {
    this.loginKey = null;
    this.addr = null;
    setPortIsSet(false);
    this.port = 0;
    this.zone = null;
    setServerIDIsSet(false);
    this.serverID = 0;
  }

  public java.lang.String getLoginKey() {
    return this.loginKey;
  }

  public TSwitchAccountResult setLoginKey(java.lang.String loginKey) {
    this.loginKey = loginKey;
    return this;
  }

  public void unsetLoginKey() {
    this.loginKey = null;
  }

  /** Returns true if field loginKey is set (has been assigned a value) and false otherwise */
  public boolean isSetLoginKey() {
    return this.loginKey != null;
  }

  public void setLoginKeyIsSet(boolean value) {
    if (!value) {
      this.loginKey = null;
    }
  }

  public java.lang.String getAddr() {
    return this.addr;
  }

  public TSwitchAccountResult setAddr(java.lang.String addr) {
    this.addr = addr;
    return this;
  }

  public void unsetAddr() {
    this.addr = null;
  }

  /** Returns true if field addr is set (has been assigned a value) and false otherwise */
  public boolean isSetAddr() {
    return this.addr != null;
  }

  public void setAddrIsSet(boolean value) {
    if (!value) {
      this.addr = null;
    }
  }

  public int getPort() {
    return this.port;
  }

  public TSwitchAccountResult setPort(int port) {
    this.port = port;
    setPortIsSet(true);
    return this;
  }

  public void unsetPort() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __PORT_ISSET_ID);
  }

  /** Returns true if field port is set (has been assigned a value) and false otherwise */
  public boolean isSetPort() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __PORT_ISSET_ID);
  }

  public void setPortIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __PORT_ISSET_ID, value);
  }

  public java.lang.String getZone() {
    return this.zone;
  }

  public TSwitchAccountResult setZone(java.lang.String zone) {
    this.zone = zone;
    return this;
  }

  public void unsetZone() {
    this.zone = null;
  }

  /** Returns true if field zone is set (has been assigned a value) and false otherwise */
  public boolean isSetZone() {
    return this.zone != null;
  }

  public void setZoneIsSet(boolean value) {
    if (!value) {
      this.zone = null;
    }
  }

  public int getServerID() {
    return this.serverID;
  }

  public TSwitchAccountResult setServerID(int serverID) {
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

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case LOGIN_KEY:
      if (value == null) {
        unsetLoginKey();
      } else {
        setLoginKey((java.lang.String)value);
      }
      break;

    case ADDR:
      if (value == null) {
        unsetAddr();
      } else {
        setAddr((java.lang.String)value);
      }
      break;

    case PORT:
      if (value == null) {
        unsetPort();
      } else {
        setPort((java.lang.Integer)value);
      }
      break;

    case ZONE:
      if (value == null) {
        unsetZone();
      } else {
        setZone((java.lang.String)value);
      }
      break;

    case SERVER_ID:
      if (value == null) {
        unsetServerID();
      } else {
        setServerID((java.lang.Integer)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case LOGIN_KEY:
      return getLoginKey();

    case ADDR:
      return getAddr();

    case PORT:
      return getPort();

    case ZONE:
      return getZone();

    case SERVER_ID:
      return getServerID();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case LOGIN_KEY:
      return isSetLoginKey();
    case ADDR:
      return isSetAddr();
    case PORT:
      return isSetPort();
    case ZONE:
      return isSetZone();
    case SERVER_ID:
      return isSetServerID();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof TSwitchAccountResult)
      return this.equals((TSwitchAccountResult)that);
    return false;
  }

  public boolean equals(TSwitchAccountResult that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_loginKey = true && this.isSetLoginKey();
    boolean that_present_loginKey = true && that.isSetLoginKey();
    if (this_present_loginKey || that_present_loginKey) {
      if (!(this_present_loginKey && that_present_loginKey))
        return false;
      if (!this.loginKey.equals(that.loginKey))
        return false;
    }

    boolean this_present_addr = true && this.isSetAddr();
    boolean that_present_addr = true && that.isSetAddr();
    if (this_present_addr || that_present_addr) {
      if (!(this_present_addr && that_present_addr))
        return false;
      if (!this.addr.equals(that.addr))
        return false;
    }

    boolean this_present_port = true;
    boolean that_present_port = true;
    if (this_present_port || that_present_port) {
      if (!(this_present_port && that_present_port))
        return false;
      if (this.port != that.port)
        return false;
    }

    boolean this_present_zone = true && this.isSetZone();
    boolean that_present_zone = true && that.isSetZone();
    if (this_present_zone || that_present_zone) {
      if (!(this_present_zone && that_present_zone))
        return false;
      if (!this.zone.equals(that.zone))
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

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetLoginKey()) ? 131071 : 524287);
    if (isSetLoginKey())
      hashCode = hashCode * 8191 + loginKey.hashCode();

    hashCode = hashCode * 8191 + ((isSetAddr()) ? 131071 : 524287);
    if (isSetAddr())
      hashCode = hashCode * 8191 + addr.hashCode();

    hashCode = hashCode * 8191 + port;

    hashCode = hashCode * 8191 + ((isSetZone()) ? 131071 : 524287);
    if (isSetZone())
      hashCode = hashCode * 8191 + zone.hashCode();

    hashCode = hashCode * 8191 + serverID;

    return hashCode;
  }

  @Override
  public int compareTo(TSwitchAccountResult other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetLoginKey()).compareTo(other.isSetLoginKey());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLoginKey()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.loginKey, other.loginKey);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetAddr()).compareTo(other.isSetAddr());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAddr()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.addr, other.addr);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetPort()).compareTo(other.isSetPort());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPort()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.port, other.port);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetZone()).compareTo(other.isSetZone());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetZone()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.zone, other.zone);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("TSwitchAccountResult(");
    boolean first = true;

    sb.append("loginKey:");
    if (this.loginKey == null) {
      sb.append("null");
    } else {
      sb.append(this.loginKey);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("addr:");
    if (this.addr == null) {
      sb.append("null");
    } else {
      sb.append(this.addr);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("port:");
    sb.append(this.port);
    first = false;
    if (!first) sb.append(", ");
    sb.append("zone:");
    if (this.zone == null) {
      sb.append("null");
    } else {
      sb.append(this.zone);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("serverID:");
    sb.append(this.serverID);
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

  private static class TSwitchAccountResultStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TSwitchAccountResultStandardScheme getScheme() {
      return new TSwitchAccountResultStandardScheme();
    }
  }

  private static class TSwitchAccountResultStandardScheme extends org.apache.thrift.scheme.StandardScheme<TSwitchAccountResult> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TSwitchAccountResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // LOGIN_KEY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.loginKey = iprot.readString();
              struct.setLoginKeyIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // ADDR
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.addr = iprot.readString();
              struct.setAddrIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // PORT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.port = iprot.readI32();
              struct.setPortIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // ZONE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.zone = iprot.readString();
              struct.setZoneIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // SERVER_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.serverID = iprot.readI32();
              struct.setServerIDIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, TSwitchAccountResult struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.loginKey != null) {
        oprot.writeFieldBegin(LOGIN_KEY_FIELD_DESC);
        oprot.writeString(struct.loginKey);
        oprot.writeFieldEnd();
      }
      if (struct.addr != null) {
        oprot.writeFieldBegin(ADDR_FIELD_DESC);
        oprot.writeString(struct.addr);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(PORT_FIELD_DESC);
      oprot.writeI32(struct.port);
      oprot.writeFieldEnd();
      if (struct.zone != null) {
        oprot.writeFieldBegin(ZONE_FIELD_DESC);
        oprot.writeString(struct.zone);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(SERVER_ID_FIELD_DESC);
      oprot.writeI32(struct.serverID);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TSwitchAccountResultTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TSwitchAccountResultTupleScheme getScheme() {
      return new TSwitchAccountResultTupleScheme();
    }
  }

  private static class TSwitchAccountResultTupleScheme extends org.apache.thrift.scheme.TupleScheme<TSwitchAccountResult> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TSwitchAccountResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetLoginKey()) {
        optionals.set(0);
      }
      if (struct.isSetAddr()) {
        optionals.set(1);
      }
      if (struct.isSetPort()) {
        optionals.set(2);
      }
      if (struct.isSetZone()) {
        optionals.set(3);
      }
      if (struct.isSetServerID()) {
        optionals.set(4);
      }
      oprot.writeBitSet(optionals, 5);
      if (struct.isSetLoginKey()) {
        oprot.writeString(struct.loginKey);
      }
      if (struct.isSetAddr()) {
        oprot.writeString(struct.addr);
      }
      if (struct.isSetPort()) {
        oprot.writeI32(struct.port);
      }
      if (struct.isSetZone()) {
        oprot.writeString(struct.zone);
      }
      if (struct.isSetServerID()) {
        oprot.writeI32(struct.serverID);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TSwitchAccountResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(5);
      if (incoming.get(0)) {
        struct.loginKey = iprot.readString();
        struct.setLoginKeyIsSet(true);
      }
      if (incoming.get(1)) {
        struct.addr = iprot.readString();
        struct.setAddrIsSet(true);
      }
      if (incoming.get(2)) {
        struct.port = iprot.readI32();
        struct.setPortIsSet(true);
      }
      if (incoming.get(3)) {
        struct.zone = iprot.readString();
        struct.setZoneIsSet(true);
      }
      if (incoming.get(4)) {
        struct.serverID = iprot.readI32();
        struct.setServerIDIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

