package com.cloudogu.scm;

import com.cloudogu.scm.mydata.MyDataResource;
import com.cloudogu.scm.mytasks.MyTaskResource;
import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.Index;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;

import javax.inject.Inject;
import javax.inject.Provider;

@Extension
@Enrich(Index.class)
public class IndexLinkEnricher implements HalEnricher {

  private final Provider<ScmPathInfoStore> scmPathInfoStore;

  @Inject
  public IndexLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStore) {
    this.scmPathInfoStore = scmPathInfoStore;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    appendTasksLink(appender);
    appendDataLink(appender);
  }

  private void appendDataLink(HalAppender appender) {
    String dataUrl = new LinkBuilder(scmPathInfoStore.get().get(), MyDataResource.class)
      .method("getData")
      .parameters()
      .href();

    appender.appendLink("data", dataUrl);
  }

  private void appendTasksLink(HalAppender appender) {
    String tasksUrl = new LinkBuilder(scmPathInfoStore.get().get(), MyTaskResource.class)
      .method("getTasks")
      .parameters()
      .href();

    appender.appendLink("tasks", tasksUrl);
  }
}
