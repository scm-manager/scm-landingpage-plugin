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

import { useLocalStorage } from "@scm-manager/ui-api";
import { useMemo } from "react";

export function useIsCategoryDisabled(category: string) {
  const [disabledCategories] = useLocalStorage<Array<string>>("scm.landingPagePlugin.disabledCategories", []);
  return useMemo(() => disabledCategories.includes(category), [category, disabledCategories]);
}

export const showNamespaceInFavoriteOptions = ["NEVER", "DUPLICATES_ONLY", "ALWAYS"] as const;

export type ShowNamespaceInFavoriteOption = typeof showNamespaceInFavoriteOptions[number];

export const isShowNamespaceInFavoriteOption = (option: string): option is ShowNamespaceInFavoriteOption => {
  return showNamespaceInFavoriteOptions.includes(option as ShowNamespaceInFavoriteOption);
};

export function useListOptions() {
  const [{ pageSize, showArchived, showNamespace }, setListOptions] = useLocalStorage<{
    pageSize: number;
    showArchived: boolean;
    showNamespace: ShowNamespaceInFavoriteOption;
  }>("scm.landingPagePlugin.listOptions", {
    pageSize: 10,
    showArchived: true,
    showNamespace: "DUPLICATES_ONLY"
  });
  return {
    showNamespace,
    pageSize,
    showArchived,
    setListOptions
  };
}

export function useDisabledCategories() {
  const [disabledCategories, setDisabledCategories] = useLocalStorage<Array<string>>(
    "scm.landingPagePlugin.disabledCategories",
    []
  );
  const isDisabled = (category: string) => disabledCategories.includes(category);
  const setCategories: (categories: { [key: string]: boolean }) => void = categories => {
    let newDisabledCategories = disabledCategories;
    Object.entries(categories).forEach(([category, enabled]) => {
      const otherCategories = newDisabledCategories.filter(it => it !== category);
      if (enabled) {
        newDisabledCategories = otherCategories;
      } else {
        newDisabledCategories = [...otherCategories, category];
      }
    });
    setDisabledCategories(newDisabledCategories);
  };
  return {
    isDisabled,
    setCategories,
    disabledCategories
  };
}

export function useCollapsedState(category: string) {
  return useLocalStorage<boolean | null>(`scm.landingPagePlugin.collapsedState.${category}`, null);
}
