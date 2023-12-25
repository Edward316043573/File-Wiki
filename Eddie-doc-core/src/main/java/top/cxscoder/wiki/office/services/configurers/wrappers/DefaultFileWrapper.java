/**
 *
 * (c) Copyright Ascensio System SIA 2021
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package top.cxscoder.wiki.office.services.configurers.wrappers;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import top.cxscoder.wiki.domain.entity.WikiPageFile;
import top.cxscoder.wiki.office.documentserver.models.enums.Action;
import top.cxscoder.wiki.office.documentserver.models.enums.Type;
import top.cxscoder.wiki.office.entities.User;

@Getter
@Builder
@Setter
public class DefaultFileWrapper {
    private WikiPageFile userFile;
    private Type type;
    private User user;
    private String lang;
    private Action action;
    private String actionData;
    private Boolean canEdit;
}
