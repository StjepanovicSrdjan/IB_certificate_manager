import { OAuth2Service } from './../services/o-auth.service';
import { CertificateService } from './../services/certificate.service';
import { AuthService } from './../services/auth.service';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { VerificationService } from '../services/verification.service';
import { markFormControlsTouched } from '../validators/formGroupValidator';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit{
  isVisible : boolean = false;
  submited: boolean = false;
  captchaOk: boolean = false;
  captchaString = '';

  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required]),
    recaptcha: new FormControl('', [Validators.required])
  });

  constructor(private authService: AuthService,
      public snackBar: MatSnackBar,
      private router: Router,
      private certificateService: CertificateService,
      private verificationService: VerificationService,
      private oauth2Service: OAuth2Service,
      private route: ActivatedRoute) {
    this.hadnleGoogleLogin();
  }
  
  ngOnInit(): void {
    markFormControlsTouched(this.loginForm);
  }

  hadnleGoogleLogin() {
    this.route.queryParams.subscribe(params => {
      const code = params['code']; 
      const state = params['state'];
      
      console.log(code);

      if (code != undefined) {
        this.oauth2Service.signInToBack(code, state).subscribe({
          next: (value) => {
            console.log(value);
            localStorage.setItem('user', JSON.stringify(value.accessToken));
            this.authService.setUser();
            this.authService.setLoggedIn(true);
            this.router.navigate(['/all-certificates']);
          },
          error: (err) => {
            this.snackBar.open("Google sign in error! Try again", "", {
              duration: 2700, panelClass: ['snack-bar-server-error']
           });
          },
        });
  
        console.log(code + "\n" + state);
      }
      
    });
  }

  resolved(captchaResponse: string) {
    this.captchaOk = captchaResponse === null ? false: true;
    this.captchaString = captchaResponse;
  }

  login(){
    this.submited = true;

    const credentials = {
      email: this.loginForm.value.email,
      password: this.loginForm.value.password
    };

    if (this.loginForm.valid && this.captchaOk) {
      this.authService.login(credentials, this.captchaString).subscribe({
        next: (result) => {
          this.processLogin(result);
        },
         error: (error) => {
          console.log(error);
          console.log("tu")
          console.log(error.error)
          if (error.error == "You should renew your password!"){
            this.snackBar.open("Your password expired. You should renew your password!", "", {
              duration: 2700, panelClass: ['snack-bar-server-error']
           });
           this.router.navigate(['password-rotation', this.loginForm.value.email]);
           return;
          }
          this.snackBar.open("Bad credentials. Please try again!", "", {
            duration: 2700, panelClass: ['snack-bar-server-error']
         });
        },
      });
    }
  }

  


  processLogin(result: any) {
    localStorage.setItem('user', JSON.stringify(result.accessToken));
          // localStorage.setItem('refreshToken', JSON.stringify(result.refreshToken));
          this.authService.setUser();
          // this.certificateService.getAllCertificates().subscribe({
          //   next: (value) => {
          //     this.certificateService.setCertificatesToDisplay(value);
              
          //   },
          //   error: (err) => {
          //     this.snackBar.open("Error wile trying to fetch all certificates.", "", {
          //       duration: 2700, panelClass: ['snack-bar-server-error']
          //    });
          //     console.log("Error wile trying to fetch all certificates.")
          //   },
          // });
          // this.router.navigate(['all-certificates']);
          console.log(this.authService.getUser());

          // ZA TESTIRANJE:
          //this.router.navigate(['all-certificates']);

          // OVO ZAPRAVO TREBA:
          this.verificationService.sendEmail(this.loginForm.value.email!);
          this.verificationService.sendCause('twofactor');
          this.router.navigate(['/verification-choice']);
  }

  loginWithGoogle() {
    console.log("tu");
    this.oauth2Service.loginWithGoogle();
  }

  redirectToReset() {
    this.verificationService.sendCause('reset');
    this.router.navigate(['/verification-choice']);
  }
}
