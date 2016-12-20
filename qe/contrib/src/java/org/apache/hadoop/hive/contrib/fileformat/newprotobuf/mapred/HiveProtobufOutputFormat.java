/**
* Tencent is pleased to support the open source community by making TDW available.
* Copyright (C) 2014 THL A29 Limited, a Tencent company. All rights reserved.
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use 
* this file except in compliance with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software distributed 
* under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS 
* OF ANY KIND, either express or implied. See the License for the specific language governing
* permissions and limitations under the License.
*/

package newprotobuf.mapred;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.hive.ql.exec.FileSinkOperator.RecordWriter;
import org.apache.hadoop.hive.ql.io.HiveOutputFormat;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.Progressable;


public class HiveProtobufOutputFormat<K extends WritableComparable, V extends Writable>
	implements HiveOutputFormat<K, V> {

	protected static class ProtobufRecordWriter implements RecordWriter {

        protected DataOutputStream out;

		public ProtobufRecordWriter(DataOutputStream out) {
		    this.out = out;
		}
		
		private static void writeLittleEndianInt(DataOutputStream out, int data) throws IOException{
			byte b1 = (byte)((data >> 24) & 0xFF);
			byte b2 = (byte)((data >> 16) & 0xFF);
			byte b3 = (byte)((data >> 8) & 0xFF);
			byte b4 = (byte)((data) & 0xFF);
			
			out.writeByte(b4);
			out.writeByte(b3);
			out.writeByte(b2);
			out.writeByte(b1);
		}
		
		
		private void writeProtobufObject(BytesWritable value) throws IOException {
			writeLittleEndianInt(out, value.getLength());
			out.write(value.getBytes(),0,value.getLength());
		}

		public synchronized void write(Writable value) throws IOException {
			if (value instanceof BytesWritable) {
				writeProtobufObject((BytesWritable) value);
			}
		}

		public synchronized void close(boolean abort) throws IOException {
			out.flush();
			out.close();
		}

	}

	

	public RecordWriter getHiveRecordWriter(JobConf jc, Path outPath,
			Class<? extends Writable> valueClass, boolean isCompressed,
			Properties tableProperties, Progressable progress)
			throws IOException {

		FileSystem fs = outPath.getFileSystem(jc);
		final FSDataOutputStream fileOut = fs.create(outPath);

		return new ProtobufRecordWriter(fileOut);
	}

}

