package com.cloudogu.scm.landingpage.myevents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import sonia.scm.event.Event;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@Event
@NoArgsConstructor
public class MyRepositoryEvent extends MyEvent {
  private String repository;
  private boolean deleted;

  public MyRepositoryEvent(String type, String permission, String repository) {
    super(type, permission);
    this.repository = repository;
  }

  public MyRepositoryEvent(String type, String permission, String repository, boolean deleted) {
    super(type, permission);
    this.repository = repository;
    this.deleted = deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
}
