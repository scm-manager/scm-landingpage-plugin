package com.cloudogu.scm.mydata;

import com.cloudogu.scm.favourite.FavouriteRepositoryProvider;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class FavouriteRepositoryDataProvider implements MyDataProvider {

  private final FavouriteRepositoryProvider favouriteRepositoryProvider;
  private final RepositoryManager repositoryManager;

  @Inject
  public FavouriteRepositoryDataProvider(FavouriteRepositoryProvider favouriteRepositoryProvider, RepositoryManager repositoryManager) {
    this.favouriteRepositoryProvider = favouriteRepositoryProvider;
    this.repositoryManager = repositoryManager;
  }

  @Override
  public Iterable<MyData> getData() {
    Set<String> repositoryIds = favouriteRepositoryProvider.get().get().getRepositoryIds();

    List<MyData> data = new ArrayList<>();
    for (String repoId : repositoryIds) {
      Repository repository = repositoryManager.get(repoId);
      String repoLink = "/repos/" + repository.getNamespace() + "/" + repository.getName();
      data.add(new FavouriteRepositoryData(repoLink));
    }
    return data;
  }
}
