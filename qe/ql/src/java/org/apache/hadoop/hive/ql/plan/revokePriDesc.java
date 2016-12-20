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
import java.util.ArrayList;

public class revokePriDesc extends aclDesc implements Serializable {
  String user;
  ArrayList<String> pris;
  String db;
  String table;

  public revokePriDesc(String user, ArrayList<String> pris, String db,
      String table, String who, String DBconnected) {
    super();
    this.user = user;
    this.pris = pris;
    this.db = db;
    this.table = table;
    this.setWho(who);
    this.setDBconnected(DBconnected);
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public ArrayList<String> getPris() {
    return pris;
  }

  public void setPris(ArrayList<String> pris) {
    this.pris = pris;
  }

  public String getDb() {
    return db;
  }

  public void setDb(String db) {
    this.db = db;
  }

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

}
