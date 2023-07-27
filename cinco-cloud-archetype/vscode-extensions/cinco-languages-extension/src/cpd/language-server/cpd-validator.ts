import { ValidationChecks } from 'langium';
import { CincoAstType } from '../../generated/ast';
import type { CpdServices } from './cpd-module';

/**
 * Register custom validation checks.
 */
export function registerValidationChecks(services: CpdServices) {
    const registry = services.validation.ValidationRegistry;
    const validator = services.validation.CpdValidator;
    const checks: ValidationChecks<CincoAstType> = {
    };
    registry.register(checks, validator);
}

/**
 * Implementation of custom validations.
 */
export class CpdValidator {

}
