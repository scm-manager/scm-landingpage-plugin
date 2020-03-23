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
import FavouriteRepositoryToggleIcon from "./FavouriteRepositoryToggleIcon";
import Home from "./Home";
import React, { FC } from "react";
import { ProtectedRoute, PrimaryNavigationLink } from "@scm-manager/ui-components";
import { useTranslation } from "react-i18next";
import "./PluginUpdateTask";

const HomeRoute: FC = props => {
  return <ProtectedRoute {...props} path={"/home"} component={Home} />;
};

const HomeNavigation: FC = () => {
  const [t] = useTranslation("plugins");
  return <PrimaryNavigationLink label={t("scm-landingpage-plugin.navigation.home")} to={"/home"} match={"/home"} />;
};

binder.bind("repository.card.beforeTitle", FavouriteRepositoryToggleIcon);
binder.bind("main.route", HomeRoute);
binder.bind("main.redirect", () => "/home");
binder.bind("primary-navigation.first-menu", HomeNavigation);
