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

package org.apache.hadoop.hive.ql.parse;

public class ASTNodeOrigin {
  private final String objectType;
  private final String objectName;
  private final String objectDefinition;
  private final String usageAlias;
  private final ASTNode usageNode;

  public ASTNodeOrigin(String objectType, String objectName,
      String objectDefinition, String usageAlias, ASTNode usageNode) {
    this.objectType = objectType;
    this.objectName = objectName;
    this.objectDefinition = objectDefinition;
    this.usageAlias = usageAlias;
    this.usageNode = usageNode;
  }

  public String getObjectType() {
    return objectType;
  }

  public String getObjectName() {
    return objectName;
  }

  public String getObjectDefinition() {
    return objectDefinition;
  }

  public String getUsageAlias() {
    return usageAlias;
  }

  public ASTNode getUsageNode() {
    return usageNode;
  }
}
