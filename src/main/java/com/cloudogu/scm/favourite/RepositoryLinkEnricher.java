package com.cloudogu.scm.favourite;

import org.apache.shiro.SecurityUtils;
import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;

import javax.inject.Inject;
import javax.inject.Provider;

@Extension
@Enrich(Repository.class)
public class RepositoryLinkEnricher implements HalEnricher {

  private final Provider<ScmPathInfoStore> scmPathInfoStore;
  private final FavouriteRepositoryStore store;

  @Inject
  public RepositoryLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStore, FavouriteRepositoryStore store) {
    this.scmPathInfoStore = scmPathInfoStore;
    this.store = store;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    Repository repository = context.oneRequireByType(Repository.class);
    if (RepositoryPermissions.read().isPermitted(repository)) {
      RepositoryFavorite favoritesForRepository = store.get(repository.getId());
      String username = (String) SecurityUtils.getSubject().getPrincipal();
      LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get().get(), FavouriteRepositoryResource.class);
      if (favoritesForRepository.getUserIds().contains(username)) {
        appender.appendLink("unfavorize", linkBuilder.method("unfavorizeRepository").parameters(repository.getNamespace(), repository.getName(), username).href());
      } else {
        appender.appendLink("favorize", linkBuilder.method("favorizeRepository").parameters(repository.getNamespace(), repository.getName(), username).href());
      }
    }
  }
}
