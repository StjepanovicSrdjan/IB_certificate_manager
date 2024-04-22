import { Component, ViewChild } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxOtpInputConfig } from 'ngx-otp-input';
import { NavbarComponent } from '../navbar/navbar.component';
import { VerificationService } from '../services/verification.service';
import { passwordRegexValidator } from '../validators/userValidator';
import { markFormControlsTouched } from '../validators/formGroupValidator';
import { ConfirmValidParentMatcher, passwordMatcher } from '../validators/passwordMatch';

@Component({
  selector: 'app-password-rotation',
  templateUrl: './password-rotation.component.html',
  styleUrls: ['./password-rotation.component.css']
})
export class PasswordRotationComponent {

  confirmValidParentMatcher = new ConfirmValidParentMatcher();
  
  @ViewChild('child') childComponent: NavbarComponent | undefined;
  
  isFirstVisible: boolean = false;
  isSecondVisible: boolean = false;
  isThirdVisible: boolean = false;
  emailForRotation: string = '';

  passwordRotationForm = new FormGroup({
    old_password: new FormControl('', [Validators.required]),
    new_password: new FormControl('', [Validators.required, passwordRegexValidator]),
    confpass: new FormControl('', [Validators.required])
  }, [passwordMatcher("new_password", "confpass")])

  constructor(private route: ActivatedRoute,
    private verificationService: VerificationService, 
    private snackBar: MatSnackBar, 
    private router: Router) { }
  
  ngAfterViewInit() {
    this.emailForRotation = this.route.snapshot.paramMap.get('email')!;
    console.log(this.emailForRotation);
    this.childComponent?.nav?.nativeElement.classList.add('pos-rel');
  }

  ngOnInit(): void {
    markFormControlsTouched(this.passwordRotationForm);
  }

  rotatePassword() {
    if (this.passwordRotationForm.valid){
      this.verificationService.rotatePassword({
        email: this.emailForRotation,
        oldPassword: this.passwordRotationForm.value.old_password!,
        newPassword: this.passwordRotationForm.value.new_password!
      }).subscribe({
        next: (res: any) => {
          this.snackBar.open(res.message, "", {
            duration: 2700, panelClass: ['snack-bar-success']
        });
          this.router.navigate(['login']);
        },
        error: (err: any) => {
          this.snackBar.open(err.error, "", {
            duration: 2700, panelClass: ['snack-bar-server-error']
         });
        }
      })
    } else 
      this.snackBar.open("Check your inputs again!", "", {
        duration: 2700,  panelClass: ['snack-bar-front-error'] 
    });
  }

}
