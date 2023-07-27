import { /*ValidationAcceptor,*/ ValidationChecks } from 'langium';
import { CincoAstType } from '../../generated/ast';
import type { MslServices } from './msl-module';

/**
 * Register custom validation checks.
 */
export function registerValidationChecks(services: MslServices) {
    const registry = services.validation.ValidationRegistry;
    const validator = services.validation.MslValidator;
    const checks: ValidationChecks<CincoAstType> = {
        //Person: validator.checkPersonStartsWithCapital
    };
    registry.register(checks, validator);
}

/**
 * Implementation of custom validations.
 */
export class MslValidator {

    // checkPersonStartsWithCapital(person: Person, accept: ValidationAcceptor): void {
    //     if (person.name) {
    //         const firstChar = person.name.substring(0, 1);
    //         if (firstChar.toUpperCase() !== firstChar) {
    //             accept('warning', 'Person name should start with a capital.', { node: person, property: 'name' });
    //         }
    //     }
    // }

}
