package com.cloudogu.scm.mydata;

import com.cloudogu.scm.favourite.FavouriteRepositoryProvider;
import com.cloudogu.scm.favourite.RepositoryFavorite;
import com.google.common.collect.ImmutableSet;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.repository.RepositoryTestData;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavouriteRepositoryDataProviderTest {

  private final Repository REPOSITORY = RepositoryTestData.createHeartOfGold();

  @Mock
  private RepositoryManager repositoryManager;
  @Mock
  private FavouriteRepositoryProvider favouriteRepositoryProvider;
  @Mock
  private FavouriteRepositoryProvider.FavouriteRepositoryStore store;
  @Mock
  private Subject subject;

  @InjectMocks
  private FavouriteRepositoryDataProvider dataProvider;

  @BeforeEach
  void initSubject() {
    ThreadContext.bind(subject);
  }

  @AfterEach
  void tearDownSubject() {
    ThreadContext.unbindSubject();
  }

  @Test
  void shouldReturnEmptyList() {
    RepositoryFavorite favorite = new RepositoryFavorite(ImmutableSet.of());
    when(favouriteRepositoryProvider.get()).thenReturn(store);
    when(store.get()).thenReturn(favorite);

    Iterable<MyData> data = dataProvider.getData();

    assertThat(data.iterator().hasNext()).isFalse();
  }

  @Test
  void shouldReturnFavouriteRepositories() {
    RepositoryFavorite favorite = new RepositoryFavorite(ImmutableSet.of(REPOSITORY.getId()));
    when(favouriteRepositoryProvider.get()).thenReturn(store);
    when(store.get()).thenReturn(favorite);
    when(repositoryManager.get(REPOSITORY.getId())).thenReturn(REPOSITORY);

    Iterable<MyData> data = dataProvider.getData();

    Iterator<MyData> iterator = data.iterator();

    MyData data1 = iterator.next();
    assertThat(data1.getLink()).isEqualTo("/repos/hitchhiker/HeartOfGold");
    assertThat(data1.getType()).isEqualTo(FavouriteRepositoryData.class.getSimpleName());
  }
}
