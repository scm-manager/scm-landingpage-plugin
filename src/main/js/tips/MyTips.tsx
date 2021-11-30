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
import { ErrorNotification, Icon, Loading } from "@scm-manager/ui-components";
import { useCollapsedState, useIsCategoryDisabled } from "../config/hooks";
import { Link } from "@scm-manager/ui-types";
import CollapsibleContainer from "../CollapsibleContainer";
import { useTranslation } from "react-i18next";
import { stringifyUrl } from "query-string";
import classNames from "classnames";
import styled from "styled-components";
import { useLoginInfo } from "@scm-manager/ui-api";

const withSourceQueryParam = (url: string) => stringifyUrl({ url, query: { source: "landingpage-feature-tile" } });

const StyledAnchor = styled.a`
  color: inherit;
`;

const MyTips: FC = () => {
  const [t] = useTranslation("plugins");
  const disabled = useIsCategoryDisabled("mytips");
  const [collapsed, setCollapsed] = useCollapsedState("mytips");
  const { data: loginInfo, error, isLoading } = useLoginInfo(disabled);

  if (disabled) {
    return null;
  }

  if (error) {
    return <ErrorNotification error={error} />;
  }

  if (isLoading) {
    return <Loading />;
  }

  if (!loginInfo?.feature) {
    return null;
  }

  return (
    <CollapsibleContainer
      title={t("scm-landingpage-plugin.mytips.title")}
      separatedEntries={false}
      count={1}
      initiallyCollapsed={collapsed}
      onCollapseToggle={setCollapsed}
    >
      <StyledAnchor
        href={withSourceQueryParam((loginInfo.feature._links.self as Link).href)}
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
          <strong className="is-marginless">{loginInfo.feature.title}</strong>
          <small>{loginInfo.feature.summary}</small>
        </div>
      </StyledAnchor>
    </CollapsibleContainer>
  );
};

export default MyTips;
