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

import { apiClient } from "@scm-manager/ui-components";
import { Link, Repository } from "@scm-manager/ui-types";
import { useMutation, useQuery, useQueryClient } from "react-query";
import { ApiResult, useRequiredIndexLink } from "@scm-manager/ui-api";
import { FavoriteRepositories } from "../types";

export const useFavoriteRepository = (repository: Repository) => {
  const queryClient = useQueryClient();

  const invalidateQueries = () => {
    return Promise.all([
      queryClient.invalidateQueries(["landingpage", "favoriteRepositories"]),
      queryClient.invalidateQueries(["repository", repository.namespace, repository.name]),
      queryClient.invalidateQueries(["repositories"])
    ]).then(() => undefined);
  };

  const { mutate, isLoading, error } = useMutation<unknown, Error, Link>(link => apiClient.post(link.href, {}), {
    onSuccess: invalidateQueries
  });

  return {
    favorize: repository._links.favorize ? () => mutate(repository._links.favorize as Link) : undefined,
    unfavorize: repository._links.unfavorize ? () => mutate(repository._links.unfavorize as Link) : undefined,
    isLoading,
    error
  };
};

export const useFavoriteRepositories = (): ApiResult<FavoriteRepositories> => {
  const indexLink = useRequiredIndexLink("favoriteRepositories");

  return useQuery<FavoriteRepositories, Error>(["landingpage", "favoriteRepositories"], () =>
    apiClient.get(indexLink).then(response => response.json())
  );
};

export type FavoriteRepository = Repository & {
  showNamespace: boolean;
};
