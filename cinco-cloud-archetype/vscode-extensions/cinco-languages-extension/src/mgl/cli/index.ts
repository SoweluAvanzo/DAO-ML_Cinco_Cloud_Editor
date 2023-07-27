import chalk from 'chalk';
import { Command } from 'commander';
import { MglModel } from '../../generated/ast';
import { MglLanguageMetaData } from '../../generated/module';
import { createMglServices } from '../language-server/mgl-module';
import { extractAstNode } from './cli-util';
import { generateMetaSpecification } from './generator';
import { NodeFileSystem } from 'langium/node';

export const generateAction = async (fileName: string, opts: GenerateOptions): Promise<void> => {
    console.log(chalk.green(`Starting generation for currently opened MGL...`));
    const services = createMglServices(NodeFileSystem).Mgl;
    const model = await extractAstNode<MglModel>(fileName, services);
    const generatedFilePath = await generateMetaSpecification(model, fileName, opts.destination);
    console.log(chalk.green(`JavaScript code generated successfully: ${generatedFilePath}`));
};

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
