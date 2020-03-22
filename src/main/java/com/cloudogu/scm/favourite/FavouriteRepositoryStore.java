package com.cloudogu.scm.favourite;

import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

import javax.inject.Inject;

public class FavouriteRepositoryStore {

  private static final String FAVORITES_STORE_NAME = "repository-favorites";

  private final DataStoreFactory storeFactory;

  @Inject
  public FavouriteRepositoryStore(DataStoreFactory storeFactory) {
    this.storeFactory = storeFactory;
  }

  public RepositoryFavorite get(String repositoryId) {
    return createStore().get(repositoryId);
  }

  public void put(RepositoryFavorite newFavorite) {
    createStore().put(newFavorite.getRepositoryId(), newFavorite);
  }

  private DataStore<RepositoryFavorite> createStore() {
    return storeFactory.withType(RepositoryFavorite.class).withName(FAVORITES_STORE_NAME).build();
  }
}
