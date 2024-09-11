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
import { MyTasksType } from "../types";

type MyTasks = MyTasksType;

export const useMyTasks = (link: string) => {
  const { error, isLoading, data } = useQuery<MyTasks, Error>(["landingpage", "myTasks"], () =>
    apiClient.get(link).then(response => response.json())
  );

  return {
    error,
    isLoading,
    data
  };
};
