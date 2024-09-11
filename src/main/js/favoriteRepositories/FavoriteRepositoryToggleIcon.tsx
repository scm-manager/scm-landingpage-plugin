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
import { useTranslation } from "react-i18next";
import styled from "styled-components";
import { Repository } from "@scm-manager/ui-types";
import { Icon } from "@scm-manager/ui-components";
import { useFavoriteRepository } from "./favoriteRepository";

type Props = {
  repository: Repository;
  classes?: string;
};

const SpanWithPointer = styled.span`
  pointer-events: all;
  cursor: pointer;
  margin-right: 0.25rem;
  z-index: 1;
`;

const FavoriteRepositoryToggleIcon: FC<Props> = ({ repository, classes }) => {
  const [t] = useTranslation("plugins");
  const { favorize, unfavorize } = useFavoriteRepository(repository);

  const toggleFavoriteStatus = () => {
    if (favorize) {
      favorize();
    }
    if (unfavorize) {
      unfavorize();
    }
  };

  return (
    <SpanWithPointer onClick={() => toggleFavoriteStatus()}>
      <Icon
        title={
          unfavorize
            ? t("scm-landingpage-plugin.favoriteRepository.unstar")
            : t("scm-landingpage-plugin.favoriteRepository.star")
        }
        iconStyle={unfavorize ? "fas" : "far"}
        name="star"
        color={unfavorize ? "warning" : "secondary-more"}
        className={classes}
      />
    </SpanWithPointer>
  );
};

export default FavoriteRepositoryToggleIcon;
