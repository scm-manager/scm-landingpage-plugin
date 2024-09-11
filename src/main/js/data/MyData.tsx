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

import React, { FC, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { useIndexLinks } from "@scm-manager/ui-api";
import { Loading, Notification } from "@scm-manager/ui-components";
import { binder } from "@scm-manager/ui-extensions";
import CollapsibleContainer from "../CollapsibleContainer";
import { Link } from "@scm-manager/ui-types";
import ScrollContainer from "../ScrollContainer";
import { useMyData } from "./useMyData";
import { useCollapsedState, useDisabledCategories, useIsCategoryDisabled } from "../config/hooks";
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

const MyData: FC<{ extensions: Array<MyDataExtension["type"]> }> = ({ extensions }) => {
  const links = useIndexLinks();
  const { data, error, isLoading } = useMyData((links?.landingpageData as Link)?.href);

  if (isLoading) {
    return <Loading />;
  }

  return (
    <>
      {extensions.map(extension => (
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

const MyDataWrapper: FC = () => {
  const extensions = binder.getExtensions<MyDataExtension>("landingpage.mydata");
  const { isDisabled } = useDisabledCategories();
  const allDisabled = useMemo(() => extensions.some(extension => isDisabled(extension.type)), [extensions, isDisabled]);
  if (allDisabled) {
    return null;
  }
  return <MyData extensions={extensions} />;
};

export default MyDataWrapper;
