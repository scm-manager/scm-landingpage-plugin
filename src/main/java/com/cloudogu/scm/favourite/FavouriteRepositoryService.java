package com.cloudogu.scm.favourite;

import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;

import javax.inject.Inject;
import java.util.Set;

public class FavouriteRepositoryService {

  private final FavouriteRepositoryStore store;
  private final RepositoryServiceFactory serviceFactory;

  @Inject
  public FavouriteRepositoryService(FavouriteRepositoryStore store, RepositoryServiceFactory serviceFactory) {
    this.store = store;
    this.serviceFactory = serviceFactory;
  }

  public void favorizeRepository(NamespaceAndName namespaceAndName, String userId) {
    try (RepositoryService repositoryService = serviceFactory.create(namespaceAndName)) {
      Repository repository = repositoryService.getRepository();
      RepositoryPermissions.read().check(repository);
      Set<String> favorites = store.get(repository.getId()).getUserIds();
      favorites.add(userId);
      store.put(new RepositoryFavorite(repository.getId(), favorites));
    }
  }

  public void unfavorizeRepository(NamespaceAndName namespaceAndName, String userId) {
    try (RepositoryService repositoryService = serviceFactory.create(namespaceAndName)) {
      Repository repository = repositoryService.getRepository();
      RepositoryPermissions.read().check(repository);
      Set<String> favorites = store.get(repository.getId()).getUserIds();
      favorites.remove(userId);
      store.put(new RepositoryFavorite(repository.getId(), favorites));
    }
  }
}
