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
package com.cloudogu.scm.mydata;

import com.cloudogu.scm.favorite.FavoriteRepositoryProvider;
import de.otto.edison.hal.HalRepresentation;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.web.api.RepositoryToHalMapper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Extension
public class FavoriteRepositoryDataProvider implements MyDataProvider {

  private final FavoriteRepositoryProvider favoriteRepositoryProvider;
  private final RepositoryManager repositoryManager;
  private final RepositoryToHalMapper mapper;

  @Inject
  public FavoriteRepositoryDataProvider(FavoriteRepositoryProvider favoriteRepositoryProvider, RepositoryManager repositoryManager, RepositoryToHalMapper mapper) {
    this.favoriteRepositoryProvider = favoriteRepositoryProvider;
    this.repositoryManager = repositoryManager;
    this.mapper = mapper;
  }

  @Override
  public Iterable<MyData> getData() {
    Set<String> repositoryIds = favoriteRepositoryProvider.get().get().getRepositoryIds();

    List<MyData> data = new ArrayList<>();
    for (String repoId : repositoryIds) {
      Repository repository = repositoryManager.get(repoId);
      HalRepresentation repositoryDto = mapper.map(repository);
      data.add(new FavoriteRepositoryData(repositoryDto));
    }
    return data;
  }
}
