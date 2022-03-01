import { BaseEntity } from './base-entity';
import { environment } from '../../../environments/environment';

export class FileReference extends BaseEntity {
  fileName: string;
  contentType: string;

  get path(): string {
    return `${environment.apiUrl}/files/read/${this.id}/private`;
  }

  get downloadPath(): string {
    return `${environment.apiUrl}/files/download/${this.id}/private`;
  }
}
