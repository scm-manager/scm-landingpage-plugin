import React, { FC } from "react";
import { useIndexLink } from "@scm-manager/ui-api";
import { useQuery } from "react-query";
import { ErrorNotification, Icon, Loading } from "@scm-manager/ui-components";
import { useCollapsedState, useIsCategoryDisabled } from "../config/hooks";
import { Link, LoginInfo as LoginInfoResponse } from "@scm-manager/ui-types";
import CollapsibleContainer from "../CollapsibleContainer";
import { useTranslation } from "react-i18next";
import { stringifyUrl } from "query-string";
import classNames from "classnames";
import styled from "styled-components";

const withSourceQueryParam = (url: string) => stringifyUrl({ url, query: { source: "landingpage-feature-tile" } });

const StyledAnchor = styled.a`
  color: inherit;
`;

const MyTips: FC = () => {
  const [t] = useTranslation("plugins");
  const loginInfoLink = useIndexLink("loginInfo");
  const disabled = useIsCategoryDisabled("mytips");
  const { error, isLoading, data: loginInfo } = useQuery<LoginInfoResponse, Error>(
    ["loginInfo"],
    () => fetch(loginInfoLink!).then(response => response.json()),
    {
      enabled: !disabled && !!loginInfoLink,
      refetchOnWindowFocus: false
    }
  );
  const [collapsed, setCollapsed] = useCollapsedState("mytips");

  if (!loginInfoLink || disabled) {
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
