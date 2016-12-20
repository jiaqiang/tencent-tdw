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
package org.apache.hadoop.hive.metastore.api;

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

public class Type implements org.apache.thrift.TBase<Type, Type._Fields>,
    java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
      "Type");

  private static final org.apache.thrift.protocol.TField NAME_FIELD_DESC = new org.apache.thrift.protocol.TField(
      "name", org.apache.thrift.protocol.TType.STRING, (short) 1);
  private static final org.apache.thrift.protocol.TField TYPE1_FIELD_DESC = new org.apache.thrift.protocol.TField(
      "type1", org.apache.thrift.protocol.TType.STRING, (short) 2);
  private static final org.apache.thrift.protocol.TField TYPE2_FIELD_DESC = new org.apache.thrift.protocol.TField(
      "type2", org.apache.thrift.protocol.TType.STRING, (short) 3);
  private static final org.apache.thrift.protocol.TField FIELDS_FIELD_DESC = new org.apache.thrift.protocol.TField(
      "fields", org.apache.thrift.protocol.TType.LIST, (short) 4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TypeStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TypeTupleSchemeFactory());
  }

  private String name;
  private String type1;
  private String type2;
  private List<FieldSchema> fields;

  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    NAME((short) 1, "name"), TYPE1((short) 2, "type1"), TYPE2((short) 3,
        "type2"), FIELDS((short) 4, "fields");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    public static _Fields findByThriftId(int fieldId) {
      switch (fieldId) {
      case 1:
        return NAME;
      case 2:
        return TYPE1;
      case 3:
        return TYPE2;
      case 4:
        return FIELDS;
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

  private _Fields optionals[] = { _Fields.TYPE1, _Fields.TYPE2, _Fields.FIELDS };
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
        _Fields.class);
    tmpMap.put(_Fields.NAME, new org.apache.thrift.meta_data.FieldMetaData(
        "name", org.apache.thrift.TFieldRequirementType.DEFAULT,
        new org.apache.thrift.meta_data.FieldValueMetaData(
            org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TYPE1, new org.apache.thrift.meta_data.FieldMetaData(
        "type1", org.apache.thrift.TFieldRequirementType.OPTIONAL,
        new org.apache.thrift.meta_data.FieldValueMetaData(
            org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TYPE2, new org.apache.thrift.meta_data.FieldMetaData(
        "type2", org.apache.thrift.TFieldRequirementType.OPTIONAL,
        new org.apache.thrift.meta_data.FieldValueMetaData(
            org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.FIELDS, new org.apache.thrift.meta_data.FieldMetaData(
        "fields", org.apache.thrift.TFieldRequirementType.OPTIONAL,
        new org.apache.thrift.meta_data.ListMetaData(
            org.apache.thrift.protocol.TType.LIST,
            new org.apache.thrift.meta_data.StructMetaData(
                org.apache.thrift.protocol.TType.STRUCT, FieldSchema.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Type.class,
        metaDataMap);
  }

  public Type() {
  }

  public Type(String name) {
    this();
    this.name = name;
  }

  public Type(Type other) {
    if (other.isSetName()) {
      this.name = other.name;
    }
    if (other.isSetType1()) {
      this.type1 = other.type1;
    }
    if (other.isSetType2()) {
      this.type2 = other.type2;
    }
    if (other.isSetFields()) {
      List<FieldSchema> __this__fields = new ArrayList<FieldSchema>();
      for (FieldSchema other_element : other.fields) {
        __this__fields.add(new FieldSchema(other_element));
      }
      this.fields = __this__fields;
    }
  }

  public Type(String name2, String type12, String type22,
      List<FieldSchema> fields2) {
    this.name = name2;
    this.type1 = type12;
    this.type2 = type22;
    this.fields = fields2;
  }

  public Type deepCopy() {
    return new Type(this);
  }

  @Override
  public void clear() {
    this.name = null;
    this.type1 = null;
    this.type2 = null;
    this.fields = null;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void unsetName() {
    this.name = null;
  }

  public boolean isSetName() {
    return this.name != null;
  }

  public void setNameIsSet(boolean value) {
    if (!value) {
      this.name = null;
    }
  }

  public String getType1() {
    return this.type1;
  }

  public void setType1(String type1) {
    this.type1 = type1;
  }

  public void unsetType1() {
    this.type1 = null;
  }

  public boolean isSetType1() {
    return this.type1 != null;
  }

  public void setType1IsSet(boolean value) {
    if (!value) {
      this.type1 = null;
    }
  }

  public String getType2() {
    return this.type2;
  }

  public void setType2(String type2) {
    this.type2 = type2;
  }

  public void unsetType2() {
    this.type2 = null;
  }

  public boolean isSetType2() {
    return this.type2 != null;
  }

  public void setType2IsSet(boolean value) {
    if (!value) {
      this.type2 = null;
    }
  }

  public int getFieldsSize() {
    return (this.fields == null) ? 0 : this.fields.size();
  }

  public java.util.Iterator<FieldSchema> getFieldsIterator() {
    return (this.fields == null) ? null : this.fields.iterator();
  }

  public void addToFields(FieldSchema elem) {
    if (this.fields == null) {
      this.fields = new ArrayList<FieldSchema>();
    }
    this.fields.add(elem);
  }

  public List<FieldSchema> getFields() {
    return this.fields;
  }

  public void setFields(List<FieldSchema> fields) {
    this.fields = fields;
  }

  public void unsetFields() {
    this.fields = null;
  }

  public boolean isSetFields() {
    return this.fields != null;
  }

  public void setFieldsIsSet(boolean value) {
    if (!value) {
      this.fields = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case NAME:
      if (value == null) {
        unsetName();
      } else {
        setName((String) value);
      }
      break;

    case TYPE1:
      if (value == null) {
        unsetType1();
      } else {
        setType1((String) value);
      }
      break;

    case TYPE2:
      if (value == null) {
        unsetType2();
      } else {
        setType2((String) value);
      }
      break;

    case FIELDS:
      if (value == null) {
        unsetFields();
      } else {
        setFields((List<FieldSchema>) value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case NAME:
      return getName();

    case TYPE1:
      return getType1();

    case TYPE2:
      return getType2();

    case FIELDS:
      return getFields();

    }
    throw new IllegalStateException();
  }

  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case NAME:
      return isSetName();
    case TYPE1:
      return isSetType1();
    case TYPE2:
      return isSetType2();
    case FIELDS:
      return isSetFields();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Type)
      return this.equals((Type) that);
    return false;
  }

  public boolean equals(Type that) {
    if (that == null)
      return false;

    boolean this_present_name = true && this.isSetName();
    boolean that_present_name = true && that.isSetName();
    if (this_present_name || that_present_name) {
      if (!(this_present_name && that_present_name))
        return false;
      if (!this.name.equals(that.name))
        return false;
    }

    boolean this_present_type1 = true && this.isSetType1();
    boolean that_present_type1 = true && that.isSetType1();
    if (this_present_type1 || that_present_type1) {
      if (!(this_present_type1 && that_present_type1))
        return false;
      if (!this.type1.equals(that.type1))
        return false;
    }

    boolean this_present_type2 = true && this.isSetType2();
    boolean that_present_type2 = true && that.isSetType2();
    if (this_present_type2 || that_present_type2) {
      if (!(this_present_type2 && that_present_type2))
        return false;
      if (!this.type2.equals(that.type2))
        return false;
    }

    boolean this_present_fields = true && this.isSetFields();
    boolean that_present_fields = true && that.isSetFields();
    if (this_present_fields || that_present_fields) {
      if (!(this_present_fields && that_present_fields))
        return false;
      if (!this.fields.equals(that.fields))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(Type other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    Type typedOther = (Type) other;

    lastComparison = Boolean.valueOf(isSetName()).compareTo(
        typedOther.isSetName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.name,
          typedOther.name);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetType1()).compareTo(
        typedOther.isSetType1());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetType1()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.type1,
          typedOther.type1);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetType2()).compareTo(
        typedOther.isSetType2());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetType2()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.type2,
          typedOther.type2);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetFields()).compareTo(
        typedOther.isSetFields());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFields()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.fields,
          typedOther.fields);
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
    StringBuilder sb = new StringBuilder("Type(");
    boolean first = true;

    sb.append("name:");
    if (this.name == null) {
      sb.append("null");
    } else {
      sb.append(this.name);
    }
    first = false;
    if (isSetType1()) {
      if (!first)
        sb.append(", ");
      sb.append("type1:");
      if (this.type1 == null) {
        sb.append("null");
      } else {
        sb.append(this.type1);
      }
      first = false;
    }
    if (isSetType2()) {
      if (!first)
        sb.append(", ");
      sb.append("type2:");
      if (this.type2 == null) {
        sb.append("null");
      } else {
        sb.append(this.type2);
      }
      first = false;
    }
    if (isSetFields()) {
      if (!first)
        sb.append(", ");
      sb.append("fields:");
      if (this.fields == null) {
        sb.append("null");
      } else {
        sb.append(this.fields);
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
      read(new org.apache.thrift.protocol.TCompactProtocol(
          new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TypeStandardSchemeFactory implements SchemeFactory {
    public TypeStandardScheme getScheme() {
      return new TypeStandardScheme();
    }
  }

  private static class TypeStandardScheme extends StandardScheme<Type> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Type struct)
        throws org.apache.thrift.TException {
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
            struct.name = iprot.readString();
            struct.setNameIsSet(true);
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
                schemeField.type);
          }
          break;
        case 2:
          if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
            struct.type1 = iprot.readString();
            struct.setType1IsSet(true);
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
                schemeField.type);
          }
          break;
        case 3:
          if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
            struct.type2 = iprot.readString();
            struct.setType2IsSet(true);
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
                schemeField.type);
          }
          break;
        case 4:
          if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
            {
              org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
              struct.fields = new ArrayList<FieldSchema>(_list0.size);
              for (int _i1 = 0; _i1 < _list0.size; ++_i1) {
                FieldSchema _elem2;
                _elem2 = new FieldSchema();
                _elem2.read(iprot);
                struct.fields.add(_elem2);
              }
              iprot.readListEnd();
            }
            struct.setFieldsIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, Type struct)
        throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.name != null) {
        oprot.writeFieldBegin(NAME_FIELD_DESC);
        oprot.writeString(struct.name);
        oprot.writeFieldEnd();
      }
      if (struct.type1 != null) {
        if (struct.isSetType1()) {
          oprot.writeFieldBegin(TYPE1_FIELD_DESC);
          oprot.writeString(struct.type1);
          oprot.writeFieldEnd();
        }
      }
      if (struct.type2 != null) {
        if (struct.isSetType2()) {
          oprot.writeFieldBegin(TYPE2_FIELD_DESC);
          oprot.writeString(struct.type2);
          oprot.writeFieldEnd();
        }
      }
      if (struct.fields != null) {
        if (struct.isSetFields()) {
          oprot.writeFieldBegin(FIELDS_FIELD_DESC);
          {
            oprot.writeListBegin(new org.apache.thrift.protocol.TList(
                org.apache.thrift.protocol.TType.STRUCT, struct.fields.size()));
            for (FieldSchema _iter3 : struct.fields) {
              _iter3.write(oprot);
            }
            oprot.writeListEnd();
          }
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TypeTupleSchemeFactory implements SchemeFactory {
    public TypeTupleScheme getScheme() {
      return new TypeTupleScheme();
    }
  }

  private static class TypeTupleScheme extends TupleScheme<Type> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Type struct)
        throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetName()) {
        optionals.set(0);
      }
      if (struct.isSetType1()) {
        optionals.set(1);
      }
      if (struct.isSetType2()) {
        optionals.set(2);
      }
      if (struct.isSetFields()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetName()) {
        oprot.writeString(struct.name);
      }
      if (struct.isSetType1()) {
        oprot.writeString(struct.type1);
      }
      if (struct.isSetType2()) {
        oprot.writeString(struct.type2);
      }
      if (struct.isSetFields()) {
        {
          oprot.writeI32(struct.fields.size());
          for (FieldSchema _iter4 : struct.fields) {
            _iter4.write(oprot);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Type struct)
        throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.name = iprot.readString();
        struct.setNameIsSet(true);
      }
      if (incoming.get(1)) {
        struct.type1 = iprot.readString();
        struct.setType1IsSet(true);
      }
      if (incoming.get(2)) {
        struct.type2 = iprot.readString();
        struct.setType2IsSet(true);
      }
      if (incoming.get(3)) {
        {
          org.apache.thrift.protocol.TList _list5 = new org.apache.thrift.protocol.TList(
              org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.fields = new ArrayList<FieldSchema>(_list5.size);
          for (int _i6 = 0; _i6 < _list5.size; ++_i6) {
            FieldSchema _elem7;
            _elem7 = new FieldSchema();
            _elem7.read(iprot);
            struct.fields.add(_elem7);
          }
        }
        struct.setFieldsIsSet(true);
      }
    }
  }

}
