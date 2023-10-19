import { BaseEntity } from './base-entity';
import { Project } from './project';

export class WorkspaceImage extends BaseEntity {
    imageVersion: string;
    published: boolean;
    featured: boolean;
    project: Project;
    createdAt: Date;

    get fullyQualifiedName(): string {
        let namespace: string = '';
        if (this.project.owner != null) {
            namespace += `${this.project.owner.username}`;
        } else if (this.project.organization != null) {
            namespace += `${this.project.organization.name}`;
        }
        return `@${namespace}/${this.project.name}`;
    }
}
