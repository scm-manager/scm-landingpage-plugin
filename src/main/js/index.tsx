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
import { binder } from "@scm-manager/ui-extensions";
import FavoriteRepositoryToggleIcon from "./FavoriteRepositoryToggleIcon";
import Home from "./Home";
import React, { FC } from "react";
import { PrimaryNavigationLink, ProtectedRoute } from "@scm-manager/ui-components";
import { useTranslation } from "react-i18next";
import "./tasks/PluginUpdateTask";
import "./data/FavoriteRepositoryCard";
import "./events/RepositoryPushEvent";
import "./data/MyFavoriteRepositoriesData";
import { RepositoryDataType } from "./types";
import { Links } from "@scm-manager/ui-types";

type HomeRouteProps = {
  authenticated: boolean;
  links: Links;
};

const HomeRoute: FC<HomeRouteProps> = props => {
  return (
    <ProtectedRoute authenticated={props.authenticated} path={"/home"} component={() => <Home links={props.links} />} />
  );
};

const HomeNavigation: FC = () => {
  const [t] = useTranslation("plugins");
  return <PrimaryNavigationLink label={t("scm-landingpage-plugin.navigation.home")} to={"/home"} match={"/home"} />;
};

const LargeToggleIcon: FC<RepositoryDataType> = props => (
  <FavoriteRepositoryToggleIcon repository={props.repository} classes={"fa-2x"} />
);

binder.bind("repository.card.beforeTitle", FavoriteRepositoryToggleIcon);
binder.bind("repository.afterTitle", LargeToggleIcon);
binder.bind("main.route", HomeRoute);
binder.bind("main.redirect", () => "/home");
binder.bind("primary-navigation.first-menu", HomeNavigation);
