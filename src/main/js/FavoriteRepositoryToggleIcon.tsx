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
import { apiClient } from "@scm-manager/ui-components";
import { Link, Repository } from "@scm-manager/ui-types";
import classNames from "classnames";
import styled from "styled-components";

type Props = {
  repository: Repository;
  classes?: string;
};

const Icon = styled.i`
  pointer-events: all;
  cursor: pointer;
`;

const FavoriteRepositoryToggleIcon: FC<Props> = ({ repository, classes }) => {
  const [favorite, setFavorite] = useState(false);

  useEffect(() => {
    setFavorite(!!(repository?._links?.unfavorize as Link)?.href);
  }, []);

  const getLink = () => {
    return favorite
      ? (repository?._links?.favorites as Link[])?.filter(link => link.name === "unfavorize")[0].href
      : (repository?._links?.favorites as Link[])?.filter(link => link.name === "favorize")[0].href;
  };

  const toggleFavoriteStatus = () => {
    apiClient.post(getLink()).then(() => setFavorite(!favorite));
  };

  return (
    <Icon
      className={classNames(classes, favorite ? "fas fa-star has-text-warning " : "far fa-star has-text-dark")}
      onClick={() => toggleFavoriteStatus()}
    />
  );
};

export default FavoriteRepositoryToggleIcon;
