package com.cloudogu.scm.favourite;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

@Getter
@AllArgsConstructor
@XmlRootElement(name = "favorite")
@XmlAccessorType(XmlAccessType.FIELD)
public class RepositoryFavorite {
  private String repositoryId;
  private Set<String> userIds;
}
