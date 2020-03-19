package com.cloudogu.scm;

import javax.inject.Inject;

public class RepositoryFavouriteService {

  private final RepositoryFavouriteStore store;

  @Inject
  public RepositoryFavouriteService(RepositoryFavouriteStore store) {
    this.store = store;
  }

}
