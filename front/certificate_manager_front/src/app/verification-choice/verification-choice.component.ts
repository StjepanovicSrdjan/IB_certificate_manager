import { Component, OnInit } from '@angular/core';
import { AuthService, User } from '../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserDTO } from '../services/user.service';
import { VerificationService } from '../services/verification.service';

@Component({
  selector: 'app-verification-choice',
  templateUrl: './verification-choice.component.html',
  styleUrls: ['./verification-choice.component.css']
})
export class VerificationChoiceComponent implements OnInit{

  userDTO: UserDTO = {} as UserDTO;
  emailForReset: string = '';
  cause: string = '';
  email: string = '';

  constructor(private router: Router, private route: ActivatedRoute, public snackBar: MatSnackBar, private verificationService: VerificationService){
    
  }
  
  ngOnInit(): void {
    this.emailForReset = this.route.snapshot.paramMap.get('email')!;
    this.verificationService.recieveUserDTO().subscribe((res: any) => {
      this.userDTO = res;
    })
    this.verificationService.recieveCause().subscribe((res: any) => {
      this.cause = res;
    })
    this.verificationService.recieveEmail().subscribe((res:any) => {
      this.email = res;
    })
  }

  clickedSms: boolean = false;
  clickedEmail: boolean = false;

  clickSms() : void {
    this.clickedSms = !this.clickedSms
    this.clickedEmail = false;
  }

  clickEmail() : void {
    this.clickedEmail = !this.clickedEmail;
    this.clickedSms = false;
  }

  open() {
    if (this.cause == 'registration')
      this.openRegisterVerification();
    else if (this.cause == 'reset')
      this.openResetPassword();
    else
      this.openTwoFactor();
  }

  openTwoFactor() {
      if (this.clickedEmail) {
        this.verificationService.sendTwoFactorEmail(this.email).subscribe({
          next: (res: any) => {
            this.snackBar.open(res.message, "", {
              duration: 2700, panelClass: ['snack-bar-success']
          });
            this.router.navigate(['verification-code']);
          }
        })
      }
  }

  openRegisterVerification(){
    if (this.clickedEmail) {
      this.verificationService.sendVerificationMail(this.userDTO.email).subscribe({
        next: (res: any) => {
          this.snackBar.open(res.message, "", {
            duration: 2700, panelClass: ['snack-bar-success']
        });
          this.router.navigate(['verification-code']);
        },
        error: (err: any) => {
          this.snackBar.open(err.error, "", {
            duration: 2700, panelClass: ['snack-bar-server-error']
         });
        }
      })
    } 
    else if (this.clickedSms) {
      this.verificationService.sendVerificationSMS(this.userDTO.email).subscribe({
        next: (res: any) => {
          this.snackBar.open(res.message, "", {
            duration: 2700, panelClass: ['snack-bar-success']
        });
          this.router.navigate(['verification-code', {type: "sms"}]);
        },
        error: (err: any) => {
          this.snackBar.open(err.error, "", {
            duration: 2700, panelClass: ['snack-bar-server-error']
         });
        }
      })
    } else {
      this.snackBar.open("Please select one of the options.", "", {
        duration: 2700, panelClass: ['snack-bar-front-error'] 
     });
    }
  }


  openResetPassword(){
    console.log("eee")
    if (this.clickedEmail){
      this.router.navigate(['forgot-password', {email: this.clickedEmail}])
    } else if (this.clickedSms)  {
      this.router.navigate(['forgot-password', {email: this.clickedEmail}])
    } else{
      this.snackBar.open("Please select one of the options.", "", {
        duration: 2700,  panelClass: ['snack-bar-front-error'] 
     });
    
    }
  }

  // openResetPassword(){
  //   if (this.clickedEmail) {
  //     this.verificationService.sendResetPasswordEmail(this.emailForReset).subscribe({
  //       next: (res: any) => {
  //         this.snackBar.open(res.message, "", {
  //           duration: 2700, panelClass: ['snack-bar-success']
  //       });
  //         this.router.navigate(['reset-password']);
  //       },
  //       error: (err: any) => {
  //         this.snackBar.open(err.error, "", {
  //           duration: 2700, panelClass: ['snack-bar-server-error']
  //        });
  //       }
  //     })
  //   } 
  //   else if (this.clickedSms) {
  //     this.verificationService.sendResetPasswordSms(this.emailForReset).subscribe({
  //       next: (res: any) => {
  //         this.snackBar.open(res.message, "", {
  //           duration: 2700, panelClass: ['snack-bar-success']
  //       });
  //         this.router.navigate(['reset-password']);
  //       },
  //       error: (err: any) => {
  //         this.snackBar.open(err.error, "", {
  //           duration: 2700, panelClass: ['snack-bar-server-error']
  //        });
  //       }
  //     })
  //   } else {
  //     this.snackBar.open("Please select one of the options.", "", {
  //       duration: 2700,  panelClass: ['snack-bar-front-error'] 
  //    });
  //   }
  // }
}
