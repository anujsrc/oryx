/*
 * Copyright (c) 2013, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */

package com.cloudera.oryx.common.stats;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A bean encapsulating some characteristics of the JVM's runtime environment.
 *
 * @author Sean Owen
 */
public final class JVMEnvironment {

  private static final String UNKNOWN_HOST = "Unknown";

  private final Runtime runtime;
  private String hostName;

  public JVMEnvironment() {
    runtime = Runtime.getRuntime();
  }

  /**
   * @return approximate heap used, in bytes
   */
  public long getUsedMemory() {
    return runtime.totalMemory() - runtime.freeMemory();
  }

  /**
   * @return approximate heap used, in megabytes
   */
  public int getUsedMemoryMB() {
    return (int) (getUsedMemory() / 1000000);
  }

  /**
   * @return maximum size that the heap may grow to, in bytes
   */
  public long getMaxMemory() {
    return runtime.maxMemory();
  }

  /**
   * @return maximum size that the heap may grow to, in megabytes
   */
  public int getMaxMemoryMB() {
    return (int) (getMaxMemory() / 1000000);
  }

  /**
   * @return {@link #getUsedMemoryMB()} as a percentage of {@link #getMaxMemoryMB()} as a percentage in [0,100]
   */
  public int getPercentUsedMemory() {
    return (100 * getUsedMemoryMB()) / getMaxMemoryMB();
  }

  /**
   * @return the hostname of the local machine, or {@link #UNKNOWN_HOST} if it can't be determined
   */
  public String getHostName() {
    if (hostName == null || UNKNOWN_HOST.equals(hostName)) {
      try {
        hostName = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException ignored) {
        hostName = UNKNOWN_HOST;
      }
    }
    return hostName;
  }

  @Override
  public String toString() {
    return hostName + " : " + getUsedMemoryMB() + "MB used of " + getMaxMemoryMB() + "MB";
  }

}
