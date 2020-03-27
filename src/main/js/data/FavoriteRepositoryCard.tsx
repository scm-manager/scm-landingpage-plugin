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
import React from "react";
import { binder } from "@scm-manager/ui-extensions";
import { MyDataComponent, MyDataType } from "../types";
import { RepositoryEntry } from "@scm-manager/ui-components";
import { Repository } from "@scm-manager/ui-types";
import styled from "styled-components";

type FavoriteRepositoryType = MyDataType & {
  repository: Repository;
};

const RepositoryEntryWrapper = styled.div`
  height: 110px;
  .overlay-column {
    width: calc(50% - 3rem);

    @media screen and (max-width: 768px) {
      width: calc(100% - 1.5rem);
    }
  }
`;

const FavoriteRepositoryCard: MyDataComponent<FavoriteRepositoryType> = ({ data }) => {
  return (
    <div className={"is-multiline"}>
      <RepositoryEntryWrapper className="box box-link-shadow column is-clipped">
        <RepositoryEntry repository={data?.repository} />
      </RepositoryEntryWrapper>
    </div>
  );
};

FavoriteRepositoryCard.type = "FavoriteRepositoryData";

binder.bind("landingpage.myFavoriteRepository", FavoriteRepositoryCard);
