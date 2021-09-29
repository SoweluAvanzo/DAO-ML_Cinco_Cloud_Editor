import * as http from 'http';
import { PYRO_HOST, PYRO_PORT, PYRO_SUBPATH } from "./env_var";
import { isEmpty } from './fileNameUtils';
import { PyroEditorProvider } from './pyroEditor';


export abstract class PyroApi {

	protected TOKEN: string | undefined;
	protected PROJECT_ID: number | undefined = undefined;

	private static async performRequest(options: http.RequestOptions,data?:any):Promise<any> {
		PyroEditorProvider.logging("REQUESTING:\n"+options.path);
		return new Promise((resolve, reject) => {
			const req = http.request(options,(response: http.IncomingMessage) => {
					if (response.statusCode != 200) {
						PyroEditorProvider.logging('REQUEST FAILED:\n'+options.hostname+'\n'+options.path+'\n'+options.port);
						PyroEditorProvider.logging('CODE: '+response.statusCode+" | MESSAGE: "+response.statusMessage);
						reject(new Error(response.statusMessage));
					}
					const chunks:any[] = [];
					response.on('data', (chunk) => {
						chunks.push(chunk);
					});
					response.on('error',(e)=>PyroEditorProvider.logging("Process-Error:\n"+e));
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
			if(data && !isEmpty(data.toString())) {
				req.write(JSON.stringify(data));
			}
			req.end();
		});
	}

	public static async createModel(name :string|undefined, modelType:string|undefined, token: string): Promise<any> {
		const options = {
			hostname: PYRO_HOST,
			port: PYRO_PORT,
			path: PYRO_SUBPATH+'/api/'+modelType?.toLowerCase()+'/create/private',
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
			hostname: PYRO_HOST,
			port: PYRO_PORT,
			path: PYRO_SUBPATH+'/api/'+modelType?.toLowerCase()+'/remove/'+id+'/private',
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
			hostname: PYRO_HOST,
			port: PYRO_PORT,
			path: PYRO_SUBPATH+'/api/graph/list/private',
			method: 'GET',
			'headers': {
				//'Authorization': token,
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
}