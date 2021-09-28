export class GraphModelFile {
	id: string;
	modelType: string;
	fileExtension: string;

	constructor(
		id: string,
		modelType: string,
		fileExtension: string
	) {
		this.id= id;
		this.modelType = modelType;
		this.fileExtension = fileExtension;
	}
}