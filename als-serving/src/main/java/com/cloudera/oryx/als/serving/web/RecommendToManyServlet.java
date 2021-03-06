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

package com.cloudera.oryx.als.serving.web;

import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudera.oryx.als.common.Rescorer;
import com.cloudera.oryx.als.common.NoSuchUserException;
import com.cloudera.oryx.als.common.NotReadyException;
import com.cloudera.oryx.als.common.OryxRecommender;
import com.cloudera.oryx.als.serving.RescorerProvider;

/**
 * <p>Responds to a GET request to
 * {@code /recommendToMany/[userID1](/[userID2]/...)(?howMany=n)(&considerKnownItems=true|false)(&rescorerParams=...)}
 * and in turn calls
 * {@link OryxRecommender#recommendToMany(String[], int, boolean, Rescorer)}.
 * If {@code howMany} is not specified, defaults to
 * {@link AbstractALSServlet#DEFAULT_HOW_MANY}. If {@code considerKnownItems} is not specified,
 * it is considered {@code false}.</p>
 *
 * <p>Unknown user IDs are ignored, unless all are unknown, in which case a
 * {@link HttpServletResponse#SC_BAD_REQUEST} status is returned.</p>
 *
 * <p>CSV output contains one recommendation per line, and each line is of the form {@code itemID, strength},
 * like {@code 325, 0.53}. Strength is an opaque indicator of the relative quality of the recommendation.</p>
 *
 * @author Sean Owen
 */
public final class RecommendToManyServlet extends AbstractALSServlet {
  
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    CharSequence pathInfo = request.getPathInfo();
    if (pathInfo == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No path");
      return;
    }
    Iterator<String> pathComponents = SLASH.split(pathInfo).iterator();
    Set<String> userIDSet = Sets.newHashSet();
    try {
      while (pathComponents.hasNext()) {
        userIDSet.add(pathComponents.next());
      }
    } catch (NoSuchElementException nsee) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, nsee.toString());
      return;
    }

    String[] userIDs = userIDSet.toArray(new String[userIDSet.size()]);
    unescapeSlashHack(userIDs);

    OryxRecommender recommender = getRecommender();
    RescorerProvider rescorerProvider = getRescorerProvider();
    try {
      Rescorer rescorer = rescorerProvider == null ? null :
          rescorerProvider.getRecommendRescorer(userIDs, recommender, getRescorerParams(request));
      output(response, recommender.recommendToMany(userIDs,
                                                   getHowMany(request),
                                                   getConsiderKnownItems(request),
                                                   rescorer));
    } catch (NoSuchUserException nsue) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, nsue.toString());
    } catch (NotReadyException nre) {
      response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, nre.toString());
    } catch (IllegalArgumentException iae) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, iae.toString());
    }
  }

}
