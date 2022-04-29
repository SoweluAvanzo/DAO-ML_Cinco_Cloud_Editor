export function getExtension(fileName: string): string {
	const extension = fileName.split('.').pop();
	return extension? extension: "";
}

export function getExtensionFrom(filePath: string): string {
	const fileName = getFileNameFrom(filePath);
	return getExtension(fileName);
}

export function getFileNameFrom(filePath: string): string {
	const platform = process.platform;
	let filename;
	if(platform == 'win32') {
		filename = filePath.split('\\').pop();
	} else {
		filename = filePath.split('/').pop();
	}
	return filename? filename: "";
}

export function isEmpty(text: string): boolean {
	return text.trim().length === 0;
}
