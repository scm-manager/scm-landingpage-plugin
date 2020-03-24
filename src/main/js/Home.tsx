import React, { FC } from "react";
import { Page } from "@scm-manager/ui-components";
import MyTasks from "./tasks/MyTasks";
import { useTranslation } from "react-i18next";
import MyData from "./data/MyData";

const Home: FC = () => {
  const [t] = useTranslation("plugins");

  return (
    <Page title={t("scm-landingpage-plugin.home.title")} subtitle={t("scm-landingpage-plugin.home.subtitle")}>
      <div className="columns">
        <div className="column">
          <MyTasks />
          <MyData/>
        </div>
        <div className="column"></div>
      </div>
    </Page>
  );
};

export default Home;
