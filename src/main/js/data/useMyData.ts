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

import { useQuery } from "react-query";
import { apiClient } from "@scm-manager/ui-components";
import { MyDataEntriesType } from "../types";
import { useDisabledCategories } from "../config/hooks";

type MyData = MyDataEntriesType;

export const useMyData = (link: string) => {
  const { disabledCategories } = useDisabledCategories();
  if (disabledCategories.length) {
    link += `?disabledTypes=${disabledCategories.join(",")}`;
  }
  const { error, isLoading, data } = useQuery<MyData, Error>(["landingpage", "myData"], () =>
    apiClient.get(link).then(response => response.json())
  );

  return {
    error,
    isLoading,
    data
  };
};
