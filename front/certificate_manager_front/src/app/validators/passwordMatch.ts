import { AbstractControl, FormGroup, FormControl, FormGroupDirective, NgForm, ValidatorFn } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core'

export function passwordMatcher(password: string, confPassword: string) {

    return function(form: AbstractControl) {
        const passwordValue = form.get(password)?.value;
        const confPasswordValue = form.get(confPassword)?.value;

        if (passwordValue === confPasswordValue) {
            return null;
        }

        return { passwordMismatchError: true };
    }
}

export class ConfirmValidParentMatcher implements ErrorStateMatcher {
	isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
		if (control?.parent?.errors?.['passwordMismatchError'] && control.dirty) return true;
        return false;
	}
}