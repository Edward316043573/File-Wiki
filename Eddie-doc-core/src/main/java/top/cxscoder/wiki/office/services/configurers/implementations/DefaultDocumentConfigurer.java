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

package top.cxscoder.wiki.office.services.configurers.implementations;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import top.cxscoder.wiki.domain.entity.WikiPage;
import top.cxscoder.wiki.enums.Extends;
import top.cxscoder.wiki.office.documentserver.managers.document.DocumentManager;
import top.cxscoder.wiki.office.documentserver.models.filemodel.Document;
import top.cxscoder.wiki.office.documentserver.models.filemodel.Permission;
import top.cxscoder.wiki.office.documentserver.storage.FileStoragePathBuilder;
import top.cxscoder.wiki.office.documentserver.util.file.FileUtility;
import top.cxscoder.wiki.office.documentserver.util.service.ServiceConverter;
import top.cxscoder.wiki.office.services.configurers.DocumentConfigurer;
import top.cxscoder.wiki.office.services.configurers.wrappers.DefaultDocumentWrapper;

@Service
@Primary
public class DefaultDocumentConfigurer implements DocumentConfigurer<DefaultDocumentWrapper> {

    @Autowired
    private DocumentManager documentManager;

    @Autowired
    private FileStoragePathBuilder storagePathBuilder;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private ServiceConverter serviceConverter;

    public void configure(Document document, DefaultDocumentWrapper wrapper){  // define the document configurer
        WikiPage userFile = wrapper.getUserFile();

        String fileName = userFile.getName() + "." + Extends.getExtends(userFile.getEditorType());  // get the fileName parameter from the document wrapper
        Permission permission = wrapper.getPermission();  // get the permission parameter from the document wrapper

        document.setTitle(fileName);  // set the title to the document config
        document.setUrl(wrapper.getPreviewUrl());  // set the URL to download a file to the document config
        document.setFileType(fileUtility.getFileExtension(fileName).replace(".",""));  // set the file type to the document config
        document.getInfo().setFavorite(wrapper.getFavorite());  // set the favorite parameter to the document config

        String key =  serviceConverter.  // get the document key
                        generateRevisionId(userFile.getId().toString() + userFile.getUpdateTime().getTime());

        document.setKey(key);  // set the key to the document config
        document.setPermissions(permission);  // set the permission parameters to the document config
    }
}
