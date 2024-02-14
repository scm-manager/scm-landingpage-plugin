/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
