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
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import styled from "styled-components";
import { binder } from "@scm-manager/ui-extensions";
import CollapsibleContainer from "../CollapsibleContainer";
import { Link, Links } from "@scm-manager/ui-types";
import { useMyData } from "./myData";

const Headline = styled.h3`
  font-size: 1.25rem;
  margin-bottom: 1rem;
  & small {
    font-size: 0.875rem;
  }
`;

type Props = {
  links: Links;
};

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

const MyData: FC<Props> = ({ links }) => {
  const [t] = useTranslation("plugins");
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
          <Box className="box">
            {dataForExtension?.map((dataEntry, key) => (
              <>
                {extension.render(dataEntry, key)} {key + 1 !== dataForExtension.length ? <Separator /> : null}
              </>
            ))}
          </Box>
        </CollapsibleContainer>
      );
    }
  };

  const extensions: ExtensionProps[] = binder.getExtensions("landingpage.mydata");

  if (error) {
    return <ErrorNotification error={error} />;
  }

  if (isLoading) {
    return <Loading />;
  }

  return (
    <>
      <Headline>{t("scm-landingpage-plugin.mydata.title")}</Headline>
      {extensions.map(renderExtension)}
    </>
  );
};

export default MyData;
