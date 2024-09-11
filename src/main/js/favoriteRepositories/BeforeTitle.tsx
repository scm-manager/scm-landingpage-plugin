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
import { FavoriteRepository } from "./favoriteRepository";
import { Repository } from "@scm-manager/ui-types";
import styled from "styled-components";
import FavoriteRepositoryToggleIcon from "./FavoriteRepositoryToggleIcon";

type Props = {
  repository: Repository;
};

const NamespaceAddition = styled.span`
  color: var(--scm-secondary-color);
`;

const isFavoriteRepository = (repository: Repository): repository is FavoriteRepository => {
  return "showNamespace" in repository;
};

const BeforeTitle: FC<Props> = ({ repository }) => {
  const namespace =
    isFavoriteRepository(repository) && repository.showNamespace ? (
      <NamespaceAddition>{repository.namespace}/</NamespaceAddition>
    ) : null;

  return (
    <>
      <FavoriteRepositoryToggleIcon repository={repository} />
      {namespace}
    </>
  );
};

export default BeforeTitle;
