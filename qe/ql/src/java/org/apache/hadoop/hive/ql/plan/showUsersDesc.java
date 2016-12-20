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
package org.apache.hadoop.hive.ql.plan;

import java.io.Serializable;

import org.apache.hadoop.fs.Path;

public class showUsersDesc extends aclDesc implements Serializable {

  private static final long serialVersionUID = 1L;
  Path tmpFile;

  private final String schema = "user_name,group_name#string,string";

  public showUsersDesc(Path tmp, String who, String DBconnected) {
    super();
    this.tmpFile = tmp;
    this.setWho(who);
    this.setDBconnected(DBconnected);
  }

  public String getSchema() {
    return schema;
  }

  public Path getTmpFile() {
    return tmpFile;
  }

  public void setTmpFile(Path tmpFile) {
    this.tmpFile = tmpFile;
  }

}
