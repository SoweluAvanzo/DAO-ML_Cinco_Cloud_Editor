
/* eslint-disable header/header */

import { FileService, TextFileContent } from '@theia/filesystem/lib/browser/file-service';
import { inject } from '@theia/core/shared/inversify';
import URI from '@theia/core/lib/common/uri';
import { fileService } from '../menu-command-removal-contribution';
import { Path } from '@theia/core';

export class FileHelper {
    @inject(FileService) private readonly fileService: FileService;
    private readonly SIZE_LIMIT = 1048576;

    constructor() {
        this.fileService = fileService;
    }

    /**
     * @param uriString uri to the file as a string
     * @returns content of the referenced file (only up to 1MB files)
     */
    async resolveUriString(uriString: string): Promise<string | undefined> {
        const uri = this.cleanURI(uriString);
        return this.resolveUri(uri);
    }

    /**
     * @param uri uri to the file
     * @returns content of the referenced file (only up to 1MB files)
     */
    async resolveUri(uri: URI): Promise<string | undefined> {
        let resource: TextFileContent | undefined;
        try {
            if (this.fileService.canHandleResource(uri)) {
                const fileStream = (await this.fileService.readFileStream(uri, {
                    length: this.SIZE_LIMIT
                }));
                const size = fileStream.size;
                if (size <= this.SIZE_LIMIT) { // readfiles to as size of 1MB
                    resource = await this.fileService.read(uri);
                }
            }
        } catch (e) {
            console.log(e);
        }
        if (!resource) {
            return undefined;
        }
        // probably checksize on large files
        // resource.size
        return this.resolveFileContent(resource);
    }

    private async resolveFileContent(resource: TextFileContent): Promise<string> {
        const content = resource.value;
        return content;
    }

    cleanURI(uriString: string): URI {
        const path = (new Path(uriString));
        return new URI(path.toString());
    }
}
