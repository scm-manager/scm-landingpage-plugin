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

package com.cloudogu.scm.landingpage.myevents;

import com.github.legman.Subscribe;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.shiro.SecurityUtils;
import sonia.scm.EagerSingleton;
import sonia.scm.HandlerEventType;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryEvent;
import sonia.scm.repository.RepositoryPermissions;
import sonia.scm.user.User;

import jakarta.inject.Inject;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@Extension
@EagerSingleton
public class RepositoryDeletedEventSubscriber {

  private final MyEventStore store;

  @Inject
  public RepositoryDeletedEventSubscriber(MyEventStore store) {
    this.store = store;
  }

  @Subscribe
  public void handleEvent(RepositoryEvent event) {
    if (event.getEventType() == HandlerEventType.DELETE) {
      Repository eventRepo = event.getItem();
      String permission = RepositoryPermissions.read(event.getItem()).asShiroString();
      String repository = eventRepo.getNamespace() + "/" + eventRepo.getName();
      User deleter = SecurityUtils.getSubject().getPrincipals().oneByType(User.class);

      store.add(new RepositoryDeletedEvent(permission, repository, deleter.getName(), deleter.getDisplayName(), deleter.getMail()));
      store.markRepositoryEventsAsDeleted(repository);
    }
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @Getter
  @NoArgsConstructor
  static class RepositoryDeletedEvent extends MyEvent {
    private String repository;
    private String deleterName;
    private String deleterDisplayName;
    private String deleterMail;

    RepositoryDeletedEvent(String permission, String repository, String deleterName, String deleterDisplayName, String deleterMail) {
      super(RepositoryDeletedEvent.class.getSimpleName(), permission);
      this.repository = repository;
      this.deleterName = deleterName;
      this.deleterDisplayName = deleterDisplayName;
      this.deleterMail = deleterMail;
    }
  }
}
