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

import { Checkbox, Page } from "@scm-manager/ui-components";
import React, { FC } from "react";
import { useTranslation } from "react-i18next";
import { ExtensionProps } from "../data/MyData";
import { useBinder } from "@scm-manager/ui-extensions";
import { useDisabledCategories } from "./hooks";

type DisplayOptionProps = {
  label: string;
  value: boolean;
  toggle: () => void;
};

const DisplayOption: FC<DisplayOptionProps> = ({ value, toggle, label }) => {
  return <Checkbox checked={value} label={label} onChange={toggle} />;
};

const ConfigPage: FC = () => {
  const [t] = useTranslation("plugins");
  const { isDisabled, toggleDisabled } = useDisabledCategories();
  const binder = useBinder();
  const extensions: ExtensionProps[] = binder.getExtensions("landingpage.mydata");

  return (
    <Page title={t("scm-landingpage-plugin.config.title")} subtitle={t("scm-landingpage-plugin.config.subtitle")}>
      <DisplayOption
        label={t("scm-landingpage-plugin.favoriteRepository.title")}
        value={isDisabled("favoriteRepository")}
        toggle={() => toggleDisabled("favoriteRepository")}
      />
      <DisplayOption
        label={t("scm-landingpage-plugin.mytasks.title")}
        value={isDisabled("mytasks")}
        toggle={() => toggleDisabled("mytasks")}
      />
      <DisplayOption
        label={t("scm-landingpage-plugin.myevents.title")}
        value={isDisabled("myevents")}
        toggle={() => toggleDisabled("myevents")}
      />
      {extensions.map(extension => (
        <DisplayOption
          label={t(extension.title)}
          value={isDisabled(extension.type)}
          toggle={() => toggleDisabled(extension.type)}
        />
      ))}
    </Page>
  );
};

export default ConfigPage;
