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

package com.cloudera.oryx.rdf.common.example;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Represents a value of a categorical feature -- one that takes on discrete, unordered values like
 * {@code {male,female}} rather than a continuous or discrete numeric value.
 *
 * @author Sean Owen
 * @see NumericFeature
 */
public final class CategoricalFeature implements Feature {

  // Not obvious we can get away with a cache without weak entries, but there should be relatively
  // few distinct category values ever observed
  private static final Map<Integer,CategoricalFeature> FEATURE_CACHE = Maps.newConcurrentMap();

  private final int valueID;

  private CategoricalFeature(int valueID) {
    this.valueID = valueID;
  }

  /**
   * @param valueID category value ID to create {@code CategoricalFeature} for
   * @return {@code CategoricalFeature} representing the category value specified by ID
   */
  public static CategoricalFeature forValue(int valueID) {
    Preconditions.checkArgument(valueID >= 0);
    Integer valueIDKey = valueID;
    CategoricalFeature feature = FEATURE_CACHE.get(valueIDKey);
    // Not important if several threads get here
    if (feature == null) {
      feature = new CategoricalFeature(valueID);
      FEATURE_CACHE.put(valueIDKey, feature);
    }
    return feature;
  }

  /**
   * @return category value ID represented by this {@code CategoricalFeature}
   */
  public int getValueID() {
    return valueID;
  }

  /**
   * @return {@link FeatureType#CATEGORICAL}
   */
  @Override
  public FeatureType getFeatureType() {
    return FeatureType.CATEGORICAL;
  }

  @Override
  public String toString() {
    return ":" + valueID;
  }
  
  @Override
  public int hashCode() {
    return valueID;
  }
  
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof CategoricalFeature)) {
      return false;
    }
    CategoricalFeature other = (CategoricalFeature) o;
    return valueID == other.valueID;
  }

}
