package com.cloudogu.scm;

import sonia.scm.repository.Repository;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

import javax.inject.Inject;

public class RepositoryFavouriteStore {

  private static final String FAVORITES_STORE_NAME = "favorites";

  private final DataStore<RepositoryFavorites> store;
  private Repository repository;

  @Inject
  public RepositoryFavouriteStore(DataStoreFactory dataStoreFactory, Repository repository) {
    store = dataStoreFactory.withType(RepositoryFavorites.class).withName(FAVORITES_STORE_NAME).forRepository(repository).build();
    this.repository = repository;
  }

  public RepositoryFavorites getAll(Repository repository) {
    return store.get(repository.getId());
  }

  public void put(Repository repository, RepositoryFavorites repositoryFavorites) {
    store.put(repository.getId(), repositoryFavorites);
  }

}
