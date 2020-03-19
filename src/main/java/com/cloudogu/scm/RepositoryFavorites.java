package com.cloudogu.scm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@XmlRootElement(name = "favorites")
@XmlAccessorType(XmlAccessType.FIELD)
public class RepositoryFavorites {
  private List<String> favorites;
}
