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
import React, { FC, ReactElement } from "react";
import { useTranslation } from "react-i18next";
import { useIndexLinks } from "@scm-manager/ui-api";
import { ErrorNotification, Loading, Notification } from "@scm-manager/ui-components";
import styled from "styled-components";
import { binder } from "@scm-manager/ui-extensions";
import CollapsibleContainer from "../CollapsibleContainer";
import { Link } from "@scm-manager/ui-types";
import { useMyData } from "./useMyData";

export type ExtensionProps = {
  title: string;
  render: (data: any, key: any) => ReactElement;
  separatedEntries: boolean;
  type: string;
  emptyMessage?: string;
};

const Separator = styled.div`
  border-bottom: 1px solid rgb(219, 219, 219, 0.5);
  margin: 0 1rem;
`;

const Box = styled.div`
  padding: 0.5rem;
`;

const MyData: FC = () => {
  const [t] = useTranslation("plugins");
  const links = useIndexLinks();
  const { data, error, isLoading } = useMyData((links?.landingpageData as Link)?.href);

  const renderExtension: (extension: ExtensionProps) => any = extension => {
    const dataForExtension = data?._embedded.data.filter(entry => entry.type === extension.type);

    if (dataForExtension?.length === 0 && !extension.emptyMessage) {
      return null;
    } else {
      return (
        <CollapsibleContainer
          title={t(extension.title)}
          separatedEntries={extension.separatedEntries}
          emptyMessage={extension.emptyMessage}
        >
          {(dataForExtension?.length || 0) > 0 ? (
            <Box className="box">
              {dataForExtension?.map((dataEntry, key) => (
                <>
                  {extension.render(dataEntry, key)} {key + 1 !== dataForExtension.length ? <Separator /> : null}
                </>
              ))}
            </Box>
          ) : (
            <Notification>{t("scm-landingpage-plugin.favoriteRepository.noData")}</Notification>
          )}
        </CollapsibleContainer>
      );
    }
  };

  const extensions: ExtensionProps[] = binder.getExtensions("landingpage.mydata");

  if (!extensions.length) {
    return null;
  }

  if (error) {
    return <ErrorNotification error={error} />;
  }

  if (isLoading) {
    return <Loading />;
  }

  return <>{extensions.map(renderExtension)}</>;
};

export default MyData;
