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

import sonia.scm.repository.Repository;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

import jakarta.inject.Inject;
import java.util.Map;

import static org.apache.shiro.SecurityUtils.getSubject;

public class FavoriteRepositoryProvider {

  static final String STORE_NAME = "favorite-repositories";

  private final DataStore<FavoriteRepository> store;

  @Inject
  public FavoriteRepositoryProvider(DataStoreFactory storeFactory) {
    this.store = storeFactory.withType(FavoriteRepository.class).withName(STORE_NAME).build();
  }

  public FavoriteRepositoryStore get() {
    return new FavoriteRepositoryStore(getSubject().getPrincipal().toString());
  }

  public class FavoriteRepositoryStore {

    private final String principal;

    private FavoriteRepositoryStore(String principal) {
      this.principal = principal;
    }

    public void removeFromAll(Repository repository) {
      final Map<String, FavoriteRepository> all = store.getAll();
      for (Map.Entry<String, FavoriteRepository> entry : all.entrySet()) {
        final FavoriteRepository favoriteRepository = entry.getValue();
        favoriteRepository.remove(repository);
        store.put(entry.getKey(), favoriteRepository);
      }
    }

    public void add(Repository repository) {
      FavoriteRepository favoriteRepository = store.get(principal);
      if (favoriteRepository == null) {
        favoriteRepository = new FavoriteRepository();
      }
      favoriteRepository.add(repository);
      store.put(principal, favoriteRepository);
    }

    public void remove(Repository repository) {
      FavoriteRepository favoriteRepository = store.get(principal);
      if (favoriteRepository != null) {
        favoriteRepository.remove(repository);
        store.put(principal, favoriteRepository);
      }
    }

    public FavoriteRepository get() {
      return get(this.principal);
    }

    public FavoriteRepository get(String principal) {
      FavoriteRepository favoriteRepository = store.get(principal);
      if (favoriteRepository == null) {
        favoriteRepository = new FavoriteRepository();
      }
      return favoriteRepository;
    }
  }
}
