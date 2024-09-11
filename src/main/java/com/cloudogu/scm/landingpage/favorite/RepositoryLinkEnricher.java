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

package com.cloudogu.scm.landingpage.favorite;

import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

@Extension
@Enrich(Repository.class)
public class RepositoryLinkEnricher implements HalEnricher {

  private final Provider<ScmPathInfoStore> scmPathInfoStore;
  private final FavoriteRepositoryProvider storeProvider;

  @Inject
  public RepositoryLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStore, FavoriteRepositoryProvider storeProvider) {
    this.scmPathInfoStore = scmPathInfoStore;
    this.storeProvider = storeProvider;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    Repository repository = context.oneRequireByType(Repository.class);
    if (RepositoryPermissions.read().isPermitted(repository)) {
      LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get().get(), FavoriteRepositoryResource.class);
      FavoriteRepository favorites = storeProvider.get().get();
      if (favorites.isFavorite(repository)) {
        appender.appendLink("unfavorize", linkBuilder.method("unfavorizeRepository").parameters(repository.getNamespace(), repository.getName()).href());
      } else {
        appender.appendLink("favorize", linkBuilder.method("favorizeRepository").parameters(repository.getNamespace(), repository.getName()).href());
      }
    }
  }
}
