import * as http from 'http';
import * as https from 'https';
import { INTERNAL_PYRO_HOST, INTERNAL_PYRO_PORT, INTERNAL_PYRO_SUBPATH, INTERNAL_USE_SSL } from "./env_var";
import { isEmpty } from './fileNameUtils';
import { PyroEditorProvider } from './pyroEditor';


export abstract class PyroApi {

	protected TOKEN: string | undefined;
	protected PROJECT_ID: number | undefined = undefined;

	private static async performRequest(httpOptions: http.RequestOptions,data?:any):Promise<any> {
		PyroEditorProvider.logging("REQUESTING:\n"+JSON.stringify(httpOptions));
		return new Promise((resolve, reject) => {
			const req = http.request(httpOptions,(response: http.IncomingMessage) => {
					if (response.statusCode != 200) {
						PyroEditorProvider.logging('REQUEST FAILED:\n'+httpOptions.hostname+'\n'+httpOptions.path+'\n'+httpOptions.port);
						PyroEditorProvider.logging('CODE: '+response.statusCode+" | MESSAGE: "+response.statusMessage);
						reject(new Error(response.statusMessage));
					}
					const chunks:any[] = [];
					response.on('data', (chunk: string) => {
						PyroEditorProvider.logging('RESPONSE-RECEIVED:\n'+chunk);
						chunks.push(chunk);
					});
					response.on('error',(e: string)=>PyroEditorProvider.logging("Process-Error:\n"+e));
					response.on('end', () => {
						const result = Buffer.concat(chunks).toString();
						PyroEditorProvider.logging("Process-Ended");
						try {
							resolve(JSON.parse(result));
						} catch {
							resolve(result);
						}
					});
				}
			);
            req.on('error', error => {
                PyroEditorProvider.logging(
                    `Request error ${error.name}: ${error.message}`
                );
            });
			if(data && !isEmpty(data.toString())) {
				req.write(JSON.stringify(data));
			}
			req.end();
		});
	}

	public static async createModel(name :string|undefined, modelType:string|undefined, token: string): Promise<any> {
		const options: http.RequestOptions = {
            agent: INTERNAL_USE_SSL ? https.globalAgent : undefined,
            protocol: `${INTERNAL_USE_SSL ? 'https' : 'http'}:`,
			hostname: INTERNAL_PYRO_HOST,
			port: INTERNAL_PYRO_PORT,
			path: INTERNAL_PYRO_SUBPATH+'/api/'+PyroApi.getRestEndpoint(modelType)+'/create/private',
			method: 'POST',
			'headers': {
				'Authorization': token,
				'Content-Type': 'application/json'
			}
		};
		return this.performRequest(options, {
			'filename': name
		});
	}

	public static async removeModel(modelType:string|undefined, id: string, token: string): Promise<any> {
		const options = {
            agent: INTERNAL_USE_SSL ? https.globalAgent : undefined,
            protocol: `${INTERNAL_USE_SSL ? 'https' : 'http'}:`,
			hostname: INTERNAL_PYRO_HOST,
			port: INTERNAL_PYRO_PORT,
			path: INTERNAL_PYRO_SUBPATH+'/api/'+PyroApi.getRestEndpoint(modelType)+'/remove/'+id+'/private',
			method: 'GET',
			'headers': {
				'Authorization': token,
				'Content-Type': 'application/json'
			}
		};
		return this.performRequest(options);
	}

	public static async getModelTypes(token: string): Promise<Map<string, string>> {
		const options = {
            agent: INTERNAL_USE_SSL ? https.globalAgent : undefined,
            protocol: `${INTERNAL_USE_SSL ? 'https' : 'http'}:`,
			hostname: INTERNAL_PYRO_HOST,
			port: INTERNAL_PYRO_PORT,
			path: INTERNAL_PYRO_SUBPATH+'/api/graph/list/private',
			method: 'GET',
			'headers': {
				'Authorization': token,
				'Content-Type': 'application/json'
			}
		};
		return this.performRequest(options);
	}
	
	public static async getModelTypesOf(extension: string, token: string): Promise<string[]> {
		const result: string[] = [];
		const modelTypes = await this.getModelTypes(token);
		const types = Object.entries(modelTypes);
		for(const t of types) {
			// if fileType is a registered graphModel-fileType
			if(t[1] == extension) {
				result.push(t[0]);
			}
		}
		return result;
	}
	
	public static getRestEndpoint(modelType: string | undefined) {
		if(!modelType)
			throw new Error("ModelType missing!");
		return modelType.replace(".", "_").toLowerCase();
	}
}
