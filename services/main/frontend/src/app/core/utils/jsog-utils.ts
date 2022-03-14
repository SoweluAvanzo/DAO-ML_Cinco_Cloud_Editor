import { FileReference } from '../models/file-reference';
import { Organization } from '../models/organization';
import { User } from '../models/user';
import { Project } from '../models/project';
import { OrganizationAccessRightVector } from '../models/organization-access-right-vector';
import { WorkspaceImage } from '../models/workspace-image';
import { Settings } from '../models/settings';
import { WorkspaceImageBuildJob } from '../models/workspace-image-build-job';
import { GraphModelType } from '../models/graph-model-type';
import { GitInformation } from '../models/git-information';

/**
 * For each entity, reference the corresponding class
 * and the class for each complex field so that the jsog
 * fields can be resolved to the correct class.
 */
const objectReferenceMap: any = {
  'FileReference': {
    cls: FileReference
  },
  'GitInformation': {
    cls: GitInformation
  },
  'Organization': {
    cls: Organization,
    fields: {
      logo: FileReference,
      owners: User,
      members: User,
      projects: Project
    }
  },
  'OrganizationAccessRightVector': {
    cls: OrganizationAccessRightVector,
    fields: {
      user: User,
      organization: Organization
    }
  },
  'Project': {
    cls: Project,
    fields: {
      members: User,
      owner: User,
      organization: Organization,
      image: WorkspaceImage,
      template: WorkspaceImage,
      graphModelTypes: GraphModelType
    }
  },
  'Settings': {
    cls: Settings,
  },
  'User': {
    cls: User,
    fields: {
      ownedProjects: Project
    }
  },
  'WorkspaceImage': {
    cls: WorkspaceImage,
    fields: {
      user: User,
      project: Project
    }
  },
  'WorkspaceImageBuildJob': {
    cls: WorkspaceImageBuildJob,
    fields: {
      project: Project
    }
  }
};

function isDateField(field: any): boolean {
  const type: string = field.constructor.name;
  if (type === 'Date') return true;
  if (type !== 'String') return false;
  const isoDateRegex: RegExp = /\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}Z/;
  const matches: RegExpMatchArray | null = type.match(isoDateRegex);
  return matches != null && matches[0] === 'field';
}

function isPrimitiveField(field: any): boolean {
  return ['String', 'Number', 'Boolean'].includes(field.constructor.name);
}

function isArrayField(field: any): boolean {
  return field.constructor.name === 'Array';
}

function fromJsogInternal<T>(obj: any, cls: any, cache: any): T {
  // create a new empty class instance for the target type
  const target = new objectReferenceMap[cls.name].cls();

  // create a cache entry for the target object based
  // on the jsog unique '@id' property
  const cacheKey = obj['@id'];
  cache[cacheKey] = target;

  for (let prop of Object.getOwnPropertyNames(obj)) {
    // do not copy null properties or properties that belong to jsog
    if (obj[prop] == null || prop == '@id') continue;

    // copy all properties from the jsog object to the target class
    const propValue = obj[prop];

    if (prop == '@ref') {
      return cache[propValue] as T;
    } else if (isPrimitiveField(propValue)) {
      target[prop] = isDateField(propValue)
        ? new Date(propValue)
        : propValue;
    } else if (isArrayField(propValue)) {
      if (propValue.length === 0) {
        target[prop] = [];
      } else {
        if (isPrimitiveField(propValue[0])) {
          target[prop] = propValue;
        } else {
          const type = objectReferenceMap[cls.name].fields[prop];
          target[prop] = propValue.map((o: any) => {
            const ref = o['@ref'];
            return ref != null
              ? cache[ref]
              : fromJsogInternal(o, type, cache);
          });
        }
      }
    } else {
      const ref = propValue['@ref'];
      target[prop] = ref != null
        ? cache[ref]
        : fromJsogInternal(propValue, objectReferenceMap[cls.name].fields[prop], cache);
    }
  }
  return target as T;
}

function toJsogInternal(obj: any, cache: any): any {
  // the target jsog object
  const jsog: any = {};
  const cacheKey = `${obj.constructor.name}.${obj['id']}`;

  if (cache[cacheKey] != null) {
    // if an entry for the object exists in cache
    // return { @ref: <id> } as object
    jsog['@ref'] = cache[cacheKey];
  } else {
    // else create a new cache entry for the object
    cache[cacheKey] = `${Object.keys(cache).length + 1}`;
    jsog['@id'] = cache[cacheKey];

    // copy all properties to the target jsog object
    for (let prop of Object.getOwnPropertyNames(obj)) {
      const propValue = obj[prop];
      if (propValue == null) {
        jsog[prop] = null;
      } else if (isPrimitiveField(propValue)) {
        jsog[prop] = propValue;
      } else if (isDateField(propValue)) {
        jsog[prop] = propValue.toISOString();
      } else if (isArrayField(propValue)) {
        if (propValue.length === 0) {
          jsog[prop] = [];
        } else {
          // we assume that do not have lists with mixed types
          if (isPrimitiveField(propValue[0])) {
            jsog[prop] = propValue;
          } else {
            jsog[prop] = propValue.map((o: any) => toJsogInternal(o, cache));
          }
        }
      } else {
        // field is object
        jsog[prop] = toJsogInternal(propValue, cache);
      }
    }
  }

  return jsog;
}

/**
 * Maps a JavaScript object to a JSOG.
 *
 * @param obj The JSOG.
 */
export function toJsog(obj: any): any {
  if (obj == null) return obj;
  return toJsogInternal(obj, {});
}

/**
 * Maps a JSOG to a JavaScript class instance.
 *
 * @param obj The JSOG object.
 * @param cls The target class the JSOG should be mapped to.
 */
export function fromJsog<T>(obj: any, cls: any): T {
  if (obj == null) return obj;
  return fromJsogInternal(obj, cls, {});
}

/**
 * Maps a list of JSOG objects to a list of JavaScript class instances.
 *
 * @param objList The list of JSOG objects.
 * @param cls The target class the JSOG should be mapped to.
 */
export function fromJsogList<T>(objList: any[], cls: any): T[] {
  const cache: any = {};
  return objList.map((o: any) => fromJsogInternal(o, cls, cache));
}
