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

package org.apache.hadoop.hive.ql.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.Operator;
import org.apache.hadoop.hive.ql.exec.TableScanOperator;
import org.apache.hadoop.hive.ql.exec.Utilities;
import org.apache.hadoop.hive.ql.plan.mapredWork;
import org.apache.hadoop.hive.ql.plan.tableDesc;
import org.apache.hadoop.hive.ql.plan.partitionDesc;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobConfigurable;
import org.apache.hadoop.mapred.MultiFileSplit;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;

import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.util.ReflectionUtils;

import StorageEngineClient.ColumnStorageInputFormat;
import StorageEngineClient.CombineColumnStorageFileInputFormat;
import StorageEngineClient.CombineFormatStorageFileInputFormat;
import StorageEngineClient.CombineTextFileInputFormat;
import StorageEngineClient.FormatStorageInputFormat;
import StorageEngineClient.FormatStorageInputFormat_SplitByLineNum;
import StorageEngineClient.HashMultiFileColumnStorageInputFormat;
import StorageEngineClient.HashMultiFileFormatStorageInputFormat;
import StorageEngineClient.HashMultiFileTextInputFormat;
import StorageEngineClient.MultiFormatStorageSplit;
import StorageEngineClient.MyTextInputFormat;

public class HiveInputFormat<K extends WritableComparable, V extends Writable>
    implements InputFormat<K, V>, JobConfigurable {

  public static final Log LOG = LogFactory
      .getLog("org.apache.hadoop.hive.ql.io.HiveInputFormat");

  public static class HiveInputSplit extends FileSplit implements InputSplit,
      Configurable {

    InputSplit inputSplit;
    String inputFormatClassName;

    public HiveInputSplit() {
      super((Path) null, 0, 0, (String[]) null);
    }

    public HiveInputSplit(InputSplit inputSplit, String inputFormatClassName) {
      super((Path) null, 0, 0, (String[]) null);
      this.inputSplit = inputSplit;
      this.inputFormatClassName = inputFormatClassName;
    }

    public InputSplit getInputSplit() {
      return inputSplit;
    }

    public String inputFormatClassName() {
      return inputFormatClassName;
    }

    public Path getPath() {
      if (inputSplit instanceof FileSplit) {
        return ((FileSplit) inputSplit).getPath();
      } else if (inputSplit instanceof MultiFileSplit)
        return ((MultiFileSplit) inputSplit).getPath(0);
      else if (inputSplit instanceof StorageEngineClient.CombineFileSplit)
        return ((StorageEngineClient.CombineFileSplit) inputSplit).getPath(0);
      else if (inputSplit instanceof StorageEngineClient.FormatStorageInputFormat_SplitByLineNum.FormatStorageInputSplit_WithLineNum)
        return new Path(
            ((StorageEngineClient.FormatStorageInputFormat_SplitByLineNum.FormatStorageInputSplit_WithLineNum) inputSplit).filesplits[0].path);
      else if (inputSplit instanceof RCFileInputFormatSplitByLineNum.CombineRCFileFileSplit_WithLineNum)
        return ((RCFileInputFormatSplitByLineNum.CombineRCFileFileSplit_WithLineNum) inputSplit).filesplits[0].getPath();
      return new Path("");
    }

    public long getStart() {
      if (inputSplit instanceof FileSplit) {
        return ((FileSplit) inputSplit).getStart();
      }
      return 0;
    }

    public String toString() {
      return inputFormatClassName + ":" + inputSplit.toString();
    }

    public long getLength() {
      long r = 0;
      try {
        r = inputSplit.getLength();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      return r;
    }

    public String[] getLocations() throws IOException {
      return inputSplit.getLocations();
    }

    public void readFields(DataInput in) throws IOException {
      String inputSplitClassName = in.readUTF();
      try {
        inputSplit = (InputSplit) ReflectionUtils.newInstance(
            conf.getClassByName(inputSplitClassName), conf);
      } catch (Exception e) {
        throw new IOException(
            "Cannot create an instance of InputSplit class = "
                + inputSplitClassName + ":" + e.getMessage());
      }
      inputSplit.readFields(in);
      inputFormatClassName = in.readUTF();
    }

    public void write(DataOutput out) throws IOException {
      out.writeUTF(inputSplit.getClass().getName());
      inputSplit.write(out);
      out.writeUTF(inputFormatClassName);
    }

    Configuration conf;

    @Override
    public Configuration getConf() {
      return conf;
    }

    @Override
    public void setConf(Configuration conf) {
      this.conf = conf;
    }
  }

  JobConf job;

  public void configure(JobConf job) {
    this.job = job;
  }

  private static Map<Class, InputFormat<WritableComparable, Writable>> inputFormats;

  static InputFormat<WritableComparable, Writable> getInputFormatFromCache(
      Class inputFormatClass, JobConf job) throws IOException {
    if (inputFormats == null) {
      inputFormats = new HashMap<Class, InputFormat<WritableComparable, Writable>>();
    }
    if (!inputFormats.containsKey(inputFormatClass)) {
      try {
        InputFormat<WritableComparable, Writable> newInstance = (InputFormat<WritableComparable, Writable>) ReflectionUtils
            .newInstance(inputFormatClass, job);
        inputFormats.put(inputFormatClass, newInstance);
      } catch (Exception e) {
        throw new IOException("Cannot create an instance of InputFormat class "
            + inputFormatClass.getName() + " as specified in mapredWork!");
      }
    }
    return inputFormats.get(inputFormatClass);
  }

  public RecordReader getRecordReader(InputSplit split, JobConf job,
      Reporter reporter) throws IOException {

    HiveInputSplit hsplit = (HiveInputSplit) split;

    InputSplit inputSplit = hsplit.getInputSplit();
    String inputFormatClassName = null;
    Class inputFormatClass = null;
    try {
      inputFormatClassName = hsplit.inputFormatClassName();
      inputFormatClass = job.getClassByName(inputFormatClassName);
    } catch (Exception e) {
      throw new IOException("cannot find class " + inputFormatClassName);
    }

    InputFormat inputFormat = getInputFormatFromCache(inputFormatClass, job);

    if (this.mrwork == null)
      init(job);
    JobConf jobConf = new JobConf(job);
    ArrayList<String> aliases = new ArrayList<String>();
    Iterator<Entry<String, ArrayList<String>>> iterator = this.mrwork
        .getPathToAliases().entrySet().iterator();
    String splitPath = hsplit.getPath().toString();
    String splitPathWithNoSchema = hsplit.getPath().toUri().getPath();
    while (iterator.hasNext()) {
      Entry<String, ArrayList<String>> entry = iterator.next();
      String key = entry.getKey();
      if (splitPath.startsWith(key) || splitPathWithNoSchema.startsWith(key)) {
        ArrayList<String> list = entry.getValue();
        for (String val : list)
          aliases.add(val);
      }
    }
    for (String alias : aliases) {
      Operator<? extends Serializable> op = this.mrwork.getAliasToWork().get(
          alias);
      if (op instanceof TableScanOperator) {
        TableScanOperator tableScan = (TableScanOperator) op;
        ArrayList<Integer> list = tableScan.getNeededColumnIDs();
        if (list != null)
          HiveFileFormatUtils.appendReadColumnIDs(jobConf, list);
        else
          HiveFileFormatUtils.setFullyReadColumns(jobConf);
      }
    }
    return new HiveRecordReader(inputFormat.getRecordReader(inputSplit,
        jobConf, reporter));
  }

  private Map<String, partitionDesc> pathToPartitionInfo;
  mapredWork mrwork = null;
  boolean isHashMapJoin = false;

  protected void init(JobConf job) {
    mrwork = Utilities.getMapRedWork(job);
    pathToPartitionInfo = mrwork.getPathToPartitionInfo();

    try {
      if (mrwork.getMapLocalWork().getHashMapjoinContext() != null)
        isHashMapJoin = true;
    } catch (Exception e) {

    }
  }

  public InputSplit[] getSplits(JobConf job, int numSplits) throws IOException {

    init(job);

    Path[] dirs = FileInputFormat.getInputPaths(job);
    if (dirs.length == 0) {
      throw new IOException("No input paths specified in job");
    }

    if (isHashMapJoin) {
      LOG.info("This is a hive mapside computing, The iput paths will be splitted for it!");
      LinkedHashMap<String, ArrayList<Path>> hashnameToPaths = new LinkedHashMap<String, ArrayList<Path>>();

      for (Path dir : dirs) {
        ArrayList<Path> paths = null;
        String lastDirName = dir.getName();
        if (!hashnameToPaths.containsKey(lastDirName)) {
          paths = new ArrayList<Path>();
          paths.add(dir);
          hashnameToPaths.put(dir.getName(), paths);
        } else {
          paths = hashnameToPaths.get(lastDirName);
          paths.add(dir);
        }
      }

      JobConf newjob = new JobConf(job);
      ArrayList<InputSplit> result = new ArrayList<InputSplit>();

      for (ArrayList<Path> pathList : hashnameToPaths.values()) {
        tableDesc table = getTableDescFromPath(pathList.get(0));
        Class inputFormatClass = table.getInputFileFormatClass();

        if (inputFormatClass == TextInputFormat.class)
          inputFormatClass = HashMultiFileTextInputFormat.class;
        else if (inputFormatClass == FormatStorageInputFormat.class)
          inputFormatClass = HashMultiFileFormatStorageInputFormat.class;
        else if (inputFormatClass == ColumnStorageInputFormat.class)
          inputFormatClass = HashMultiFileColumnStorageInputFormat.class;

        InputFormat inputFormat = getInputFormatFromCache(inputFormatClass, job);
        String paths = "";
        for (Path path : pathList) {
          paths += path.toString();
          if (pathList.indexOf(path) < pathList.size() - 1)
            paths += ",";
        }
        FileInputFormat.setInputPaths(newjob, paths);
        newjob.setInputFormat(inputFormat.getClass());
        InputSplit[] iss = inputFormat.getSplits(newjob, 1);
        for (InputSplit is : iss) {
          result.add(new HiveInputSplit(is, inputFormatClass.getName()));
        }
      }

      return result.toArray(new HiveInputSplit[result.size()]);
    }

    JobConf newjob = new JobConf(job);
    ArrayList<InputSplit> result = new ArrayList<InputSplit>();

    for (Path dir : dirs) {
      tableDesc table = getTableDescFromPath(dir);
      Class inputFormatClass = table.getInputFileFormatClass();
      if (job.getBoolean("hive.inputfiles.splitbylinenum", false)) {
        if (inputFormatClass == TextInputFormat.class)
          inputFormatClass = CombineTextFileInputFormat.class;
        else if (inputFormatClass == FormatStorageInputFormat.class)
          inputFormatClass = FormatStorageInputFormat_SplitByLineNum.class;
        else if (inputFormatClass == ColumnStorageInputFormat.class)
          inputFormatClass = CombineColumnStorageFileInputFormat.class;
        else if (inputFormatClass == RCFileInputFormat.class)
          inputFormatClass = RCFileInputFormatSplitByLineNum.class;
      } else if (job.getBoolean("hive.merge.inputfiles", true)) {
        if (inputFormatClass == TextInputFormat.class)
          inputFormatClass = CombineTextFileInputFormat.class;
        else if (inputFormatClass == FormatStorageInputFormat.class)
          inputFormatClass = CombineFormatStorageFileInputFormat.class;
        else if (inputFormatClass == ColumnStorageInputFormat.class)
          inputFormatClass = CombineColumnStorageFileInputFormat.class;
        else if (inputFormatClass == RCFileInputFormat.class)
          inputFormatClass = CombineRCFileInputFormat.class;
      } else if (inputFormatClass == TextInputFormat.class) {
        inputFormatClass = MyTextInputFormat.class;
      }
      InputFormat inputFormat = getInputFormatFromCache(inputFormatClass, job);

      FileInputFormat.setInputPaths(newjob, dir);
      newjob.setInputFormat(inputFormat.getClass());
      InputSplit[] iss = inputFormat.getSplits(newjob, numSplits / dirs.length);
      for (InputSplit is : iss) {
        result.add(new HiveInputSplit(is, inputFormatClass.getName()));
      }
    }
    return result.toArray(new HiveInputSplit[result.size()]);
  }

  public void validateInput(JobConf job) throws IOException {

    init(job);

    Path[] dirs = FileInputFormat.getInputPaths(job);
    if (dirs.length == 0) {
      throw new IOException("No input paths specified in job");
    }
    JobConf newjob = new JobConf(job);

    for (Path dir : dirs) {
      tableDesc table = getTableDescFromPath(dir);
      InputFormat inputFormat = getInputFormatFromCache(
          table.getInputFileFormatClass(), job);

      FileInputFormat.setInputPaths(newjob, dir);
      newjob.setInputFormat(inputFormat.getClass());
      ShimLoader.getHadoopShims().inputFormatValidateInput(inputFormat, newjob);
    }
  }

  private tableDesc getTableDescFromPath(Path dir) throws IOException {

    partitionDesc partDesc = pathToPartitionInfo.get(dir.toString());
    if (partDesc == null) {
      partDesc = pathToPartitionInfo.get(dir.toUri().getPath());
    }
    if (partDesc == null) {
      throw new IOException("cannot find dir = " + dir.toString()
          + " in partToPartitionInfo!");
    }

    tableDesc table = partDesc.getTableDesc();
    if (table == null) {
      throw new IOException("Input " + dir.toString()
          + " does not have associated InputFormat in mapredWork!");
    }

    return table;
  }

}
