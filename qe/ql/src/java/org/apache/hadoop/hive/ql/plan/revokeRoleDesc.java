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

public class revokeRoleDesc extends aclDesc implements Serializable {

  private static final long serialVersionUID = 1L;
  ArrayList<String> roles;
  ArrayList<String> users;

  public revokeRoleDesc(ArrayList<String> roles, ArrayList<String> users,
      String who, String DBconnected) {
    super();
    this.roles = roles;
    this.users = users;
    this.setWho(who);
    this.setDBconnected(DBconnected);
  }

  public ArrayList<String> getRoles() {
    return roles;
  }

  public void setRoles(ArrayList<String> roles) {
    this.roles = roles;
  }

  public ArrayList<String> getUsers() {
    return users;
  }

  public void setUsers(ArrayList<String> users) {
    this.users = users;
  }

}
