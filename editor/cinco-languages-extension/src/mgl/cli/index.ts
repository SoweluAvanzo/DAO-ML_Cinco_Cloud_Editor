/********************************************************************************
 * Copyright (c) 2024 Cinco Cloud.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
import { Command } from 'commander';
import { LanguageMetaData , mglLoader } from '@cinco-glsp/cinco-languages';
import { getLanguageFolder, saveToFolder, uploadToMinio } from './persistence_handler';
import * as vscode from 'vscode';

export enum LanguageJobMode {
    UPLOAD,
    GENERATE,
    UPLOAD_AND_GENERATE
}

export const languageHandlingAction = async (filePath: string, opts: GenerateOptions, mode?: LanguageJobMode): Promise<void> => {
    const prefix =
        mode === LanguageJobMode.GENERATE
            ? 'Generation'
            : mode === LanguageJobMode.UPLOAD
              ? 'Upload'
              : mode === LanguageJobMode.UPLOAD_AND_GENERATE
                ? 'Generate and Upload'
                : undefined;
    console.log('Starting generation for currently opened MGL...');

    const languageJobs: LanguageJob[] = [];
    if (mode === LanguageJobMode.GENERATE || mode === LanguageJobMode.UPLOAD_AND_GENERATE) {
        languageJobs.push({
            message: 'Reading currently opened MGL...',
            callable: async errorDisplay => {
                const generatedMetaSpecification = await mglLoader.loadLanguage(filePath, opts);
                if (generatedMetaSpecification) {
                    const fileName = getFileName(filePath);
                    const specificationName = `${fileName}_spec.json`;
                    const languagesFolder = await getLanguageFolder(opts.destination);
                    await saveToFolder(generatedMetaSpecification, specificationName, languagesFolder).catch(e => {
                        errorDisplay('Generation failed with error:\n' + e);
                        throw Error('failed save generated files!');
                    });
                    //  update meta-specification in glsp-server
                    vscode.commands.executeCommand('cinco.meta-specification.reload');
                }
            }
        });
    }
    if (mode === LanguageJobMode.UPLOAD || mode === LanguageJobMode.UPLOAD_AND_GENERATE) {
        languageJobs.push({
            message: 'Uploading languages...',
            callable: async errorDisplay => {
                const languagesFolder = await getLanguageFolder(opts.destination);
                return uploadToMinio(languagesFolder, e => {
                    errorDisplay('Uploading failed with error:\n' + e);
                });
            }
        });
    }

    generationProgress('Generating', languageJobs, prefix + ' successful!', prefix + ' ended with errors!', prefix + ' canceled!');
};

export interface LanguageJob {
    message: string;
    callable: (errorDisplay: (message: string) => void) => Promise<void> | void;
}
export async function generationProgress(
    title: string,
    jobs: LanguageJob[],
    finishedMessage?: string,
    errorMessage?: string,
    cancelMessage?: string
): Promise<void> {
    vscode.window.withProgress(
        {
            location: vscode.ProgressLocation.Notification,
            title: title,
            cancellable: true
        },
        async (progress, token) => {
            token.onCancellationRequested(() => {
                if (cancelMessage) {
                    vscode.window.showInformationMessage(cancelMessage);
                }
                console.log('canceled progress');
            });

            let hadErrors = false;
            const errorDisplay = (errorMessage: string) => {
                if (errorMessage) {
                    vscode.window.showErrorMessage(errorMessage);
                }
                console.log('error in progress');
                hadErrors = true;
            };

            let leftJobs = jobs.length;
            for (const job of jobs) {
                const message = job.message;
                progress.report({ increment: 100 / leftJobs - 1, message: message });
                try {
                    await job.callable(errorDisplay);
                } catch (e) {
                    console.log('error in progress: ' + e);
                }
                if (hadErrors) {
                    break;
                }
                leftJobs -= 1;
            }
            if (hadErrors) {
                console.log('error in progress');
                if (errorMessage) {
                    vscode.window.showErrorMessage(errorMessage);
                }
            } else {
                if (finishedMessage) {
                    vscode.window.showInformationMessage(finishedMessage);
                }
                console.log('finished progress');
            }
        }
    );
}

/**
 * @param filePath path to a file or folder.
 * @returns returns only the last segment of the filePath, without file extension
 */
function getFileName(filePath: string): string {
    let fileName = filePath;
    if (fileName.lastIndexOf('/') > 0) {
        const lastSegment = fileName.lastIndexOf('/');
        fileName = fileName.slice(lastSegment + 1);
    }
    if (fileName.indexOf('.') > 0) {
        const index = fileName.indexOf('.');
        return fileName.substring(0, index);
    }
    return fileName;
}

export interface GenerateOptions {
    destination?: string;
}

export default function (): void {
    const program = new Command();

    program
        // eslint-disable-next-line @typescript-eslint/no-var-requires
        .version(require('../../../package.json').version);

    const fileExtensions = LanguageMetaData.MglLanguageMetaData.fileExtensions.join(', ');
    program
        .command('generate')
        .argument('<file>', `source file (possible file extensions: ${fileExtensions})`)
        .option('-d, --destination <dir>', 'destination directory of generating')
        .description('generates JavaScript code that prints "Hello, {name}!" for each greeting in a source file')
        .action(languageHandlingAction);

    program.parse(process.argv);
}
