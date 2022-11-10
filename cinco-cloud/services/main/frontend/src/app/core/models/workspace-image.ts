import { BaseEntity } from './base-entity';
import { User } from './user';
import { Project } from './project';

export class WorkspaceImage extends BaseEntity {
    name: string;
    imageName: string;
    imageVersion: string;
    published: boolean;
    user: User;
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
