import { BaseEntity } from './base-entity';
import { environment } from '../../../environments/environment';

export class FileReference extends BaseEntity {
  fileName: string;
  contentType: string;

  get path(): string {
    return `${environment.apiUrl}/files/${this.id}?download=false`;
  }

  get downloadPath(): string {
    return `${environment.apiUrl}/files/${this.id}?download=true`;
  }
}
