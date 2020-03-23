package com.cloudogu.scm.favourite;

import org.junit.jupiter.api.Test;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;

import javax.xml.bind.JAXB;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

class RepositoryFavoriteTest {

  @Test
  void shouldBeMarshalledBackAndForth() {
    Repository heartOfGold = RepositoryTestData.createHeartOfGold();
    Repository puzzle42 = RepositoryTestData.create42Puzzle();
    Repository hvpt = RepositoryTestData.createHappyVerticalPeopleTransporter();

    RepositoryFavorite favorites = new RepositoryFavorite();
    favorites.add(heartOfGold);
    favorites.add(puzzle42);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    JAXB.marshal(favorites, out);

    favorites = JAXB.unmarshal(new ByteArrayInputStream(out.toByteArray()), RepositoryFavorite.class);
    assertThat(favorites.isFavorite(heartOfGold)).isTrue();
    assertThat(favorites.isFavorite(puzzle42)).isTrue();
    assertThat(favorites.isFavorite(hvpt)).isFalse();
  }

}
