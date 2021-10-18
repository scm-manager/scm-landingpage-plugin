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

import { useState } from "react";

const CONFIG_LOCAL_STORAGE_KEY = "scm.landing-page.config";

export type Config = {
  disabledCategories: string[];
  collapsedState: {
    [category: string]: boolean;
  };
};

function useLocalStorage(): [config: Config, setConfig: (value: Config) => void] {
  const [config, setConfig] = useState<Config>(() => {
    try {
      const item = window.localStorage.getItem(CONFIG_LOCAL_STORAGE_KEY);
      return item ? JSON.parse(item) : {
        disabledCategories: [],
        collapsedState: {}
      } as Config;
    } catch (error) {
      console.error(error);
      return {};
    }
  });

  const updateConfig = (value: Config) => {
    try {
      setConfig(value);
      window.localStorage.setItem(CONFIG_LOCAL_STORAGE_KEY, JSON.stringify(value));
    } catch (error) {
      console.log(error);
    }
  };

  return [config, updateConfig];
}

export function useConfig() {
  const [config, setConfig] = useLocalStorage();

  const isDisplayed = (key: string) => !config.disabledCategories.includes(key);
  const getCollapsedState = (key: string): boolean | undefined => config.collapsedState[key];

  const toggleDisplayed = (key: string) => {
    if (isDisplayed(key)) {
      setConfig({...config, disabledCategories: [...config.disabledCategories, key]});
    } else {
      setConfig({...config, disabledCategories: config.disabledCategories.filter(dt => dt !== key)});
    }
  };

  const setCollapsed = (key: string, collapsed: boolean) => {
    const newConfig = config;
    newConfig.collapsedState[key] = collapsed;
    setConfig(newConfig);
  };

  return {
    isDisplayed,
    getCollapsedState,
    toggleDisplayed,
    setCollapsed,
    disabledCategories: config.disabledCategories
  }
}
