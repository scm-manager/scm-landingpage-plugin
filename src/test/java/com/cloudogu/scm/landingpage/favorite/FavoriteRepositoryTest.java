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
