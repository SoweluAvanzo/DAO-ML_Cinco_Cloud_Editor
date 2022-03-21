import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

export abstract class BaseApiService {

  protected constructor(protected http: HttpClient) {
  }

  protected get apiUrl(): string {
    return environment.apiUrl;
  }

  protected get webSocketUrl(): string {
    return environment.webSocketUrl;
  }

  protected get defaultHttpHeaders(): HttpHeaders {
    let headers = new HttpHeaders();

    const jwt = localStorage.getItem('cinco_cloud_token');
    if (jwt != null) {
      headers = headers.set('Authorization', `Bearer ${jwt}`);
    }

    return headers;
  }

  protected get defaultHttpOptions(): any {
    return {
      headers: this.defaultHttpHeaders
    };
  }
}
