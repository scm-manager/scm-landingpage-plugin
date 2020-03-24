package com.cloudogu.scm.mydata;

import com.cloudogu.scm.favorite.FavoriteRepositoryProvider;
import com.cloudogu.scm.favorite.FavoriteRepository;
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
class FavoriteRepositoryDataProviderTest {

  private final Repository REPOSITORY = RepositoryTestData.createHeartOfGold();

  @Mock
  private RepositoryManager repositoryManager;
  @Mock
  private FavoriteRepositoryProvider favoriteRepositoryProvider;
  @Mock
  private FavoriteRepositoryProvider.FavoriteRepositoryStore store;
  @Mock
  private Subject subject;

  @InjectMocks
  private FavoriteRepositoryDataProvider dataProvider;

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
    FavoriteRepository favorite = new FavoriteRepository(ImmutableSet.of());
    when(favoriteRepositoryProvider.get()).thenReturn(store);
    when(store.get()).thenReturn(favorite);

    Iterable<MyData> data = dataProvider.getData();

    assertThat(data.iterator().hasNext()).isFalse();
  }

  @Test
  void shouldReturnFavoriteRepositories() {
    FavoriteRepository favorite = new FavoriteRepository(ImmutableSet.of(REPOSITORY.getId()));
    when(favoriteRepositoryProvider.get()).thenReturn(store);
    when(store.get()).thenReturn(favorite);
    when(repositoryManager.get(REPOSITORY.getId())).thenReturn(REPOSITORY);

    Iterable<MyData> data = dataProvider.getData();

    Iterator<MyData> iterator = data.iterator();

    MyData data1 = iterator.next();
    assertThat(data1.getLink()).isEqualTo("/repos/hitchhiker/HeartOfGold");
    assertThat(data1.getType()).isEqualTo(FavoriteRepositoryData.class.getSimpleName());
  }
}
