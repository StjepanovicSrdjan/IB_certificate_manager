import { Component, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import { VerificationService } from '../services/verification.service';
// import { phonenumRegexValidator } from '../validators/userValidator';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit{

  @ViewChild('child') childComponent: NavbarComponent | undefined;
  clickedEmail: string = "";

  ngAfterViewInit() {
    this.childComponent?.nav?.nativeElement.classList.add('pos-rel');
  }
  
  forgotPasswordForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    number: new FormControl('', [Validators.required,])
  })

  constructor(private snackBar: MatSnackBar, 
    private verificationService: VerificationService, private router: Router, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.clickedEmail = this.route.snapshot.paramMap.get('email')!;
  }

  sendVerificationCode() {
    if (this.clickedEmail == 'true') {
      this.verificationService.sendResetPasswordEmail(this.forgotPasswordForm.value.email!).subscribe({
        next: (res: any) => {
          this.router.navigate(['reset-password']);
          this.snackBar.open("If this email exists, reset code will be sent to it", "", {
            duration: 2700, panelClass: ['snack-bar-success']
        });
        },
        error: (err: any) => {
          this.snackBar.open(err.error, "", {
            duration: 2700, panelClass: ['snack-bar-server-error']
         });
        }
      })
    } 
    else if (this.clickedEmail == 'false') {
      this.verificationService.sendResetPasswordSms(this.forgotPasswordForm.value.number!).subscribe({
        next: (res: any) => {
          this.router.navigate(['reset-password']);
          console.log(res);
          this.snackBar.open("If this email exists, reset code will be sent to it", "", {
            duration: 2700, panelClass: ['snack-bar-success']
        });
        },
        error: (err: any) => {
          this.snackBar.open(err.error, "", {
            duration: 2700, panelClass: ['snack-bar-server-error']
         });
        }
      })
    } 
    //this.router.navigate(['verification', {email: this.forgotPasswordForm.value.email}]);
    // if (this.forgotPasswordForm.valid)
    //   this.router.navigate(['verification', {email: this.forgotPasswordForm.value.email}]);
    // else 
    //   this.snackBar.open("Check your inputs again!", "", {
    //     duration: 2700,  panelClass: ['snack-bar-front-error'] 
    // });
  }
}