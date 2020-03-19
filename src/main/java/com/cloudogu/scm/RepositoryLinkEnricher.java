package com.cloudogu.scm.review;

import com.cloudogu.scm.RepositoryFavouriteResource;
import com.cloudogu.scm.RepositoryFavouriteStore;
import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;

import javax.inject.Inject;
import javax.inject.Provider;

@Extension
@Enrich(Repository.class)
public class RepositoryLinkEnricher implements HalEnricher {

  private final Provider<ScmPathInfoStore> scmPathInfoStore;
  private final RepositoryFavouriteStore store;

  @Inject
  public RepositoryLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStore, RepositoryFavouriteStore store) {
    this.scmPathInfoStore = scmPathInfoStore;
    this.store = store;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    Repository repository = context.oneRequireByType(Repository.class);
    LinkBuilder linkBuilder = new LinkBuilder(scmPathInfoStore.get().get(), RepositoryFavouriteResource.class);
    appender.appendLink("favorize", linkBuilder.method("favorize").parameters(repository.getNamespace(), repository.getName()).href());
  }
}
