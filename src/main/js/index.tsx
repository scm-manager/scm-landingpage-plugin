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
