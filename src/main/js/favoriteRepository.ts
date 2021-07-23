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

import { apiClient } from "@scm-manager/ui-components";
import { Link, Repository } from "@scm-manager/ui-types";
import { useMutation, useQueryClient } from "react-query";

export const useFavoriteRepository = (repository: Repository) => {
  const queryClient = useQueryClient();

  const invalidateQueries = () => {
    queryClient.invalidateQueries(["repository", repository.namespace, repository.name]);
    queryClient.invalidateQueries(["landingpage", "myData"]);
    return queryClient.invalidateQueries(["repositories"]);
  };

  const { mutate: favorize, isLoading: isLoadingFavorize, error: favorizeError } = useMutation<unknown, Error>(
    () => {
      return apiClient.post((repository?._links?.favorize as Link).href, {});
    },
    {
      onSuccess: invalidateQueries
    }
  );

  const { mutate: unfavorize, isLoading: isLoadingUnfavorize, error: unfavorizeError } = useMutation<
    unknown,
    Error
  >(
    () => {
      return apiClient.post((repository?._links?.unfavorize as Link).href, {});
    },
    {
      onSuccess: invalidateQueries
    }
  );

  return {
    favorize: repository._links.favorize ? () => favorize() : undefined,
    unfavorize: repository._links.unfavorize ? () => unfavorize() : undefined,
    isLoading: isLoadingFavorize || isLoadingUnfavorize,
    error: favorizeError || unfavorizeError
  };
};
