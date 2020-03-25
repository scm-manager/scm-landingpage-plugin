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
package com.cloudogu.scm.myevents;

import com.github.legman.Subscribe;
import com.google.common.collect.Iterables;
import lombok.NoArgsConstructor;
import org.apache.shiro.SecurityUtils;
import sonia.scm.EagerSingleton;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.PostReceiveRepositoryHookEvent;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;
import sonia.scm.user.User;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Extension
@EagerSingleton
public class RepositoryPushEventSubscriber {

  private final MyEventStore store;

  @Inject
  public RepositoryPushEventSubscriber(MyEventStore store) {
    this.store = store;
  }

  @Subscribe
  public void handleEvents(PostReceiveRepositoryHookEvent event) {
    Repository repository = event.getRepository();
    Iterable<Changeset> changesets = event.getContext().getChangesetProvider().getChangesets();
    int changesetCount = Iterables.size(changesets);
    String author = SecurityUtils.getSubject().getPrincipals().oneByType(User.class).getDisplayName();

    String link = String.format("/repo/%s/%s/code/changesets", repository.getNamespace(), repository.getName());

    store.add(new PushEvent(
      RepositoryPermissions.read(repository).asShiroString(),
      link,
      repository.getNamespace() + "/" + repository.getName(),
      author,
      changesetCount
    ));
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class PushEvent extends MyEvent {

    private final String repository;
    private final String author;
    private final int changesets;

    public PushEvent(String permission, String link, String repository, String author, int changesets) {
      super(PushEvent.class.getSimpleName(), permission, link);
      this.repository = repository;
      this.author = author;
      this.changesets = changesets;
    }
  }
}
