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

package com.cloudogu.scm.landingpage.favorite;

import org.junit.jupiter.api.Test;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;

import jakarta.xml.bind.JAXB;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

class FavoriteRepositoryTest {

  @Test
  void shouldBeMarshalledBackAndForth() {
    Repository heartOfGold = RepositoryTestData.createHeartOfGold();
    Repository puzzle42 = RepositoryTestData.create42Puzzle();
    Repository hvpt = RepositoryTestData.createHappyVerticalPeopleTransporter();

    FavoriteRepository favorites = new FavoriteRepository();
    favorites.add(heartOfGold);
    favorites.add(puzzle42);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    JAXB.marshal(favorites, out);

    favorites = JAXB.unmarshal(new ByteArrayInputStream(out.toByteArray()), FavoriteRepository.class);
    assertThat(favorites.isFavorite(heartOfGold)).isTrue();
    assertThat(favorites.isFavorite(puzzle42)).isTrue();
    assertThat(favorites.isFavorite(hvpt)).isFalse();
  }

}
