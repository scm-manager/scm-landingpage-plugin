package com.cloudogu.scm.mydata;

import com.cloudogu.scm.favorite.FavoriteRepositoryProvider;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Extension
public class FavoriteRepositoryDataProvider implements MyDataProvider {

  private final FavoriteRepositoryProvider favoriteRepositoryProvider;
  private final RepositoryManager repositoryManager;

  @Inject
  public FavoriteRepositoryDataProvider(FavoriteRepositoryProvider favoriteRepositoryProvider, RepositoryManager repositoryManager) {
    this.favoriteRepositoryProvider = favoriteRepositoryProvider;
    this.repositoryManager = repositoryManager;
  }

  @Override
  public Iterable<MyData> getData() {
    Set<String> repositoryIds = favoriteRepositoryProvider.get().get().getRepositoryIds();

    List<MyData> data = new ArrayList<>();
    for (String repoId : repositoryIds) {
      Repository repository = repositoryManager.get(repoId);
      String repoLink = "/repos/" + repository.getNamespace() + "/" + repository.getName();
      data.add(new FavoriteRepositoryData(repoLink));
    }
    return data;
  }
}
