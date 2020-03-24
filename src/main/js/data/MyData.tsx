import React, { FC, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import CollapsibleContainer from "../CollapsibleContainer";
import { apiClient, ErrorNotification, Loading, Subtitle } from "@scm-manager/ui-components";
import MyRepositoryData from "./MyRepositoryData";
import { MyDataType } from "../types";
import styled from "styled-components";

const Headline = styled.h3`
  font-size: 1.25rem;
  margin-bottom: 1rem;
  & small {
    font-size: 0.875rem;
  }
`;

type Props = {};

const MyData: FC<Props> = ({}) => {
  const [t] = useTranslation("plugins");
  const [content, setContent] = useState([]);
  const [error, setError] = useState(undefined);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    apiClient
      .get("/landingpage/mydata")
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

  console.log(content?._embedded?.data.filter((dataEntry: MyDataType) => dataEntry.type === "FavoriteRepositoryData"));
  console.log(content?._embedded?.data.forEach((dataEntry: MyDataType) => console.log(dataEntry)));
  return (
    <>
      <Headline>{t("scm-landingpage-plugin.mydata.title")}</Headline>
      <CollapsibleContainer title={t("scm-landingpage-plugin.favoriteRepository.title")}>
        {content?._embedded?.data
          .filter((dataEntry: MyDataType) => dataEntry.type === "FavoriteRepositoryData")
          .map((data, key) => {
            console.log(data);
            return <MyRepositoryData key={key} data={data} />;
          })}
      </CollapsibleContainer>
    </>
  );
};

export default MyData;
