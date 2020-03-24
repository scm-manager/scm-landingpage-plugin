package com.cloudogu.scm;

import com.cloudogu.scm.mytasks.MyTaskProvider;
import com.cloudogu.scm.mytasks.PluginUpdateTask;
import com.google.common.collect.ImmutableList;
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

import javax.inject.Provider;
import java.net.URI;
import java.time.Instant;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndexLinkEnricherTest {

  @Mock
  private Provider<ScmPathInfoStore> scmPathInfoStoreProvider;
  @Mock
  private HalAppender appender;
  @Mock
  private MyTaskProvider taskProvider;

  private HalEnricherContext context;

  @InjectMocks
  private IndexLinkEnricher enricher;


  @BeforeEach
  void setUp() {
    ScmPathInfoStore scmPathInfoStore = new ScmPathInfoStore();
    scmPathInfoStore.set(() -> URI.create("https://scm-manager.org/scm/api/"));
    com.google.inject.Provider<ScmPathInfoStore> scmPathInfoStoreProvider = Providers.of(scmPathInfoStore);
    enricher = new IndexLinkEnricher(scmPathInfoStoreProvider, taskProvider);
    context = HalEnricherContext.of();
  }

  @Test
  void shouldNotAppendTasksLink() {
    when(taskProvider.getTasks()).thenReturn(Collections.emptyList());

    enricher.enrich(context, appender);

    verify(appender, never()).appendLink(anyString(), anyString());
  }

  @Test
  void shouldAppendTasksLink() {
    when(taskProvider.getTasks()).thenReturn(ImmutableList.of(new PluginUpdateTask(1, Instant.now())));

    enricher.enrich(context, appender);

    verify(appender).appendLink("tasks", "https://scm-manager.org/scm/api/v2/landingpage/mytasks");
  }

}
