import { WorkspaceImageBuildJob } from './workspace-image-build-job';

export class Page<T> {
  items: T[] = [];
  number: number;
  size: number;
  amountOfPages: number;
  hasPreviousPage: boolean;
  hasNextPage: boolean;

  public static fromObject<T>(obj: any, items: T[] = []): Page<T> {
    const page = new Page<T>();
    page.number = obj.number;
    page.size = obj.size;
    page.amountOfPages = obj.amountOfPages;
    page.hasPreviousPage = obj.hasPreviousPage;
    page.hasNextPage = obj.hasNextPage;
    page.items = items;
    return page;
  }
}
