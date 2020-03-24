import React from "react";
import { binder, ExtensionPoint } from "@scm-manager/ui-extensions";
import { MyDataComponent, MyDataType } from "../types";
import { useTranslation } from "react-i18next";
import { CardColumn } from "@scm-manager/ui-components";
import { Repository, Link } from "@scm-manager/ui-types";

type FavoriteRepositoryType = MyDataType & {
  repository: Repository;
};

const FavoriteRepositoryCard: MyDataComponent<FavoriteRepositoryType> = ({ data }) => {
  const [t] = useTranslation("plugins");

  const getLogo = () => (
    <p className="image is-64x64">
      <ExtensionPoint
        name="repos.repository-avatar"
        props={{
          repository: data.repository
        }}
      >
        <Image src="/images/blib.jpg" alt="Logo" />
      </ExtensionPoint>
    </p>
  );

  return (
    <>
      <CardColumn
        title={data.repository.name}
        link={(data?._links?.self as Link)?.href}
        avatar={getLogo()}

      ></CardColumn>
    </>
  );
};

FavoriteRepositoryCard.type = "FavoriteRepositoryData";

binder.bind("landingpage.mytask", FavoriteRepositoryCard);
