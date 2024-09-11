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
import { LandingpageConfiguration } from "../types";
import { ConfigurationForm, Form } from "@scm-manager/ui-forms";
import { Title } from "@scm-manager/ui-components";
import { useTranslation } from "react-i18next";

const GlobalConfigPage: FC<{ link: string }> = ({ link }) => {
  const [t] = useTranslation("plugins");

  return (
    <>
      <Title title={t("scm-landingpage-plugin.globalConfig.title")} />
      <ConfigurationForm<LandingpageConfiguration>
        link={link}
        translationPath={["plugins", "scm-landingpage-plugin.globalConfig.form"]}
      >
        <Form.Row>
          <Form.Input name="instanceName" />
        </Form.Row>
        <p>{t("scm-landingpage-plugin.globalConfig.reloadHint")}</p>
      </ConfigurationForm>
    </>
  );
};

export default GlobalConfigPage;
