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
