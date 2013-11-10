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

package com.cloudera.oryx.rdf.serving.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudera.oryx.rdf.serving.generation.RDFGenerationManager;

/**
 * <p>Responds to a POST request to {@code /refresh},
 * and in turn calls {@link RDFGenerationManager#refresh()} .
 *
 * @author Sean Owen
 */
public final class RefreshServlet extends AbstractRDFServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    RDFGenerationManager generationManager = getGenerationManager();
    if (generationManager != null) {
      generationManager.refresh();
    }
  }

}
