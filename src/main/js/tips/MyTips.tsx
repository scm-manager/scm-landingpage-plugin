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

import React, { FC } from "react";
import { Icon, Loading } from "@scm-manager/ui-components";
import { useCollapsedState } from "../config/hooks";
import { Link } from "@scm-manager/ui-types";
import CollapsibleContainer from "../CollapsibleContainer";
import { useTranslation } from "react-i18next";
import { stringifyUrl } from "query-string";
import classNames from "classnames";
import styled from "styled-components";
import { useLoginInfo } from "@scm-manager/ui-api";

const withSourceQueryParam = (url?: string) =>
  url && stringifyUrl({ url, query: { source: "landingpage-feature-tile" } });

const StyledAnchor = styled.a`
  color: inherit;
`;

const MyTips: FC = () => {
  const [t] = useTranslation("plugins");
  const [collapsed, setCollapsed] = useCollapsedState("mytips");
  const { data: loginInfo, error, isLoading } = useLoginInfo();

  if (isLoading) {
    return <Loading />;
  }

  if (!loginInfo?.feature && !error) {
    return null;
  }

  return (
    <CollapsibleContainer
      title={t("scm-landingpage-plugin.mytips.title")}
      separatedEntries={false}
      count={1}
      initiallyCollapsed={collapsed}
      onCollapseToggle={setCollapsed}
      error={error}
      category="mytips"
    >
      <StyledAnchor
        href={withSourceQueryParam((loginInfo?.feature?._links.self as Link | undefined)?.href)}
        className="p-2 media has-hover-background-blue"
      >
        <figure className="media-left mr-2 mt-1">
          <Icon name="lightbulb" className="fa-lg fa-fw" color="inherit" />
        </figure>
        <div
          className={classNames(
            "media-content",
            "text-box",
            "is-flex",
            "is-flex-direction-column",
            "is-justify-content-space-around",
            "is-align-self-stretch"
          )}
        >
          <strong className="is-marginless">{loginInfo?.feature?.title}</strong>
          <small>{loginInfo?.feature?.summary}</small>
        </div>
      </StyledAnchor>
    </CollapsibleContainer>
  );
};

export default MyTips;
