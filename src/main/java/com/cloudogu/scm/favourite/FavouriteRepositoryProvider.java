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
package com.cloudogu.scm.favourite;

import org.apache.shiro.SecurityUtils;
import sonia.scm.repository.Repository;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

import javax.inject.Inject;

public class FavouriteRepositoryProvider {

  private static final String STORE_NAME = "favourite-repositories";

  private final DataStore<RepositoryFavorite> store;

  @Inject
  public FavouriteRepositoryProvider(DataStoreFactory storeFactory) {
    this.store = storeFactory.withType(RepositoryFavorite.class).withName(STORE_NAME).build();;
  }

  public FavouriteRepositoryStore get() {
    String principal = SecurityUtils.getSubject().getPrincipal().toString();
    return new FavouriteRepositoryStore(principal, store);
  }

  public class FavouriteRepositoryStore {

    private final String principal;
    private final DataStore<RepositoryFavorite> store;

    private FavouriteRepositoryStore(String principal, DataStore<RepositoryFavorite> store) {
      this.principal = principal;
      this.store = store;
    }

    public void add(Repository repository) {
      RepositoryFavorite repositoryFavorite = store.get(principal);
      if (repositoryFavorite == null) {
        repositoryFavorite = new RepositoryFavorite();
      }
      repositoryFavorite.add(repository);
      store.put(principal, repositoryFavorite);
    }

    public void remove(Repository repository) {
      RepositoryFavorite repositoryFavorite = store.get(principal);
      if (repositoryFavorite != null) {
        repositoryFavorite.remove(repository);
        store.put(principal, repositoryFavorite);
      }
    }

    public RepositoryFavorite get() {
      return store.get(principal);
    }
  }
}
