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
import React, { FC, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { apiClient, ErrorNotification, Loading } from "@scm-manager/ui-components";
import styled from "styled-components";
import { MyDataEntriesType } from "../types";
import { ExtensionPoint } from "@scm-manager/ui-extensions";

const Headline = styled.h3`
  font-size: 1.25rem;
  margin-bottom: 1rem;
  & small {
    font-size: 0.875rem;
  }
`;

type Props = {};

const MyData: FC<Props> = ({}) => {
  const [t] = useTranslation("plugins");
  const [content, setContent] = useState<MyDataEntriesType>({ _embedded: { data: [] }});
  const [error, setError] = useState(undefined);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    apiClient
      .get("/landingpage/mydata")
      .then(r => r.json())
      .then(setContent)
      .then(() => setLoading(false))
      .catch(setError);
  }, []);

  if (loading) {
    return <Loading />;
  }

  if (error) {
    return <ErrorNotification error={error} />;
  }

  return (
    <>
      <Headline>{t("scm-landingpage-plugin.mydata.title")}</Headline>
      <ExtensionPoint name={"landingpage.mydata"} props={content} />
    </>
  );
};

export default MyData;
