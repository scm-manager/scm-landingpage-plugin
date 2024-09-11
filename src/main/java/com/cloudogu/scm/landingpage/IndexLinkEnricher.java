/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package com.cloudogu.scm.landingpage;

import com.cloudogu.scm.landingpage.favorite.FavoriteRepositoryResource;
import com.cloudogu.scm.landingpage.mydata.MyDataResource;
import com.cloudogu.scm.landingpage.myevents.MyEventResource;
import com.cloudogu.scm.landingpage.mytasks.MyTaskResource;
import org.apache.shiro.SecurityUtils;
import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.Index;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

@Extension
@Enrich(Index.class)
public class IndexLinkEnricher implements HalEnricher {

  private final Provider<ScmPathInfoStore> scmPathInfoStore;

  @Inject
  public IndexLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStore) {
    this.scmPathInfoStore = scmPathInfoStore;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    if (SecurityUtils.getSubject().isAuthenticated()) {
      appendTasksLink(appender);
      appendDataLink(appender);
      appendEventsLink(appender);
      appendFavoritesLink(appender);
    }
  }

  private void appendFavoritesLink(HalAppender appender) {
    String favoritesUrl = new LinkBuilder(scmPathInfoStore.get().get(), FavoriteRepositoryResource.class)
      .method("getFavoriteRepositories")
      .parameters()
      .href();

    appender.appendLink("favoriteRepositories", favoritesUrl);
  }

  private void appendDataLink(HalAppender appender) {
    String dataUrl = new LinkBuilder(scmPathInfoStore.get().get(), MyDataResource.class)
      .method("getData")
      .parameters()
      .href();

    appender.appendLink("landingpageData", dataUrl);
  }

  private void appendTasksLink(HalAppender appender) {
    String tasksUrl = new LinkBuilder(scmPathInfoStore.get().get(), MyTaskResource.class)
      .method("getTasks")
      .parameters()
      .href();

    appender.appendLink("landingpageTasks", tasksUrl);
  }

  private void appendEventsLink(HalAppender appender) {
    String tasksUrl = new LinkBuilder(scmPathInfoStore.get().get(), MyEventResource.class)
      .method("getEvents")
      .parameters()
      .href();

    appender.appendLink("landingpageEvents", tasksUrl);
  }
}
