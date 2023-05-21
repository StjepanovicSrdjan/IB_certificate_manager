import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { NgxOtpInputConfig } from 'ngx-otp-input';
import { NavbarComponent } from '../navbar/navbar.component';
import { VerificationService } from '../services/verification.service';
import { UserDTO } from '../services/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CertificateService } from '../services/certificate.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-verification-code',
  templateUrl: './verification-code.component.html',
  styleUrls: ['./verification-code.component.css']
})
export class VerificationCodeComponent implements OnInit{

  user: UserDTO = {} as UserDTO;
  // sendCodeVia: string = '';
  cause: string = '';

  @ViewChild('child') childComponent: NavbarComponent | undefined;

  ngAfterViewInit() {
    this.childComponent?.nav?.nativeElement.classList.add('pos-rel');
  }
  
  otpValue: any;

  constructor(private snackBar: MatSnackBar, private verificationService: VerificationService, 
    private router: Router, private route: ActivatedRoute, private certificateService: CertificateService,
    private authService: AuthService){

  }
  ngOnInit(): void {
    // this.sendCodeVia = this.route.snapshot.paramMap.get('type')!;
    this.verificationService.recieveUserDTO().subscribe((res: any) =>{
      this.user = res;
    });
    this.verificationService.recieveCause().subscribe((res:any) => {
      this.cause = res;
    });
  }

  public otpConfig: NgxOtpInputConfig = {
    otpLength: 6,
    // numericInputMode: false
    pattern: /^[a-zA-Z0-9_.-]$/
  };

  onOtpComplete(event: any) {
    this.otpValue = event;
  }

  verifyRegistration(){ 
    if (this.otpValue != null && this.otpValue != undefined && this.otpValue.length == 6){
      if (this.cause == 'registration') {

        this.verificationService.verifyRegistration(this.otpValue).subscribe({
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

      }
      else if (this.cause == 'twofactor') {

        this.verificationService.verifyTwoFactor(this.otpValue).subscribe({
          next: (res:any) => {
              this.authService.setLoggedIn(true);
              this.certificateService.getAllCertificates().subscribe({
              next: (value) => {
                this.certificateService.setCertificatesToDisplay(value);
                
              },
              error: (err) => {
                this.snackBar.open("Error wile trying to fetch all certificates.", "", {
                  duration: 2700, panelClass: ['snack-bar-server-error']
              });
                console.log("Error wile trying to fetch all certificates.")
              },
            });
            this.router.navigate(['all-certificates']);
          },
          error: (err) => {
            this.snackBar.open("Error wile trying to log in.", "", {
              duration: 2700, panelClass: ['snack-bar-server-error']
          });
          }
        })
      }
    } else 
      this.snackBar.open("Check your inputs again!", "", {
        duration: 2700,  panelClass: ['snack-bar-front-error'] 
    });
  }

}
