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

import com.github.legman.Subscribe;
import sonia.scm.EagerSingleton;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.RepositoryEvent;

import jakarta.inject.Inject;

import static sonia.scm.HandlerEventType.DELETE;

@Extension
@EagerSingleton
public class RemoveDeletedRepositoryFavorites {

  private final FavoriteRepositoryService favoriteRepositoryService;

  @Inject
  public RemoveDeletedRepositoryFavorites(FavoriteRepositoryService favoriteRepositoryService) {
    this.favoriteRepositoryService = favoriteRepositoryService;
  }

  @Subscribe
  public void handle(RepositoryEvent event) {
    if (event.getEventType() == DELETE) {
      this.favoriteRepositoryService.unfavorizeRepositoryForAllUsers(event.getItem());
    }
  }

}
