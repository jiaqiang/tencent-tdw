/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.service;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class HiveServerException extends Exception implements
    org.apache.thrift.TBase<HiveServerException, HiveServerException._Fields>,
    java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
      "HiveServerException");

  private static final org.apache.thrift.protocol.TField MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField(
      "message", org.apache.thrift.protocol.TType.STRING, (short) 1);
  private static final org.apache.thrift.protocol.TField ERROR_CODE_FIELD_DESC = new org.apache.thrift.protocol.TField(
      "errorCode", org.apache.thrift.protocol.TType.I32, (short) 2);
  private static final org.apache.thrift.protocol.TField SQLSTATE_FIELD_DESC = new org.apache.thrift.protocol.TField(
      "SQLState", org.apache.thrift.protocol.TType.STRING, (short) 3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class,
        new HiveServerExceptionStandardSchemeFactory());
    schemes.put(TupleScheme.class, new HiveServerExceptionTupleSchemeFactory());
  }

  private String message;
  private int errorCode;
  private String SQLState;

  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    MESSAGE((short) 1, "message"), ERROR_CODE((short) 2, "errorCode"), SQLSTATE(
        (short) 3, "SQLState");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    public static _Fields findByThriftId(int fieldId) {
      switch (fieldId) {
      case 1:
        return MESSAGE;
      case 2:
        return ERROR_CODE;
      case 3:
        return SQLSTATE;
      default:
        return null;
      }
    }

    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null)
        throw new IllegalArgumentException("Field " + fieldId
            + " doesn't exist!");
      return fields;
    }

    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  private static final int __ERRORCODE_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);
  private _Fields optionals[] = { _Fields.ERROR_CODE, _Fields.SQLSTATE };
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
        _Fields.class);
    tmpMap.put(_Fields.MESSAGE, new org.apache.thrift.meta_data.FieldMetaData(
        "message", org.apache.thrift.TFieldRequirementType.DEFAULT,
        new org.apache.thrift.meta_data.FieldValueMetaData(
            org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ERROR_CODE,
        new org.apache.thrift.meta_data.FieldMetaData("errorCode",
            org.apache.thrift.TFieldRequirementType.OPTIONAL,
            new org.apache.thrift.meta_data.FieldValueMetaData(
                org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.SQLSTATE, new org.apache.thrift.meta_data.FieldMetaData(
        "SQLState", org.apache.thrift.TFieldRequirementType.OPTIONAL,
        new org.apache.thrift.meta_data.FieldValueMetaData(
            org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
        HiveServerException.class, metaDataMap);
  }

  public HiveServerException() {
  }

  public HiveServerException(String message) {
    this();
    this.message = message;
  }

  public HiveServerException(HiveServerException other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetMessage()) {
      this.message = other.message;
    }
    this.errorCode = other.errorCode;
    if (other.isSetSQLState()) {
      this.SQLState = other.SQLState;
    }
  }

  public HiveServerException deepCopy() {
    return new HiveServerException(this);
  }

  @Override
  public void clear() {
    this.message = null;
    setErrorCodeIsSet(false);
    this.errorCode = 0;
    this.SQLState = null;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void unsetMessage() {
    this.message = null;
  }

  public boolean isSetMessage() {
    return this.message != null;
  }

  public void setMessageIsSet(boolean value) {
    if (!value) {
      this.message = null;
    }
  }

  public int getErrorCode() {
    return this.errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
    setErrorCodeIsSet(true);
  }

  public void unsetErrorCode() {
    __isset_bit_vector.clear(__ERRORCODE_ISSET_ID);
  }

  public boolean isSetErrorCode() {
    return __isset_bit_vector.get(__ERRORCODE_ISSET_ID);
  }

  public void setErrorCodeIsSet(boolean value) {
    __isset_bit_vector.set(__ERRORCODE_ISSET_ID, value);
  }

  public String getSQLState() {
    return this.SQLState;
  }

  public void setSQLState(String SQLState) {
    this.SQLState = SQLState;
  }

  public void unsetSQLState() {
    this.SQLState = null;
  }

  public boolean isSetSQLState() {
    return this.SQLState != null;
  }

  public void setSQLStateIsSet(boolean value) {
    if (!value) {
      this.SQLState = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case MESSAGE:
      if (value == null) {
        unsetMessage();
      } else {
        setMessage((String) value);
      }
      break;

    case ERROR_CODE:
      if (value == null) {
        unsetErrorCode();
      } else {
        setErrorCode((Integer) value);
      }
      break;

    case SQLSTATE:
      if (value == null) {
        unsetSQLState();
      } else {
        setSQLState((String) value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case MESSAGE:
      return getMessage();

    case ERROR_CODE:
      return Integer.valueOf(getErrorCode());

    case SQLSTATE:
      return getSQLState();

    }
    throw new IllegalStateException();
  }

  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case MESSAGE:
      return isSetMessage();
    case ERROR_CODE:
      return isSetErrorCode();
    case SQLSTATE:
      return isSetSQLState();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof HiveServerException)
      return this.equals((HiveServerException) that);
    return false;
  }

  public boolean equals(HiveServerException that) {
    if (that == null)
      return false;

    boolean this_present_message = true && this.isSetMessage();
    boolean that_present_message = true && that.isSetMessage();
    if (this_present_message || that_present_message) {
      if (!(this_present_message && that_present_message))
        return false;
      if (!this.message.equals(that.message))
        return false;
    }

    boolean this_present_errorCode = true && this.isSetErrorCode();
    boolean that_present_errorCode = true && that.isSetErrorCode();
    if (this_present_errorCode || that_present_errorCode) {
      if (!(this_present_errorCode && that_present_errorCode))
        return false;
      if (this.errorCode != that.errorCode)
        return false;
    }

    boolean this_present_SQLState = true && this.isSetSQLState();
    boolean that_present_SQLState = true && that.isSetSQLState();
    if (this_present_SQLState || that_present_SQLState) {
      if (!(this_present_SQLState && that_present_SQLState))
        return false;
      if (!this.SQLState.equals(that.SQLState))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(HiveServerException other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    HiveServerException typedOther = (HiveServerException) other;

    lastComparison = Boolean.valueOf(isSetMessage()).compareTo(
        typedOther.isSetMessage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMessage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.message,
          typedOther.message);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetErrorCode()).compareTo(
        typedOther.isSetErrorCode());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetErrorCode()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.errorCode,
          typedOther.errorCode);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetSQLState()).compareTo(
        typedOther.isSetSQLState());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSQLState()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.SQLState,
          typedOther.SQLState);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot)
      throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot)
      throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("HiveServerException(");
    boolean first = true;

    sb.append("message:");
    if (this.message == null) {
      sb.append("null");
    } else {
      sb.append(this.message);
    }
    first = false;
    if (isSetErrorCode()) {
      if (!first)
        sb.append(", ");
      sb.append("errorCode:");
      sb.append(this.errorCode);
      first = false;
    }
    if (isSetSQLState()) {
      if (!first)
        sb.append(", ");
      sb.append("SQLState:");
      if (this.SQLState == null) {
        sb.append("null");
      } else {
        sb.append(this.SQLState);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
  }

  private void writeObject(java.io.ObjectOutputStream out)
      throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(
          new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in)
      throws java.io.IOException, ClassNotFoundException {
    try {
      __isset_bit_vector = new BitSet(1);
      read(new org.apache.thrift.protocol.TCompactProtocol(
          new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class HiveServerExceptionStandardSchemeFactory implements
      SchemeFactory {
    public HiveServerExceptionStandardScheme getScheme() {
      return new HiveServerExceptionStandardScheme();
    }
  }

  private static class HiveServerExceptionStandardScheme extends
      StandardScheme<HiveServerException> {

    public void read(org.apache.thrift.protocol.TProtocol iprot,
        HiveServerException struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true) {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
          break;
        }
        switch (schemeField.id) {
        case 1:
          if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
            struct.message = iprot.readString();
            struct.setMessageIsSet(true);
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
                schemeField.type);
          }
          break;
        case 2:
          if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
            struct.errorCode = iprot.readI32();
            struct.setErrorCodeIsSet(true);
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
                schemeField.type);
          }
          break;
        case 3:
          if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
            struct.SQLState = iprot.readString();
            struct.setSQLStateIsSet(true);
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
                schemeField.type);
          }
          break;
        default:
          org.apache.thrift.protocol.TProtocolUtil
              .skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot,
        HiveServerException struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.message != null) {
        oprot.writeFieldBegin(MESSAGE_FIELD_DESC);
        oprot.writeString(struct.message);
        oprot.writeFieldEnd();
      }
      if (struct.isSetErrorCode()) {
        oprot.writeFieldBegin(ERROR_CODE_FIELD_DESC);
        oprot.writeI32(struct.errorCode);
        oprot.writeFieldEnd();
      }
      if (struct.SQLState != null) {
        if (struct.isSetSQLState()) {
          oprot.writeFieldBegin(SQLSTATE_FIELD_DESC);
          oprot.writeString(struct.SQLState);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class HiveServerExceptionTupleSchemeFactory implements
      SchemeFactory {
    public HiveServerExceptionTupleScheme getScheme() {
      return new HiveServerExceptionTupleScheme();
    }
  }

  private static class HiveServerExceptionTupleScheme extends
      TupleScheme<HiveServerException> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot,
        HiveServerException struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetMessage()) {
        optionals.set(0);
      }
      if (struct.isSetErrorCode()) {
        optionals.set(1);
      }
      if (struct.isSetSQLState()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetMessage()) {
        oprot.writeString(struct.message);
      }
      if (struct.isSetErrorCode()) {
        oprot.writeI32(struct.errorCode);
      }
      if (struct.isSetSQLState()) {
        oprot.writeString(struct.SQLState);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot,
        HiveServerException struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.message = iprot.readString();
        struct.setMessageIsSet(true);
      }
      if (incoming.get(1)) {
        struct.errorCode = iprot.readI32();
        struct.setErrorCodeIsSet(true);
      }
      if (incoming.get(2)) {
        struct.SQLState = iprot.readString();
        struct.setSQLStateIsSet(true);
      }
    }
  }

}
