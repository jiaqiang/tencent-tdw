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

public class HiveClusterStatus implements
    org.apache.thrift.TBase<HiveClusterStatus, HiveClusterStatus._Fields>,
    java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
      "HiveClusterStatus");

  private static final org.apache.thrift.protocol.TField TASK_TRACKERS_FIELD_DESC = new org.apache.thrift.protocol.TField(
      "taskTrackers", org.apache.thrift.protocol.TType.I32, (short) 1);
  private static final org.apache.thrift.protocol.TField MAP_TASKS_FIELD_DESC = new org.apache.thrift.protocol.TField(
      "mapTasks", org.apache.thrift.protocol.TType.I32, (short) 2);
  private static final org.apache.thrift.protocol.TField REDUCE_TASKS_FIELD_DESC = new org.apache.thrift.protocol.TField(
      "reduceTasks", org.apache.thrift.protocol.TType.I32, (short) 3);
  private static final org.apache.thrift.protocol.TField MAX_MAP_TASKS_FIELD_DESC = new org.apache.thrift.protocol.TField(
      "maxMapTasks", org.apache.thrift.protocol.TType.I32, (short) 4);
  private static final org.apache.thrift.protocol.TField MAX_REDUCE_TASKS_FIELD_DESC = new org.apache.thrift.protocol.TField(
      "maxReduceTasks", org.apache.thrift.protocol.TType.I32, (short) 5);
  private static final org.apache.thrift.protocol.TField STATE_FIELD_DESC = new org.apache.thrift.protocol.TField(
      "state", org.apache.thrift.protocol.TType.I32, (short) 6);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class,
        new HiveClusterStatusStandardSchemeFactory());
    schemes.put(TupleScheme.class, new HiveClusterStatusTupleSchemeFactory());
  }

  private int taskTrackers;
  private int mapTasks;
  private int reduceTasks;
  private int maxMapTasks;
  private int maxReduceTasks;
  private JobTrackerState state;

  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    TASK_TRACKERS((short) 1, "taskTrackers"), MAP_TASKS((short) 2, "mapTasks"), REDUCE_TASKS(
        (short) 3, "reduceTasks"), MAX_MAP_TASKS((short) 4, "maxMapTasks"), MAX_REDUCE_TASKS(
        (short) 5, "maxReduceTasks"),

    STATE((short) 6, "state");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    public static _Fields findByThriftId(int fieldId) {
      switch (fieldId) {
      case 1:
        return TASK_TRACKERS;
      case 2:
        return MAP_TASKS;
      case 3:
        return REDUCE_TASKS;
      case 4:
        return MAX_MAP_TASKS;
      case 5:
        return MAX_REDUCE_TASKS;
      case 6:
        return STATE;
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

  private static final int __TASKTRACKERS_ISSET_ID = 0;
  private static final int __MAPTASKS_ISSET_ID = 1;
  private static final int __REDUCETASKS_ISSET_ID = 2;
  private static final int __MAXMAPTASKS_ISSET_ID = 3;
  private static final int __MAXREDUCETASKS_ISSET_ID = 4;
  private BitSet __isset_bit_vector = new BitSet(5);
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
        _Fields.class);
    tmpMap.put(_Fields.TASK_TRACKERS,
        new org.apache.thrift.meta_data.FieldMetaData("taskTrackers",
            org.apache.thrift.TFieldRequirementType.DEFAULT,
            new org.apache.thrift.meta_data.FieldValueMetaData(
                org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.MAP_TASKS,
        new org.apache.thrift.meta_data.FieldMetaData("mapTasks",
            org.apache.thrift.TFieldRequirementType.DEFAULT,
            new org.apache.thrift.meta_data.FieldValueMetaData(
                org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.REDUCE_TASKS,
        new org.apache.thrift.meta_data.FieldMetaData("reduceTasks",
            org.apache.thrift.TFieldRequirementType.DEFAULT,
            new org.apache.thrift.meta_data.FieldValueMetaData(
                org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.MAX_MAP_TASKS,
        new org.apache.thrift.meta_data.FieldMetaData("maxMapTasks",
            org.apache.thrift.TFieldRequirementType.DEFAULT,
            new org.apache.thrift.meta_data.FieldValueMetaData(
                org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.MAX_REDUCE_TASKS,
        new org.apache.thrift.meta_data.FieldMetaData("maxReduceTasks",
            org.apache.thrift.TFieldRequirementType.DEFAULT,
            new org.apache.thrift.meta_data.FieldValueMetaData(
                org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.STATE, new org.apache.thrift.meta_data.FieldMetaData(
        "state", org.apache.thrift.TFieldRequirementType.DEFAULT,
        new org.apache.thrift.meta_data.EnumMetaData(
            org.apache.thrift.protocol.TType.ENUM, JobTrackerState.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
        HiveClusterStatus.class, metaDataMap);
  }

  public HiveClusterStatus() {
  }

  public HiveClusterStatus(int taskTrackers, int mapTasks, int reduceTasks,
      int maxMapTasks, int maxReduceTasks, JobTrackerState state) {
    this();
    this.taskTrackers = taskTrackers;
    setTaskTrackersIsSet(true);
    this.mapTasks = mapTasks;
    setMapTasksIsSet(true);
    this.reduceTasks = reduceTasks;
    setReduceTasksIsSet(true);
    this.maxMapTasks = maxMapTasks;
    setMaxMapTasksIsSet(true);
    this.maxReduceTasks = maxReduceTasks;
    setMaxReduceTasksIsSet(true);
    this.state = state;
  }

  public HiveClusterStatus(HiveClusterStatus other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.taskTrackers = other.taskTrackers;
    this.mapTasks = other.mapTasks;
    this.reduceTasks = other.reduceTasks;
    this.maxMapTasks = other.maxMapTasks;
    this.maxReduceTasks = other.maxReduceTasks;
    if (other.isSetState()) {
      this.state = other.state;
    }
  }

  public HiveClusterStatus deepCopy() {
    return new HiveClusterStatus(this);
  }

  @Override
  public void clear() {
    setTaskTrackersIsSet(false);
    this.taskTrackers = 0;
    setMapTasksIsSet(false);
    this.mapTasks = 0;
    setReduceTasksIsSet(false);
    this.reduceTasks = 0;
    setMaxMapTasksIsSet(false);
    this.maxMapTasks = 0;
    setMaxReduceTasksIsSet(false);
    this.maxReduceTasks = 0;
    this.state = null;
  }

  public int getTaskTrackers() {
    return this.taskTrackers;
  }

  public void setTaskTrackers(int taskTrackers) {
    this.taskTrackers = taskTrackers;
    setTaskTrackersIsSet(true);
  }

  public void unsetTaskTrackers() {
    __isset_bit_vector.clear(__TASKTRACKERS_ISSET_ID);
  }

  public boolean isSetTaskTrackers() {
    return __isset_bit_vector.get(__TASKTRACKERS_ISSET_ID);
  }

  public void setTaskTrackersIsSet(boolean value) {
    __isset_bit_vector.set(__TASKTRACKERS_ISSET_ID, value);
  }

  public int getMapTasks() {
    return this.mapTasks;
  }

  public void setMapTasks(int mapTasks) {
    this.mapTasks = mapTasks;
    setMapTasksIsSet(true);
  }

  public void unsetMapTasks() {
    __isset_bit_vector.clear(__MAPTASKS_ISSET_ID);
  }

  public boolean isSetMapTasks() {
    return __isset_bit_vector.get(__MAPTASKS_ISSET_ID);
  }

  public void setMapTasksIsSet(boolean value) {
    __isset_bit_vector.set(__MAPTASKS_ISSET_ID, value);
  }

  public int getReduceTasks() {
    return this.reduceTasks;
  }

  public void setReduceTasks(int reduceTasks) {
    this.reduceTasks = reduceTasks;
    setReduceTasksIsSet(true);
  }

  public void unsetReduceTasks() {
    __isset_bit_vector.clear(__REDUCETASKS_ISSET_ID);
  }

  public boolean isSetReduceTasks() {
    return __isset_bit_vector.get(__REDUCETASKS_ISSET_ID);
  }

  public void setReduceTasksIsSet(boolean value) {
    __isset_bit_vector.set(__REDUCETASKS_ISSET_ID, value);
  }

  public int getMaxMapTasks() {
    return this.maxMapTasks;
  }

  public void setMaxMapTasks(int maxMapTasks) {
    this.maxMapTasks = maxMapTasks;
    setMaxMapTasksIsSet(true);
  }

  public void unsetMaxMapTasks() {
    __isset_bit_vector.clear(__MAXMAPTASKS_ISSET_ID);
  }

  public boolean isSetMaxMapTasks() {
    return __isset_bit_vector.get(__MAXMAPTASKS_ISSET_ID);
  }

  public void setMaxMapTasksIsSet(boolean value) {
    __isset_bit_vector.set(__MAXMAPTASKS_ISSET_ID, value);
  }

  public int getMaxReduceTasks() {
    return this.maxReduceTasks;
  }

  public void setMaxReduceTasks(int maxReduceTasks) {
    this.maxReduceTasks = maxReduceTasks;
    setMaxReduceTasksIsSet(true);
  }

  public void unsetMaxReduceTasks() {
    __isset_bit_vector.clear(__MAXREDUCETASKS_ISSET_ID);
  }

  public boolean isSetMaxReduceTasks() {
    return __isset_bit_vector.get(__MAXREDUCETASKS_ISSET_ID);
  }

  public void setMaxReduceTasksIsSet(boolean value) {
    __isset_bit_vector.set(__MAXREDUCETASKS_ISSET_ID, value);
  }

  public JobTrackerState getState() {
    return this.state;
  }

  public void setState(JobTrackerState state) {
    this.state = state;
  }

  public void unsetState() {
    this.state = null;
  }

  public boolean isSetState() {
    return this.state != null;
  }

  public void setStateIsSet(boolean value) {
    if (!value) {
      this.state = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case TASK_TRACKERS:
      if (value == null) {
        unsetTaskTrackers();
      } else {
        setTaskTrackers((Integer) value);
      }
      break;

    case MAP_TASKS:
      if (value == null) {
        unsetMapTasks();
      } else {
        setMapTasks((Integer) value);
      }
      break;

    case REDUCE_TASKS:
      if (value == null) {
        unsetReduceTasks();
      } else {
        setReduceTasks((Integer) value);
      }
      break;

    case MAX_MAP_TASKS:
      if (value == null) {
        unsetMaxMapTasks();
      } else {
        setMaxMapTasks((Integer) value);
      }
      break;

    case MAX_REDUCE_TASKS:
      if (value == null) {
        unsetMaxReduceTasks();
      } else {
        setMaxReduceTasks((Integer) value);
      }
      break;

    case STATE:
      if (value == null) {
        unsetState();
      } else {
        setState((JobTrackerState) value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case TASK_TRACKERS:
      return Integer.valueOf(getTaskTrackers());

    case MAP_TASKS:
      return Integer.valueOf(getMapTasks());

    case REDUCE_TASKS:
      return Integer.valueOf(getReduceTasks());

    case MAX_MAP_TASKS:
      return Integer.valueOf(getMaxMapTasks());

    case MAX_REDUCE_TASKS:
      return Integer.valueOf(getMaxReduceTasks());

    case STATE:
      return getState();

    }
    throw new IllegalStateException();
  }

  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case TASK_TRACKERS:
      return isSetTaskTrackers();
    case MAP_TASKS:
      return isSetMapTasks();
    case REDUCE_TASKS:
      return isSetReduceTasks();
    case MAX_MAP_TASKS:
      return isSetMaxMapTasks();
    case MAX_REDUCE_TASKS:
      return isSetMaxReduceTasks();
    case STATE:
      return isSetState();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof HiveClusterStatus)
      return this.equals((HiveClusterStatus) that);
    return false;
  }

  public boolean equals(HiveClusterStatus that) {
    if (that == null)
      return false;

    boolean this_present_taskTrackers = true;
    boolean that_present_taskTrackers = true;
    if (this_present_taskTrackers || that_present_taskTrackers) {
      if (!(this_present_taskTrackers && that_present_taskTrackers))
        return false;
      if (this.taskTrackers != that.taskTrackers)
        return false;
    }

    boolean this_present_mapTasks = true;
    boolean that_present_mapTasks = true;
    if (this_present_mapTasks || that_present_mapTasks) {
      if (!(this_present_mapTasks && that_present_mapTasks))
        return false;
      if (this.mapTasks != that.mapTasks)
        return false;
    }

    boolean this_present_reduceTasks = true;
    boolean that_present_reduceTasks = true;
    if (this_present_reduceTasks || that_present_reduceTasks) {
      if (!(this_present_reduceTasks && that_present_reduceTasks))
        return false;
      if (this.reduceTasks != that.reduceTasks)
        return false;
    }

    boolean this_present_maxMapTasks = true;
    boolean that_present_maxMapTasks = true;
    if (this_present_maxMapTasks || that_present_maxMapTasks) {
      if (!(this_present_maxMapTasks && that_present_maxMapTasks))
        return false;
      if (this.maxMapTasks != that.maxMapTasks)
        return false;
    }

    boolean this_present_maxReduceTasks = true;
    boolean that_present_maxReduceTasks = true;
    if (this_present_maxReduceTasks || that_present_maxReduceTasks) {
      if (!(this_present_maxReduceTasks && that_present_maxReduceTasks))
        return false;
      if (this.maxReduceTasks != that.maxReduceTasks)
        return false;
    }

    boolean this_present_state = true && this.isSetState();
    boolean that_present_state = true && that.isSetState();
    if (this_present_state || that_present_state) {
      if (!(this_present_state && that_present_state))
        return false;
      if (!this.state.equals(that.state))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(HiveClusterStatus other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    HiveClusterStatus typedOther = (HiveClusterStatus) other;

    lastComparison = Boolean.valueOf(isSetTaskTrackers()).compareTo(
        typedOther.isSetTaskTrackers());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTaskTrackers()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(
          this.taskTrackers, typedOther.taskTrackers);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMapTasks()).compareTo(
        typedOther.isSetMapTasks());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMapTasks()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.mapTasks,
          typedOther.mapTasks);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetReduceTasks()).compareTo(
        typedOther.isSetReduceTasks());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetReduceTasks()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(
          this.reduceTasks, typedOther.reduceTasks);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMaxMapTasks()).compareTo(
        typedOther.isSetMaxMapTasks());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMaxMapTasks()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(
          this.maxMapTasks, typedOther.maxMapTasks);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMaxReduceTasks()).compareTo(
        typedOther.isSetMaxReduceTasks());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMaxReduceTasks()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(
          this.maxReduceTasks, typedOther.maxReduceTasks);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetState()).compareTo(
        typedOther.isSetState());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetState()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.state,
          typedOther.state);
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
    StringBuilder sb = new StringBuilder("HiveClusterStatus(");
    boolean first = true;

    sb.append("taskTrackers:");
    sb.append(this.taskTrackers);
    first = false;
    if (!first)
      sb.append(", ");
    sb.append("mapTasks:");
    sb.append(this.mapTasks);
    first = false;
    if (!first)
      sb.append(", ");
    sb.append("reduceTasks:");
    sb.append(this.reduceTasks);
    first = false;
    if (!first)
      sb.append(", ");
    sb.append("maxMapTasks:");
    sb.append(this.maxMapTasks);
    first = false;
    if (!first)
      sb.append(", ");
    sb.append("maxReduceTasks:");
    sb.append(this.maxReduceTasks);
    first = false;
    if (!first)
      sb.append(", ");
    sb.append("state:");
    if (this.state == null) {
      sb.append("null");
    } else {
      sb.append(this.state);
    }
    first = false;
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

  private static class HiveClusterStatusStandardSchemeFactory implements
      SchemeFactory {
    public HiveClusterStatusStandardScheme getScheme() {
      return new HiveClusterStatusStandardScheme();
    }
  }

  private static class HiveClusterStatusStandardScheme extends
      StandardScheme<HiveClusterStatus> {

    public void read(org.apache.thrift.protocol.TProtocol iprot,
        HiveClusterStatus struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true) {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
          break;
        }
        switch (schemeField.id) {
        case 1:
          if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
            struct.taskTrackers = iprot.readI32();
            struct.setTaskTrackersIsSet(true);
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
                schemeField.type);
          }
          break;
        case 2:
          if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
            struct.mapTasks = iprot.readI32();
            struct.setMapTasksIsSet(true);
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
                schemeField.type);
          }
          break;
        case 3:
          if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
            struct.reduceTasks = iprot.readI32();
            struct.setReduceTasksIsSet(true);
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
                schemeField.type);
          }
          break;
        case 4:
          if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
            struct.maxMapTasks = iprot.readI32();
            struct.setMaxMapTasksIsSet(true);
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
                schemeField.type);
          }
          break;
        case 5:
          if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
            struct.maxReduceTasks = iprot.readI32();
            struct.setMaxReduceTasksIsSet(true);
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
                schemeField.type);
          }
          break;
        case 6:
          if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
            struct.state = JobTrackerState.findByValue(iprot.readI32());
            struct.setStateIsSet(true);
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
        HiveClusterStatus struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(TASK_TRACKERS_FIELD_DESC);
      oprot.writeI32(struct.taskTrackers);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(MAP_TASKS_FIELD_DESC);
      oprot.writeI32(struct.mapTasks);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(REDUCE_TASKS_FIELD_DESC);
      oprot.writeI32(struct.reduceTasks);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(MAX_MAP_TASKS_FIELD_DESC);
      oprot.writeI32(struct.maxMapTasks);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(MAX_REDUCE_TASKS_FIELD_DESC);
      oprot.writeI32(struct.maxReduceTasks);
      oprot.writeFieldEnd();
      if (struct.state != null) {
        oprot.writeFieldBegin(STATE_FIELD_DESC);
        oprot.writeI32(struct.state.getValue());
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class HiveClusterStatusTupleSchemeFactory implements
      SchemeFactory {
    public HiveClusterStatusTupleScheme getScheme() {
      return new HiveClusterStatusTupleScheme();
    }
  }

  private static class HiveClusterStatusTupleScheme extends
      TupleScheme<HiveClusterStatus> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot,
        HiveClusterStatus struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetTaskTrackers()) {
        optionals.set(0);
      }
      if (struct.isSetMapTasks()) {
        optionals.set(1);
      }
      if (struct.isSetReduceTasks()) {
        optionals.set(2);
      }
      if (struct.isSetMaxMapTasks()) {
        optionals.set(3);
      }
      if (struct.isSetMaxReduceTasks()) {
        optionals.set(4);
      }
      if (struct.isSetState()) {
        optionals.set(5);
      }
      oprot.writeBitSet(optionals, 6);
      if (struct.isSetTaskTrackers()) {
        oprot.writeI32(struct.taskTrackers);
      }
      if (struct.isSetMapTasks()) {
        oprot.writeI32(struct.mapTasks);
      }
      if (struct.isSetReduceTasks()) {
        oprot.writeI32(struct.reduceTasks);
      }
      if (struct.isSetMaxMapTasks()) {
        oprot.writeI32(struct.maxMapTasks);
      }
      if (struct.isSetMaxReduceTasks()) {
        oprot.writeI32(struct.maxReduceTasks);
      }
      if (struct.isSetState()) {
        oprot.writeI32(struct.state.getValue());
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot,
        HiveClusterStatus struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(6);
      if (incoming.get(0)) {
        struct.taskTrackers = iprot.readI32();
        struct.setTaskTrackersIsSet(true);
      }
      if (incoming.get(1)) {
        struct.mapTasks = iprot.readI32();
        struct.setMapTasksIsSet(true);
      }
      if (incoming.get(2)) {
        struct.reduceTasks = iprot.readI32();
        struct.setReduceTasksIsSet(true);
      }
      if (incoming.get(3)) {
        struct.maxMapTasks = iprot.readI32();
        struct.setMaxMapTasksIsSet(true);
      }
      if (incoming.get(4)) {
        struct.maxReduceTasks = iprot.readI32();
        struct.setMaxReduceTasksIsSet(true);
      }
      if (incoming.get(5)) {
        struct.state = JobTrackerState.findByValue(iprot.readI32());
        struct.setStateIsSet(true);
      }
    }
  }

}
