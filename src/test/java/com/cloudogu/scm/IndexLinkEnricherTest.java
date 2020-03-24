package com.cloudogu.scm;

import com.cloudogu.scm.mytasks.MyTaskProvider;
import com.google.inject.util.Providers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;

import java.net.URI;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IndexLinkEnricherTest {

  @Mock
  private HalAppender appender;

  private HalEnricherContext context;

  @InjectMocks
  private IndexLinkEnricher enricher;


  @BeforeEach
  void setUp() {
    ScmPathInfoStore scmPathInfoStore = new ScmPathInfoStore();
    scmPathInfoStore.set(() -> URI.create("https://scm-manager.org/scm/api/"));
    com.google.inject.Provider<ScmPathInfoStore> scmPathInfoStoreProvider = Providers.of(scmPathInfoStore);
    enricher = new IndexLinkEnricher(scmPathInfoStoreProvider);
    context = HalEnricherContext.of();
  }


  @Test
  void shouldAppendTasksLink() {
    enricher.enrich(context, appender);

    verify(appender).appendLink("tasks", "https://scm-manager.org/scm/api/v2/landingpage/mytasks");
  }

}
