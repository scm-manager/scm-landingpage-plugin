/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.cloudogu.scm.landingpage;

import com.cloudogu.scm.landingpage.mydata.MyDataResource;
import com.cloudogu.scm.landingpage.myevents.MyEventResource;
import com.cloudogu.scm.landingpage.mytasks.MyTaskResource;
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
    appendEventsLink(appender);
  }

  private void appendDataLink(HalAppender appender) {
    String dataUrl = new LinkBuilder(scmPathInfoStore.get().get(), MyDataResource.class)
      .method("getData")
      .parameters()
      .href();

    appender.appendLink("landingpageData", dataUrl);
  }

  private void appendTasksLink(HalAppender appender) {
    String tasksUrl = new LinkBuilder(scmPathInfoStore.get().get(), MyTaskResource.class)
      .method("getTasks")
      .parameters()
      .href();

    appender.appendLink("landingpageTasks", tasksUrl);
  }

  private void appendEventsLink(HalAppender appender) {
    String tasksUrl = new LinkBuilder(scmPathInfoStore.get().get(), MyEventResource.class)
      .method("getEvents")
      .parameters()
      .href();

    appender.appendLink("landingpageEvents", tasksUrl);
  }
}
