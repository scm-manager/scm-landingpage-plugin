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
import React, { FC } from "react";
import { useTranslation } from "react-i18next";
import { useIndexLinks } from "@scm-manager/ui-api";
import { Loading, Notification } from "@scm-manager/ui-components";
import { binder } from "@scm-manager/ui-extensions";
import CollapsibleContainer from "../CollapsibleContainer";
import { Link } from "@scm-manager/ui-types";
import ScrollContainer from "../ScrollContainer";
import { useMyData } from "./useMyData";
import { useCollapsedState, useIsCategoryDisabled } from "../config/hooks";
import { MyDataExtension, MyDataType } from "../types";

type MyDataExtensionProps = {
  extension: MyDataExtension["type"];
  data?: MyDataType[];
  error?: Error | null;
};

const MyDataExtensionContainer: FC<MyDataExtensionProps> = ({ extension, data, error }) => {
  const [t] = useTranslation("plugins");
  const disabled = useIsCategoryDisabled(extension.type);
  const [collapsed, setCollapsed] = useCollapsedState(extension.type);

  if (disabled || (data?.length === 0 && !extension.emptyMessage)) {
    return null;
  } else {
    return (
      <CollapsibleContainer
        title={t(extension.title)}
        separatedEntries={extension.separatedEntries}
        emptyMessage={extension.emptyMessage}
        count={data?.length}
        initiallyCollapsed={collapsed}
        onCollapseToggle={setCollapsed}
        contentWrapper={ScrollContainer}
        error={error}
        category={extension.type}
      >
        {extension.beforeData}
        {data?.length ? (
          data?.map(extension.render)
        ) : (
          <Notification type="info">
            {t(extension.emptyMessage || "scm-landingpage-plugin.favoriteRepository.noData")}
          </Notification>
        )}
      </CollapsibleContainer>
    );
  }
};

const MyData: FC = () => {
  const links = useIndexLinks();
  const { data, error, isLoading } = useMyData((links?.landingpageData as Link)?.href);

  if (isLoading) {
    return <Loading />;
  }

  return (
    <>
      {binder.getExtensions<MyDataExtension>("landingpage.mydata").map(extension => (
        <MyDataExtensionContainer
          key={extension.type}
          extension={extension}
          data={data?._embedded.data.filter(entry => entry.type === extension.type)}
          error={error}
        />
      ))}
    </>
  );
};

export default MyData;
