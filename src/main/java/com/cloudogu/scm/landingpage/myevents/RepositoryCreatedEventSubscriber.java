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

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.Instant;

@Extension
@EagerSingleton
public class RepositoryCreatedEventSubscriber {

  private final MyEventStore store;

  @Inject
  public RepositoryCreatedEventSubscriber(MyEventStore store) {
    this.store = store;
  }

  @Subscribe
  public void handleEvent(RepositoryEvent event) {
    if (event.getEventType() == HandlerEventType.CREATE) {
      Repository eventRepo = event.getItem();
      String permission = RepositoryPermissions.read(event.getItem()).asShiroString();
      String repository = eventRepo.getNamespace() + "/" + eventRepo.getName();
      User creator = SecurityUtils.getSubject().getPrincipals().oneByType(User.class);
      Instant date = Instant.now();

      store.add(new RepositoryCreatedEvent(permission, repository,  creator.getName(), creator.getDisplayName(), creator.getMail(), date));
    }
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @Getter
  @NoArgsConstructor
  static class RepositoryCreatedEvent extends MyEvent {
    private String repository;
    private String creatorName;
    private String creatorDisplayName;
    private String creatorMail;

    RepositoryCreatedEvent(String permission, String repository, String creatorName, String creatorDisplayName, String creatorMail, Instant date) {
      super(RepositoryCreatedEvent.class.getSimpleName(), permission, date);
      this.repository = repository;
      this.creatorName = creatorName;
      this.creatorDisplayName = creatorDisplayName;
      this.creatorMail = creatorMail;
    }
  }

}
