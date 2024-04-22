import { AbstractControl, FormGroup, FormControl, FormGroupDirective, NgForm, ValidatorFn } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core'

  export function nameRegexValidator( control: AbstractControl): { [key: string]: boolean } | null {
    const regex = /^([a-zA-Zčćđžš ]*)$/;
    if (control.value !== undefined && !regex.test(control.value)) {
        return { nameRegexError: true };
    }
    return null;
  }

  export function surnameRegexValidator( control: AbstractControl): { [key: string]: boolean } | null {
    const regex = /^([a-zA-Zčćđžš ]*)$/;
    if (control.value !== undefined && !regex.test(control.value)) {
        return { surnameRegexError: true };
    }
    return null;
  }

  export function passwordRegexValidator( control: AbstractControl): { [key: string]: boolean } | null {
    const regex = /^([0-9a-zA-Z]{8,}$)/;
    if (control.value !== undefined && !regex.test(control.value)) {
        return { passwordRegexError: true };
    }
    return null;
  }

  export function usernameRegexValidator( control: AbstractControl): { [key: string]: boolean } | null {
    const regex = /^([0-9a-z]{3,}$)/;
    if (control.value !== undefined && !regex.test(control.value)) {
        return { usernameRegexError: true };
    }
    return null;
  }

    export function hasLetterAndDigitValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
        const hasLetter = /[a-z]/.test(control.value);
        const hasDigit = /\d/.test(control.value);
        return hasLetter && hasDigit ? null : { hasLetterAndDigitError: true };
    };
    }

    export function dateBeforeTodayValidator(): ValidatorFn {
    return (control: AbstractControl): {[key: string]: any} | null => {
        const today = new Date();
        const selectedDate = new Date(control.value);
        if (selectedDate > today) {
        return null; // return null if validation succeeds
        }
        return {'dateAheadOfToday': {value: control.value}}; // return error object if validation fails
    };
    }


