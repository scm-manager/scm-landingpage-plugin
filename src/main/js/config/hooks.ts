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

import { useEffect, useState } from "react";

function useLocalStorage<T>(key: string, initialValue: T): [value: T, setValue: (value: T | ((previousConfig: T) => T)) => void] {
  const [value, setValue] = useState<T>(() => {
    try {
      const item = localStorage.getItem(key);
      return item ? JSON.parse(item) : initialValue;
    } catch (error) {
      console.error(error);
      return initialValue;
    }
  });

  useEffect(() => localStorage.setItem(key, JSON.stringify(value)), [key, value]);

  return [value, setValue];
}

export function useIsCategoryDisabled(category: string) {
  const [disabledCategories] = useLocalStorage<Array<string>>("scm.landingPagePlugin.disabledCategories", []);
  return disabledCategories.includes(category);
}

export function useListOptions() {
  const [{pageSize, showArchived}, setListOptions] = useLocalStorage<{ pageSize: number, showArchived: boolean }>("scm.landingPagePlugin.listOptions", { pageSize: 10, showArchived: true });
  return {
    pageSize,
    showArchived,
    setListOptions
  };
}

export function useDisabledCategories() {
  const [disabledCategories, setDisabledCategories] = useLocalStorage<Array<string>>("scm.landingPagePlugin.disabledCategories", []);
  const isDisabled = (category: string) => disabledCategories.includes(category);
  const setCategories: (categories: { [key: string]: boolean }) => void = (categories) => {
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
