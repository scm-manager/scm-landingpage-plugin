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

import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.repository.RepositoryPermissions;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;

import jakarta.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class FavoriteRepositoryService {

  private final FavoriteRepositoryProvider store;
  private final RepositoryServiceFactory serviceFactory;
  private final RepositoryManager repositoryManager;

  @Inject
  public FavoriteRepositoryService(FavoriteRepositoryProvider store, RepositoryServiceFactory serviceFactory, RepositoryManager repositoryManager) {
    this.store = store;
    this.serviceFactory = serviceFactory;
    this.repositoryManager = repositoryManager;
  }

  public void favorizeRepository(NamespaceAndName namespaceAndName) {
    try (RepositoryService repositoryService = serviceFactory.create(namespaceAndName)) {
      Repository repository = repositoryService.getRepository();
      RepositoryPermissions.read().check(repository);
      store.get().add(repository);
    }
  }

  public void unfavorizeRepository(NamespaceAndName namespaceAndName) {
    try (RepositoryService repositoryService = serviceFactory.create(namespaceAndName)) {
      Repository repository = repositoryService.getRepository();
      RepositoryPermissions.read().check(repository);
      store.get().remove(repository);
    }
  }

  void unfavorizeRepositoryForAllUsers(Repository repository) {
    store.get().removeFromAll(repository);
  }

  List<Repository> getFavoriteRepositories() {
    Set<String> repositoryIds = store.get().get().getRepositoryIds();

    return repositoryIds.stream()
      .filter(repoId -> RepositoryPermissions.read(repoId).isPermitted())
      .map(repositoryManager::get)
      .filter(Objects::nonNull)
      .sorted(Comparator.comparing(r -> r.getName().toLowerCase()))
      .collect(Collectors.toList());
  }
}
