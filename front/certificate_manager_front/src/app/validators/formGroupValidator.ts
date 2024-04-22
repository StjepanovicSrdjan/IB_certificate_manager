import { FormControl, FormGroup } from "@angular/forms";

export function markFormControlsTouched(formGroup: FormGroup) {
    (<any>Object).values(formGroup.controls).forEach((control: FormControl) => {
      control.valueChanges.subscribe(() => {
        control.markAsTouched();
      })
    });
  }