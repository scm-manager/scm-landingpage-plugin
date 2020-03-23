package com.cloudogu.scm.favourite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@XmlRootElement(name = "favorites")
@XmlAccessorType(XmlAccessType.FIELD)
public class RepositoryFavorite {
  private String repositoryId;
  private Set<String> userIds;
}
