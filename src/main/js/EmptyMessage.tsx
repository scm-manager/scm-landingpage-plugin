import React, { FC } from "react";
import { useTranslation } from "react-i18next";
import { Notification } from "@scm-manager/ui-components";
import styled from "styled-components";

type Props = {
  messageKey?: string;
};

const Container = styled.div`
  margin: 1.25rem 0;
`;

const EmptyMessage: FC<Props> = ({ messageKey }) => {
  const [t] = useTranslation("plugins");
  return (
    <Container>
      <Notification type="info">{t(messageKey!)}</Notification>
    </Container>
  );
};

EmptyMessage.defaultProps = {
  messageKey: "scm-landingpage-plugin.emptyMessage.default"
};

export default EmptyMessage;
