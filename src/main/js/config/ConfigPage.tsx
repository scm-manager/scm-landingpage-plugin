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

import { Checkbox, InputField, Page, SubmitButton } from "@scm-manager/ui-components";
import React, { FC, useState } from "react";
import { useTranslation } from "react-i18next";
import { MyDataExtension } from "../types";
import { useBinder } from "@scm-manager/ui-extensions";
import {
  isShowNamespaceInFavoriteOption,
  showNamespaceInFavoriteOptions,
  useDisabledCategories,
  useListOptions
} from "./hooks";
import { useHistory } from "react-router-dom";
import { Select } from "@scm-manager/ui-components";

type DisplayOptionProps = {
  label: string;
  value: boolean;
  toggle: (newValue: boolean) => void;
};

const DisplayOption: FC<DisplayOptionProps> = ({ value, toggle, label }) => {
  return <Checkbox checked={value} label={label} onChange={toggle} />;
};

const pageSizeMin = 5;
const pageSizeMax = 100;

const ConfigPage: FC = () => {
  const [t] = useTranslation("plugins");
  const { isDisabled, setCategories } = useDisabledCategories();
  const binder = useBinder();
  const [changedCategories, setChangedCategories] = useState<{ [key: string]: boolean }>({});
  const { showNamespace, pageSize, showArchived, setListOptions } = useListOptions();
  const [pageSizeInput, setPageSizeInput] = useState(pageSize);
  const [pageSizeError, setPageSizeError] = useState(false);
  const [optionsChanged, setOptionsChanged] = useState(false);
  const [showArchivedInput, setShowArchivedInput] = useState(showArchived);
  const [namespaceOption, setNamespaceOption] = useState(showNamespace);
  const extensions = binder.getExtensions<MyDataExtension>("landingpage.mydata");
  const history = useHistory();

  const changeExtension = (category: string) => (enabled: boolean) => {
    const newValue = { ...changedCategories, [category]: enabled };
    setChangedCategories(newValue);
  };

  const shouldBeEnabled = (category: string) => {
    return Object.keys(changedCategories).includes(category) ? changedCategories[category] : !isDisabled(category);
  };

  const changePageSize = (newPageSizeString: string) => {
    const newPageSize = Number(newPageSizeString);
    if (!isNaN(newPageSize)) {
      setPageSizeInput(newPageSize);
      setPageSizeError(newPageSize < pageSizeMin || newPageSize > pageSizeMax);
      setOptionsChanged(true);
    }
  };

  const changeShowArchived = (newValue: boolean) => {
    setShowArchivedInput(newValue);
    setOptionsChanged(true);
  };

  const submitChanges = () => {
    setCategories(changedCategories);
    setListOptions({
      pageSize: Number(pageSizeInput),
      showArchived: showArchivedInput,
      showNamespace: namespaceOption
    });
    setChangedCategories({});
    setOptionsChanged(false);
    setTimeout(() => history.push("/repos/"));
  };

  const changeNamespaceOption = (value: string) => {
    if (isShowNamespaceInFavoriteOption(value) && value !== namespaceOption) {
      setNamespaceOption(value);
      setOptionsChanged(true);
    }
  };

  return (
    <Page title={t("scm-landingpage-plugin.config.title")} subtitle={t("scm-landingpage-plugin.config.subtitle")}>
      <div className={"mb-4"}>
        {["favoriteRepository", "mytasks", "myevents", "mytips"].map(category => (
          <DisplayOption
            key={category}
            label={t(`scm-landingpage-plugin.${category}.title`)}
            value={shouldBeEnabled(category)}
            toggle={changeExtension(category)}
          />
        ))}
        {extensions.map(extension => (
          <DisplayOption
            key={extension.type}
            label={t(extension.title)}
            value={shouldBeEnabled(extension.type)}
            toggle={changeExtension(extension.type)}
          />
        ))}
        <DisplayOption
          label={t("scm-landingpage-plugin.config.showArchived")}
          value={showArchivedInput}
          toggle={changeShowArchived}
        />
        <Select
          defaultValue={namespaceOption}
          options={showNamespaceInFavoriteOptions.map(option => ({
            label: t(`scm-landingpage-plugin.config.select.option.${option}`),
            value: option
          }))}
          label={t("scm-landingpage-plugin.config.select.label")}
          onChange={changeNamespaceOption}
        />
        <InputField
          label={t("scm-landingpage-plugin.config.pageSize")}
          type={"number"}
          value={pageSizeInput}
          onChange={changePageSize}
          errorMessage={t("scm-landingpage-plugin.config.pageSizeError", { min: pageSizeMin, max: pageSizeMax })}
          validationError={pageSizeError}
        />
      </div>
      <div className={"level"}>
        <div className={"level-left"} />
        <SubmitButton
          className={"level-right"}
          label={t("scm-landingpage-plugin.config.submit")}
          action={submitChanges}
          disabled={(Object.keys(changedCategories).length === 0 && !optionsChanged) || pageSizeError}
        />
      </div>
    </Page>
  );
};

export default ConfigPage;
