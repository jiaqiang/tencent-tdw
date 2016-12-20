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
package org.apache.hadoop.hive.serde2.thrift.test;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import org.apache.log4j.Logger;

import org.apache.thrift.*;
import org.apache.thrift.meta_data.*;
import org.apache.thrift.protocol.*;

public class Complex implements TBase, java.io.Serializable, Cloneable {
  private static final TStruct STRUCT_DESC = new TStruct("Complex");
  private static final TField AINT_FIELD_DESC = new TField("aint", TType.I32,
      (short) 1);
  private static final TField A_STRING_FIELD_DESC = new TField("aString",
      TType.STRING, (short) 2);
  private static final TField LINT_FIELD_DESC = new TField("lint", TType.LIST,
      (short) 3);
  private static final TField L_STRING_FIELD_DESC = new TField("lString",
      TType.LIST, (short) 4);
  private static final TField LINT_STRING_FIELD_DESC = new TField("lintString",
      TType.LIST, (short) 5);
  private static final TField M_STRING_STRING_FIELD_DESC = new TField(
      "mStringString", TType.MAP, (short) 6);

  public int aint;
  public static final int AINT = 1;
  public String aString;
  public static final int ASTRING = 2;
  public List<Integer> lint;
  public static final int LINT = 3;
  public List<String> lString;
  public static final int LSTRING = 4;
  public List<IntString> lintString;
  public static final int LINTSTRING = 5;
  public Map<String, String> mStringString;
  public static final int MSTRINGSTRING = 6;

  private final Isset __isset = new Isset();

  private static final class Isset implements java.io.Serializable {
    public boolean aint = false;
  }

  public static final Map<Integer, FieldMetaData> metaDataMap = Collections
      .unmodifiableMap(new HashMap<Integer, FieldMetaData>() {
        {
          put(AINT, new FieldMetaData("aint", TFieldRequirementType.DEFAULT,
              new FieldValueMetaData(TType.I32)));
          put(ASTRING, new FieldMetaData("aString",
              TFieldRequirementType.DEFAULT, new FieldValueMetaData(
                  TType.STRING)));
          put(LINT, new FieldMetaData("lint", TFieldRequirementType.DEFAULT,
              new ListMetaData(TType.LIST, new FieldValueMetaData(TType.I32))));
          put(LSTRING, new FieldMetaData("lString",
              TFieldRequirementType.DEFAULT, new ListMetaData(TType.LIST,
                  new FieldValueMetaData(TType.STRING))));
          put(LINTSTRING, new FieldMetaData("lintString",
              TFieldRequirementType.DEFAULT, new ListMetaData(TType.LIST,
                  new StructMetaData(TType.STRUCT, IntString.class))));
          put(MSTRINGSTRING, new FieldMetaData("mStringString",
              TFieldRequirementType.DEFAULT, new MapMetaData(TType.MAP,
                  new FieldValueMetaData(TType.STRING), new FieldValueMetaData(
                      TType.STRING))));
        }
      });

  static {
  }

  public Complex() {
  }

  public Complex(int aint, String aString, List<Integer> lint,
      List<String> lString, List<IntString> lintString,
      Map<String, String> mStringString) {
    this();
    this.aint = aint;
    this.__isset.aint = true;
    this.aString = aString;
    this.lint = lint;
    this.lString = lString;
    this.lintString = lintString;
    this.mStringString = mStringString;
  }

  public Complex(Complex other) {
    __isset.aint = other.__isset.aint;
    this.aint = other.aint;
    if (other.isSetAString()) {
      this.aString = other.aString;
    }
    if (other.isSetLint()) {
      List<Integer> __this__lint = new ArrayList<Integer>();
      for (Integer other_element : other.lint) {
        __this__lint.add(other_element);
      }
      this.lint = __this__lint;
    }
    if (other.isSetLString()) {
      List<String> __this__lString = new ArrayList<String>();
      for (String other_element : other.lString) {
        __this__lString.add(other_element);
      }
      this.lString = __this__lString;
    }
    if (other.isSetLintString()) {
      List<IntString> __this__lintString = new ArrayList<IntString>();
      for (IntString other_element : other.lintString) {
        __this__lintString.add(new IntString(other_element));
      }
      this.lintString = __this__lintString;
    }
    if (other.isSetMStringString()) {
      Map<String, String> __this__mStringString = new HashMap<String, String>();
      for (Map.Entry<String, String> other_element : other.mStringString
          .entrySet()) {

        String other_element_key = other_element.getKey();
        String other_element_value = other_element.getValue();

        String __this__mStringString_copy_key = other_element_key;

        String __this__mStringString_copy_value = other_element_value;

        __this__mStringString.put(__this__mStringString_copy_key,
            __this__mStringString_copy_value);
      }
      this.mStringString = __this__mStringString;
    }
  }

  @Override
  public Complex clone() {
    return new Complex(this);
  }

  public int getAint() {
    return this.aint;
  }

  public void setAint(int aint) {
    this.aint = aint;
    this.__isset.aint = true;
  }

  public void unsetAint() {
    this.__isset.aint = false;
  }

  public boolean isSetAint() {
    return this.__isset.aint;
  }

  public void setAintIsSet(boolean value) {
    this.__isset.aint = value;
  }

  public String getAString() {
    return this.aString;
  }

  public void setAString(String aString) {
    this.aString = aString;
  }

  public void unsetAString() {
    this.aString = null;
  }

  public boolean isSetAString() {
    return this.aString != null;
  }

  public void setAStringIsSet(boolean value) {
    if (!value) {
      this.aString = null;
    }
  }

  public int getLintSize() {
    return (this.lint == null) ? 0 : this.lint.size();
  }

  public java.util.Iterator<Integer> getLintIterator() {
    return (this.lint == null) ? null : this.lint.iterator();
  }

  public void addToLint(int elem) {
    if (this.lint == null) {
      this.lint = new ArrayList<Integer>();
    }
    this.lint.add(elem);
  }

  public List<Integer> getLint() {
    return this.lint;
  }

  public void setLint(List<Integer> lint) {
    this.lint = lint;
  }

  public void unsetLint() {
    this.lint = null;
  }

  public boolean isSetLint() {
    return this.lint != null;
  }

  public void setLintIsSet(boolean value) {
    if (!value) {
      this.lint = null;
    }
  }

  public int getLStringSize() {
    return (this.lString == null) ? 0 : this.lString.size();
  }

  public java.util.Iterator<String> getLStringIterator() {
    return (this.lString == null) ? null : this.lString.iterator();
  }

  public void addToLString(String elem) {
    if (this.lString == null) {
      this.lString = new ArrayList<String>();
    }
    this.lString.add(elem);
  }

  public List<String> getLString() {
    return this.lString;
  }

  public void setLString(List<String> lString) {
    this.lString = lString;
  }

  public void unsetLString() {
    this.lString = null;
  }

  public boolean isSetLString() {
    return this.lString != null;
  }

  public void setLStringIsSet(boolean value) {
    if (!value) {
      this.lString = null;
    }
  }

  public int getLintStringSize() {
    return (this.lintString == null) ? 0 : this.lintString.size();
  }

  public java.util.Iterator<IntString> getLintStringIterator() {
    return (this.lintString == null) ? null : this.lintString.iterator();
  }

  public void addToLintString(IntString elem) {
    if (this.lintString == null) {
      this.lintString = new ArrayList<IntString>();
    }
    this.lintString.add(elem);
  }

  public List<IntString> getLintString() {
    return this.lintString;
  }

  public void setLintString(List<IntString> lintString) {
    this.lintString = lintString;
  }

  public void unsetLintString() {
    this.lintString = null;
  }

  public boolean isSetLintString() {
    return this.lintString != null;
  }

  public void setLintStringIsSet(boolean value) {
    if (!value) {
      this.lintString = null;
    }
  }

  public int getMStringStringSize() {
    return (this.mStringString == null) ? 0 : this.mStringString.size();
  }

  public void putToMStringString(String key, String val) {
    if (this.mStringString == null) {
      this.mStringString = new HashMap<String, String>();
    }
    this.mStringString.put(key, val);
  }

  public Map<String, String> getMStringString() {
    return this.mStringString;
  }

  public void setMStringString(Map<String, String> mStringString) {
    this.mStringString = mStringString;
  }

  public void unsetMStringString() {
    this.mStringString = null;
  }

  public boolean isSetMStringString() {
    return this.mStringString != null;
  }

  public void setMStringStringIsSet(boolean value) {
    if (!value) {
      this.mStringString = null;
    }
  }

  public void setFieldValue(int fieldID, Object value) {
    switch (fieldID) {
    case AINT:
      if (value == null) {
        unsetAint();
      } else {
        setAint((Integer) value);
      }
      break;

    case ASTRING:
      if (value == null) {
        unsetAString();
      } else {
        setAString((String) value);
      }
      break;

    case LINT:
      if (value == null) {
        unsetLint();
      } else {
        setLint((List<Integer>) value);
      }
      break;

    case LSTRING:
      if (value == null) {
        unsetLString();
      } else {
        setLString((List<String>) value);
      }
      break;

    case LINTSTRING:
      if (value == null) {
        unsetLintString();
      } else {
        setLintString((List<IntString>) value);
      }
      break;

    case MSTRINGSTRING:
      if (value == null) {
        unsetMStringString();
      } else {
        setMStringString((Map<String, String>) value);
      }
      break;

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public Object getFieldValue(int fieldID) {
    switch (fieldID) {
    case AINT:
      return new Integer(getAint());

    case ASTRING:
      return getAString();

    case LINT:
      return getLint();

    case LSTRING:
      return getLString();

    case LINTSTRING:
      return getLintString();

    case MSTRINGSTRING:
      return getMStringString();

    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  public boolean isSet(int fieldID) {
    switch (fieldID) {
    case AINT:
      return isSetAint();
    case ASTRING:
      return isSetAString();
    case LINT:
      return isSetLint();
    case LSTRING:
      return isSetLString();
    case LINTSTRING:
      return isSetLintString();
    case MSTRINGSTRING:
      return isSetMStringString();
    default:
      throw new IllegalArgumentException("Field " + fieldID + " doesn't exist!");
    }
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Complex)
      return this.equals((Complex) that);
    return false;
  }

  public boolean equals(Complex that) {
    if (that == null)
      return false;

    boolean this_present_aint = true;
    boolean that_present_aint = true;
    if (this_present_aint || that_present_aint) {
      if (!(this_present_aint && that_present_aint))
        return false;
      if (this.aint != that.aint)
        return false;
    }

    boolean this_present_aString = true && this.isSetAString();
    boolean that_present_aString = true && that.isSetAString();
    if (this_present_aString || that_present_aString) {
      if (!(this_present_aString && that_present_aString))
        return false;
      if (!this.aString.equals(that.aString))
        return false;
    }

    boolean this_present_lint = true && this.isSetLint();
    boolean that_present_lint = true && that.isSetLint();
    if (this_present_lint || that_present_lint) {
      if (!(this_present_lint && that_present_lint))
        return false;
      if (!this.lint.equals(that.lint))
        return false;
    }

    boolean this_present_lString = true && this.isSetLString();
    boolean that_present_lString = true && that.isSetLString();
    if (this_present_lString || that_present_lString) {
      if (!(this_present_lString && that_present_lString))
        return false;
      if (!this.lString.equals(that.lString))
        return false;
    }

    boolean this_present_lintString = true && this.isSetLintString();
    boolean that_present_lintString = true && that.isSetLintString();
    if (this_present_lintString || that_present_lintString) {
      if (!(this_present_lintString && that_present_lintString))
        return false;
      if (!this.lintString.equals(that.lintString))
        return false;
    }

    boolean this_present_mStringString = true && this.isSetMStringString();
    boolean that_present_mStringString = true && that.isSetMStringString();
    if (this_present_mStringString || that_present_mStringString) {
      if (!(this_present_mStringString && that_present_mStringString))
        return false;
      if (!this.mStringString.equals(that.mStringString))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public void read(TProtocol iprot) throws TException {
    TField field;
    iprot.readStructBegin();
    while (true) {
      field = iprot.readFieldBegin();
      if (field.type == TType.STOP) {
        break;
      }
      switch (field.id) {
      case AINT:
        if (field.type == TType.I32) {
          this.aint = iprot.readI32();
          this.__isset.aint = true;
        } else {
          TProtocolUtil.skip(iprot, field.type);
        }
        break;
      case ASTRING:
        if (field.type == TType.STRING) {
          this.aString = iprot.readString();
        } else {
          TProtocolUtil.skip(iprot, field.type);
        }
        break;
      case LINT:
        if (field.type == TType.LIST) {
          {
            TList _list0 = iprot.readListBegin();
            this.lint = new ArrayList<Integer>(_list0.size);
            for (int _i1 = 0; _i1 < _list0.size; ++_i1) {
              int _elem2;
              _elem2 = iprot.readI32();
              this.lint.add(_elem2);
            }
            iprot.readListEnd();
          }
        } else {
          TProtocolUtil.skip(iprot, field.type);
        }
        break;
      case LSTRING:
        if (field.type == TType.LIST) {
          {
            TList _list3 = iprot.readListBegin();
            this.lString = new ArrayList<String>(_list3.size);
            for (int _i4 = 0; _i4 < _list3.size; ++_i4) {
              String _elem5;
              _elem5 = iprot.readString();
              this.lString.add(_elem5);
            }
            iprot.readListEnd();
          }
        } else {
          TProtocolUtil.skip(iprot, field.type);
        }
        break;
      case LINTSTRING:
        if (field.type == TType.LIST) {
          {
            TList _list6 = iprot.readListBegin();
            this.lintString = new ArrayList<IntString>(_list6.size);
            for (int _i7 = 0; _i7 < _list6.size; ++_i7) {
              IntString _elem8;
              _elem8 = new IntString();
              _elem8.read(iprot);
              this.lintString.add(_elem8);
            }
            iprot.readListEnd();
          }
        } else {
          TProtocolUtil.skip(iprot, field.type);
        }
        break;
      case MSTRINGSTRING:
        if (field.type == TType.MAP) {
          {
            TMap _map9 = iprot.readMapBegin();
            this.mStringString = new HashMap<String, String>(2 * _map9.size);
            for (int _i10 = 0; _i10 < _map9.size; ++_i10) {
              String _key11;
              String _val12;
              _key11 = iprot.readString();
              _val12 = iprot.readString();
              this.mStringString.put(_key11, _val12);
            }
            iprot.readMapEnd();
          }
        } else {
          TProtocolUtil.skip(iprot, field.type);
        }
        break;
      default:
        TProtocolUtil.skip(iprot, field.type);
        break;
      }
      iprot.readFieldEnd();
    }
    iprot.readStructEnd();

    validate();
  }

  public void write(TProtocol oprot) throws TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    oprot.writeFieldBegin(AINT_FIELD_DESC);
    oprot.writeI32(this.aint);
    oprot.writeFieldEnd();
    if (this.aString != null) {
      oprot.writeFieldBegin(A_STRING_FIELD_DESC);
      oprot.writeString(this.aString);
      oprot.writeFieldEnd();
    }
    if (this.lint != null) {
      oprot.writeFieldBegin(LINT_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.I32, this.lint.size()));
        for (int _iter13 : this.lint) {
          oprot.writeI32(_iter13);
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    if (this.lString != null) {
      oprot.writeFieldBegin(L_STRING_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRING, this.lString.size()));
        for (String _iter14 : this.lString) {
          oprot.writeString(_iter14);
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    if (this.lintString != null) {
      oprot.writeFieldBegin(LINT_STRING_FIELD_DESC);
      {
        oprot.writeListBegin(new TList(TType.STRUCT, this.lintString.size()));
        for (IntString _iter15 : this.lintString) {
          _iter15.write(oprot);
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    if (this.mStringString != null) {
      oprot.writeFieldBegin(M_STRING_STRING_FIELD_DESC);
      {
        oprot.writeMapBegin(new TMap(TType.STRING, TType.STRING,
            this.mStringString.size()));
        for (Map.Entry<String, String> _iter16 : this.mStringString.entrySet()) {
          oprot.writeString(_iter16.getKey());
          oprot.writeString(_iter16.getValue());
        }
        oprot.writeMapEnd();
      }
      oprot.writeFieldEnd();
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Complex(");
    boolean first = true;

    sb.append("aint:");
    sb.append(this.aint);
    first = false;
    if (!first)
      sb.append(", ");
    sb.append("aString:");
    if (this.aString == null) {
      sb.append("null");
    } else {
      sb.append(this.aString);
    }
    first = false;
    if (!first)
      sb.append(", ");
    sb.append("lint:");
    if (this.lint == null) {
      sb.append("null");
    } else {
      sb.append(this.lint);
    }
    first = false;
    if (!first)
      sb.append(", ");
    sb.append("lString:");
    if (this.lString == null) {
      sb.append("null");
    } else {
      sb.append(this.lString);
    }
    first = false;
    if (!first)
      sb.append(", ");
    sb.append("lintString:");
    if (this.lintString == null) {
      sb.append("null");
    } else {
      sb.append(this.lintString);
    }
    first = false;
    if (!first)
      sb.append(", ");
    sb.append("mStringString:");
    if (this.mStringString == null) {
      sb.append("null");
    } else {
      sb.append(this.mStringString);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
  }

  @Override
  public int compareTo(Object arg0) {
    return 0;
  }

  @Override
  public TFieldIdEnum fieldForId(int fieldId) {
    return null;
  }

  @Override
  public boolean isSet(TFieldIdEnum field) {
    return false;
  }

  @Override
  public Object getFieldValue(TFieldIdEnum field) {
    return null;
  }

  @Override
  public void setFieldValue(TFieldIdEnum field, Object value) {

  }

  @Override
  public TBase deepCopy() {
    return null;
  }

  @Override
  public void clear() {

  }

}
