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
import styled from "styled-components";
import { Link, Repository } from "@scm-manager/ui-types";
import { apiClient, Icon } from "@scm-manager/ui-components";

type Props = {
  repository: Repository;
  classes?: string;
};

const SpanWithPointer = styled.span`
  pointer-events: all;
  cursor: pointer;
`;

const FavoriteRepositoryToggleIcon: FC<Props> = ({ repository, classes }) => {
  const [t] = useTranslation("plugins");
  const [favorite, setFavorite] = useState(false);
  const [link, setLink] = useState("");

  useEffect(() => {
    setFavorite(!!repository?._links?.unfavorize);
    setLink(getLink(repository));
  }, []);

  const getLink: (repository: Repository) => string = (repository: Repository) => {
    const currentLink = (repository?._links?.unfavorize || repository?._links?.favorize) as Link;
    return currentLink.href;
  };

  const toggleFavoriteStatus = () => {
    apiClient
      .post(link)
      .then(() => apiClient.get((repository._links.self as Link).href))
      .then(response => response.json())
      .then(updatedRepository => {
        setFavorite(!favorite);
        setLink(getLink(updatedRepository));
      });
  };

  return (
    <SpanWithPointer onClick={() => toggleFavoriteStatus()}>
      <Icon
        title={
          favorite
            ? t("scm-landingpage-plugin.favoriteRepository.unstar")
            : t("scm-landingpage-plugin.favoriteRepository.star")
        }
        iconStyle={favorite ? "fas" : "far"}
        name="star"
        color={favorite ? "warning" : "dark"}
        className={classes}
      />
    </SpanWithPointer>
  );
};

export default FavoriteRepositoryToggleIcon;
