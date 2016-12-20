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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.Compressor;
import org.apache.hadoop.io.compress.Decompressor;

public class CodecPool {
  private static final Log LOG = LogFactory.getLog(CodecPool.class);

  private static final Map<Class<Compressor>, List<Compressor>> compressorPool = new HashMap<Class<Compressor>, List<Compressor>>();

  private static final Map<Class<Decompressor>, List<Decompressor>> decompressorPool = new HashMap<Class<Decompressor>, List<Decompressor>>();

  private static <T> T borrow(Map<Class<T>, List<T>> pool,
      Class<? extends T> codecClass) {
    T codec = null;

    synchronized (pool) {
      if (pool.containsKey(codecClass)) {
        List<T> codecList = pool.get(codecClass);

        if (codecList != null) {
          synchronized (codecList) {
            if (!codecList.isEmpty()) {
              codec = codecList.remove(codecList.size() - 1);
            }
          }
        }
      }
    }

    return codec;
  }

  private static <T> void payback(Map<Class<T>, List<T>> pool, T codec) {
    if (codec != null) {
      Class<T> codecClass = (Class<T>) codec.getClass();
      synchronized (pool) {
        if (!pool.containsKey(codecClass)) {
          pool.put(codecClass, new ArrayList<T>());
        }

        List<T> codecList = pool.get(codecClass);
        synchronized (codecList) {
          codecList.add(codec);
        }
      }
    }
  }

  public static Compressor getCompressor(CompressionCodec codec) {
    Compressor compressor = (Compressor) borrow(compressorPool,
        codec.getCompressorType());
    if (compressor == null) {
      compressor = codec.createCompressor();
      LOG.info("Got brand-new compressor");
    } else {
      LOG.debug("Got recycled compressor");
    }
    return compressor;
  }

  public static Decompressor getDecompressor(CompressionCodec codec) {
    Decompressor decompressor = (Decompressor) borrow(decompressorPool,
        codec.getDecompressorType());
    if (decompressor == null) {
      decompressor = codec.createDecompressor();
      LOG.info("Got brand-new decompressor");
    } else {
      LOG.debug("Got recycled decompressor");
    }
    return decompressor;
  }

  public static void returnCompressor(Compressor compressor) {
    if (compressor == null) {
      return;
    }
    compressor.reset();
    payback(compressorPool, compressor);
  }

  public static void returnDecompressor(Decompressor decompressor) {
    if (decompressor == null) {
      return;
    }
    decompressor.reset();
    payback(decompressorPool, decompressor);
  }
}
