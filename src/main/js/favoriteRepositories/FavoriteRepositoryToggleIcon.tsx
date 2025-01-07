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

import React, { FC, useState } from "react";
import { useTranslation } from "react-i18next";
import styled from "styled-components";
import { Repository } from "@scm-manager/ui-types";
import { useFavoriteRepository } from "./favoriteRepository";
import { Icon } from "@scm-manager/ui-core";

type Props = {
  repository: Repository;
  classes?: string;
};

const ToggleButton = styled.button`
  pointer-events: all;
  cursor: pointer;
  z-index: 1;
  background: none;
  border: none;
  padding: 0;
`;

const FavoriteRepositoryToggleIcon: FC<Props> = ({ repository, classes }) => {
  const [t] = useTranslation("plugins");
  const { favorize, unfavorize } = useFavoriteRepository(repository);
  const [hovered, setHovered] = useState(false);
  const toggleHover = () => setHovered(!hovered);
  const toggleFavoriteStatus = () => {
    if (favorize) {
      favorize();
    }
    if (unfavorize) {
      unfavorize();
    }
  };

  return (
    <ToggleButton onMouseEnter={toggleHover} onMouseLeave={toggleHover} onClick={() => toggleFavoriteStatus()}>
      <>
        <span className="sr-only">
          {unfavorize
            ? t("scm-landingpage-plugin.favoriteRepository.unstar")
            : t("scm-landingpage-plugin.favoriteRepository.star")}
        </span>
        <Icon
          type={hovered ? "fas" : unfavorize ? "fas" : "far"}
          className={(unfavorize ? "has-text-warning " : "has-text-secondary-more ") + classes}
        >
          {hovered ? "star-half-alt" : "star"}
        </Icon>
      </>
    </ToggleButton>
  );
};
export default FavoriteRepositoryToggleIcon;
