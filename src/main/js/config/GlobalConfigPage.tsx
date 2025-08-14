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
import { useTranslation } from "react-i18next";

const GlobalConfigPage: FC<{ link: string }> = ({ link }) => {
  const [t] = useTranslation("plugins");
  const validateMyEventsStoreSize = (value: string) => {
    const storeSize = Number(value);

    if (!Number.isInteger(storeSize)) {
      return t("scm-landingpage-plugin.globalConfig.form.myEventsStoreSize.notInteger");
    }

    if (storeSize < 1) {
      return t("scm-landingpage-plugin.globalConfig.form.myEventsStoreSize.notPositive");
    }
  };

  return (
    <>
      <ConfigurationForm<LandingpageConfiguration>
        link={link}
        translationPath={["plugins", "scm-landingpage-plugin.globalConfig.form"]}
      >
        <Form.Row className="mb-2">
          <Form.Input name="instanceName" className="pb-0" rules={{ required: true }} />
        </Form.Row>
        <p className="mb-4">{t("scm-landingpage-plugin.globalConfig.reloadHint")}</p>
        <Form.Row>
          <Form.Input
            name="myEventsStoreSize"
            type="number"
            rules={{ required: true, validate: validateMyEventsStoreSize }}
          />
        </Form.Row>
        <Form.Row>
          <Form.Input name="myEventsCleanupExpression" rules={{ required: true }} />
        </Form.Row>
      </ConfigurationForm>
    </>
  );
};

export default GlobalConfigPage;
