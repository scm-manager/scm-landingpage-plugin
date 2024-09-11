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

package com.cloudogu.scm.landingpage.myevents;

import com.github.legman.Subscribe;
import com.google.common.collect.Iterables;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.shiro.SecurityUtils;
import sonia.scm.EagerSingleton;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.PostReceiveRepositoryHookEvent;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;
import sonia.scm.user.User;

import jakarta.inject.Inject;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

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
    if (changesetCount == 0) {
      return;
    }
    User author = SecurityUtils.getSubject().getPrincipals().oneByType(User.class);


    store.add(new PushEvent(
      RepositoryPermissions.read(repository).asShiroString(),
      repository.getNamespace() + "/" + repository.getName(),
      author.getName(),
      author.getDisplayName(),
      author.getMail(),
      changesetCount));
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @Getter
  @NoArgsConstructor
  public static class PushEvent extends MyRepositoryEvent {

    private String authorName;
    private String authorDisplayName;
    private String authorMail;
    private int changesets;


    public PushEvent(String permission, String repository, String authorName, String authorDisplayName, String authorMail, int changesets) {
      super(PushEvent.class.getSimpleName(), permission, repository);
      this.authorName = authorName;
      this.authorDisplayName = authorDisplayName;
      this.authorMail = authorMail;
      this.changesets = changesets;
    }
  }
}
