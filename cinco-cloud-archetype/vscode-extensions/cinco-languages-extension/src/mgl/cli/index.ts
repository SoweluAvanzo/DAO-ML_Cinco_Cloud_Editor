import chalk from 'chalk';
import { Command } from 'commander';
import { MglModel } from '../../generated/ast';
import { MglLanguageMetaData } from '../../generated/module';
import { createMglServices } from '../language-server/mgl-module';
import { extractAstNode } from './cli-util';
import { MGLGenerator } from './generator';
import { NodeFileSystem } from 'langium/node';
import { saveToLanguagesFolder, uploadToMinio } from './persistence_handler';

export const generateAction = async (filePath: string, opts: GenerateOptions, uploadMetaSpecification?: boolean): Promise<void> => {
    console.log(chalk.green(`Starting generation for currently opened MGL...`));
    const services = createMglServices(NodeFileSystem).Mgl;
    const model = await extractAstNode<MglModel>(filePath, services);
    const mglGenerator = new MGLGenerator();
    const generatedMetaSpecification = await mglGenerator.generateMetaSpecification(model, filePath, opts.destination);

    const fileName = getFileName(filePath)
    const specificationName = `${fileName}_spec.json`;
    saveToLanguagesFolder(generatedMetaSpecification, specificationName).then((languagesFolder) => {
        if(uploadMetaSpecification) {
            uploadToMinio(languagesFolder);
        }
    })
};

/**
 * @param filePath path to a file or folder.
 * @returns returns only the last segment of the filePath, without file extension
 */
function getFileName(filePath: string): string {
    let fileName = filePath;
    if(fileName.lastIndexOf('/') > 0) {
        const lastSegment = fileName.lastIndexOf('/');
        fileName = fileName.slice(lastSegment + 1);
    }
    if(fileName.indexOf('.') > 0) {
        const index = fileName.indexOf('.');
        return fileName.substring(0, index);
    }
    return fileName;
}

export type GenerateOptions = {
    destination?: string;
}

export default function(): void {
    const program = new Command();

    program
        // eslint-disable-next-line @typescript-eslint/no-var-requires
        .version(require('../../../package.json').version);

    const fileExtensions = MglLanguageMetaData.fileExtensions.join(', ');
    program
        .command('generate')
        .argument('<file>', `source file (possible file extensions: ${fileExtensions})`)
        .option('-d, --destination <dir>', 'destination directory of generating')
        .description('generates JavaScript code that prints "Hello, {name}!" for each greeting in a source file')
        .action(generateAction);

    program.parse(process.argv);
}
