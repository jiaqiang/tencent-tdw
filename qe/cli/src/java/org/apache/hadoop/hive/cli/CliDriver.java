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

package org.apache.hadoop.hive.cli;

import jline.*;

import java.io.*;
import java.util.*;

import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.MetaStoreUtils;
import org.apache.hadoop.hive.metastore.api.DbPriv;
import org.apache.hadoop.hive.metastore.api.Role;
import org.apache.hadoop.hive.metastore.api.TblPriv;
import org.apache.hadoop.hive.metastore.api.User;
import org.apache.hadoop.hive.ql.exec.Utilities;
import org.apache.hadoop.hive.ql.exec.Utilities.StreamPrinter;
import org.apache.hadoop.hive.ql.io.HiveInputFormat;
import org.apache.hadoop.hive.ql.io.RCFileOutputFormat;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.session.SessionState.LogHelper;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.metadata.Table;
import org.apache.hadoop.hive.ql.metadata.Hive.Privilege;
import org.apache.hadoop.hive.ql.processors.CommandProcessor;
import org.apache.hadoop.hive.ql.processors.CommandProcessorFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.serde2.thrift.ThriftDeserializer;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.apache.hadoop.mapred.lib.db.DBInputFormat;
import org.apache.thrift.TException;

public class CliDriver {

  public final static String prompt = "hive";
  public final static String prompt2 = "    ";

  private LogHelper console;
  private Configuration conf;

  public CliDriver() {
    SessionState ss = SessionState.get();
    conf = (ss != null) ? ss.getConf() : new Configuration();
    Log LOG = LogFactory.getLog("CliDriver");
    console = new LogHelper(LOG);
  }

  public int processCmd(String cmd) throws TException, HiveException {
    SessionState ss = SessionState.get();
    ss.setiscli(true);

    String cmd_trimmed = cmd.trim();
    String[] tokens = cmd_trimmed.split("\\s+");
    String cmd_1 = cmd_trimmed.substring(tokens[0].length()).trim();
    int ret = 0;

    if (tokens[0].equalsIgnoreCase("delete")) {
      Vector<String> nexttoken = new Vector<String>();
      nexttoken.add("jar");
      nexttoken.add("file");
      nexttoken.add("from");
      if (tokens.length < 2 || !nexttoken.contains(tokens[1].toLowerCase())) {
        String errorMessage = "\nif delete resource:\n"
            + "Usage: delete [FILE|JAR] <value> [<value>]*\n"
            + "if delete table rows:\n"
            + "Usage: delete from tableName [where searchCondition]";
        console.printError(errorMessage);
        ret = 1;

        return ret;
      }
    }
    
    if (tokens[0].equalsIgnoreCase("dfs") || tokens[0].equalsIgnoreCase("zktest")) {
      String errorMessage = "\ntdw hive do not support " + tokens[0].toLowerCase() + " operation\n";  
      throw new HiveException(errorMessage);
    }
    
    if (cmd_trimmed.toLowerCase().equals("quit")
        || cmd_trimmed.toLowerCase().equals("exit")) {

      System.exit(0);

    } else if (cmd_trimmed.startsWith("!")) {

      String shell_cmd = cmd_trimmed.substring(1);

      try {
        Process executor = Runtime.getRuntime().exec(shell_cmd);
        StreamPrinter outPrinter = new StreamPrinter(executor.getInputStream(),
            null, ss.out);
        StreamPrinter errPrinter = new StreamPrinter(executor.getErrorStream(),
            null, ss.err);

        outPrinter.start();
        errPrinter.start();

        ret = executor.waitFor();
        if (ret != 0) {
          console.printError("Command failed with exit code = " + ret);
        }
      } catch (Exception e) {
        console.printError(
            "Exception raised from Shell command " + e.getLocalizedMessage(),
            org.apache.hadoop.util.StringUtils.stringifyException(e));
        ret = 1;
      }

    } else if (tokens[0].toLowerCase().equals("list")) {

      SessionState.ResourceType t;
      if (tokens.length < 2
          || (t = SessionState.find_resource_type(tokens[1])) == null) {
        console.printError("Usage: list ["
            + StringUtils.join(SessionState.ResourceType.values(), "|")
            + "] [<value> [<value>]*]");
        ret = 1;
      } else {
        List<String> filter = null;
        if (tokens.length >= 3) {
          System.arraycopy(tokens, 2, tokens, 0, tokens.length - 2);
          filter = Arrays.asList(tokens);
        }
        Set<String> s = ss.list_resource(t, filter);
        if (s != null && !s.isEmpty())
          ss.out.println(StringUtils.join(s, "\n"));
      }

    } else {
      CommandProcessor proc = CommandProcessorFactory.get(tokens);
      if (proc != null) {
        if (proc instanceof Driver) {
          Driver qp = (Driver) proc;
          PrintStream out = ss.out;
          long start = System.currentTimeMillis();

          try {
            ret = qp.run(cmd);
          } catch (Exception e1) {
            e1.printStackTrace();
          }
          if (ret != 0) {
            qp.close();
            return ret;
          }

          Vector<String> res = new Vector<String>();
          try {
            while (qp.getResults(res)) {
              for (String r : res) {
                out.println(r);
              }
              res.clear();
              if (out.checkError()) {
                break;
              }
            }
          } catch (IOException e) {
            console.printError("Failed with exception "
                + e.getClass().getName() + ":" + e.getMessage(), "\n"
                + org.apache.hadoop.util.StringUtils.stringifyException(e));
            ret = 1;
          }

          int cret = qp.close();
          if (ret == 0) {
            ret = cret;
          }

          long end = System.currentTimeMillis();
          if (end > start) {
            double timeTaken = (double) (end - start) / 1000.0;
            console.printInfo("Time taken: " + timeTaken + " seconds", null);
          }

        } else {
          try {
            ret = proc.run(cmd_1);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }

    return ret;
  }

  public int processLine(String line) {
    int lastRet = 0, ret = 0;

    String command = "";
    for (String oneCmd : line.split(";")) {

      if (StringUtils.endsWith(oneCmd, "\\")) {
        command += StringUtils.chop(oneCmd) + ";";
        continue;
      } else {
        command += oneCmd;
      }
      if (StringUtils.isBlank(command))
        continue;

      try {
        ret = processCmd(command);
      } catch (TException e) {
        e.printStackTrace();
      } catch (HiveException e) {
        e.printStackTrace();
      }
      command = "";
      lastRet = ret;
      boolean ignoreErrors = HiveConf.getBoolVar(conf,
          HiveConf.ConfVars.CLIIGNOREERRORS);
      if (ret != 0 && !ignoreErrors) {
        return ret;
      }
    }
    return lastRet;
  }

  public int processReader(BufferedReader r) {
    String line;
    StringBuffer qsb = new StringBuffer();

    try {
      while ((line = r.readLine()) != null) {
        qsb.append(line + "\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      int rrr = (processLine(qsb.toString()));
      return rrr;
    } catch (Exception e) {
      e.printStackTrace();
      return 1;
    }
  }

  public static void main(String[] args) throws Exception {
    OptionsProcessor oproc = new OptionsProcessor();
    if (!oproc.process_stage1(args)) {
      System.exit(1);
    }
    
    //set dns cache to 3s;the negtive cache default value is 10s in java
    java.security.Security.setProperty("networkaddress.cache.ttl" , "3");
    
    SessionState.initHiveLog4j();

    CliSessionState ss = new CliSessionState(new HiveConf(SessionState.class));
    ss.in = System.in;
    try {
      ss.out = new PrintStream(System.out, true, "UTF-8");
      ss.err = new PrintStream(System.err, true, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      System.exit(3);
    }

    if (!oproc.process_stage2(ss)) {
      System.exit(2);
    }

    HiveConf conf = ss.getConf();
    for (Map.Entry<Object, Object> item : ss.cmdProperties.entrySet()) {
      conf.set((String) item.getKey(), (String) item.getValue());
    }

    if (!ShimLoader.getHadoopShims().usesJobShell()) {
      ClassLoader loader = conf.getClassLoader();
      String auxJars = HiveConf.getVar(conf, HiveConf.ConfVars.HIVEAUXJARS);
      if (StringUtils.isNotBlank(auxJars)) {
        loader = Utilities.addToClassPath(loader,
            StringUtils.split(auxJars, ","));
      }
      conf.setClassLoader(loader);
      Thread.currentThread().setContextClassLoader(loader);
    }

    SessionState.start(ss);

    CliDriver cli = new CliDriver();

    Hive hive = Hive.get();

    String username = ss.getUserName();
    String passwd = ss.passwd;
    if (!hive.isAUser(username, passwd)) {
      System.out.println("User or passwd is wrong!");
      System.exit(0);
    } else {
      System.out.println("Connect to TDW successfully!");

    }

    if (ss.getDbName() == null) {
      ss.setDbName(MetaStoreUtils.DEFAULT_DATABASE_NAME);
    }
    if (ss.execString != null) {
      System.exit(cli.processLine(ss.execString));
    }

    try {
      if (ss.fileName != null) {
        System.exit(cli.processReader(new BufferedReader(new FileReader(
            ss.fileName))));
      }
    } catch (FileNotFoundException e) {
      System.err.println("Could not open input file for reading. ("
          + e.getMessage() + ")");
      System.exit(3);
    }

    ConsoleReader reader = new ConsoleReader();
    reader.setBellEnabled(false);

    List<SimpleCompletor> completors = new LinkedList<SimpleCompletor>();
    completors.add(new SimpleCompletor(new String[] { "set", "from", "create",
        "load", "describe", "quit", "exit" }));
    reader.addCompletor(new ArgumentCompletor(completors));

    String line;
    final String HISTORYFILE = ".hivehistory";
    String historyFile = System.getProperty("user.home") + File.separator
        + HISTORYFILE;
    reader.setHistory(new History(new File(historyFile)));
    int ret = 0;

    String prefix = "";
    String curPrompt = prompt;
    while ((line = reader.readLine(curPrompt + "> ")) != null) {
      if (!prefix.equals("")) {
        prefix += '\n';
      }
      if (line.trim().endsWith(";") && !line.trim().endsWith("\\;")) {
        line = prefix + line;
        ret = cli.processLine(line);
        prefix = "";
        curPrompt = prompt;
      } else {
        prefix = prefix + line;
        curPrompt = prompt2;
        continue;
      }
    }

    System.exit(ret);
  }

}
