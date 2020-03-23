import React, { FC, useEffect, useState } from "react";
import CollapsibleContainer from "./CollapsibleContainer";
import { useTranslation } from "react-i18next";
import { apiClient, ErrorNotification, Loading } from "@scm-manager/ui-components";
import MyTask from "./MyTask";

type Props = {};

const MyTasks: FC<Props> = ({}) => {
  const [t] = useTranslation("plugins");
  const [content, setContent] = useState([]);
  const [error, setError] = useState(undefined);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    apiClient
      .get("/landingpage/mytasks")
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
    <CollapsibleContainer title={t("scm-landingpage-plugin.mytasks.title")}>
      {content?._embedded?.tasks.map((task,key) => <MyTask key={key} task={task} />)}
    </CollapsibleContainer>
  );
};

export default MyTasks;
