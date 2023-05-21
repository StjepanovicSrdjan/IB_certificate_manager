import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../services/auth.service';
import { CertificateService } from '../services/certificate.service';

@Component({
  selector: 'app-withdraw-dialog',
  templateUrl: './withdraw-dialog.component.html',
  styleUrls: ['./withdraw-dialog.component.css']
})
export class WithdrawDialogComponent implements OnInit{

  constructor(
    public dialogRef: MatDialogRef<WithdrawDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private certificateService: CertificateService,
    private snackBar: MatSnackBar
  ) {}

  reason: string = "";
  serialNumber: string = "";
  selectedRequestId: number = 0;

  ngOnInit(): void {
    this.serialNumber = this.data.serialNumber;
    this.selectedRequestId = this.data.selectedRequestId;
  }

  withdrawCertificate(): void {
    console.log(this.serialNumber, this.reason);
    if (this.reason != ''){
    
    this.certificateService.withdraw(this.serialNumber, this.reason).subscribe({
      next: (res: any) => {
        this.snackBar.open(res.message, "", {
          duration: 2700, panelClass:['snack-bar-success']
       });
       this.dialogRef.close();
      },
      error: (err: any) => {
        this.snackBar.open(err.error, "", {
          duration: 2700, panelClass: ['snack-bar-server-error']
       });
      },
    })} else {
      this.snackBar.open("Reason is required!", "", {
        duration: 2700, panelClass: ['snack-bar-front-error']
     });}
  }

  decide(){
    if (this.serialNumber != '')
      this.withdrawCertificate();
    else 
      this.declineRequest();
  }

  declineRequest(){
    if (this.reason != ''){
    this.certificateService.declineRequestes(this.selectedRequestId, this.reason).subscribe({
      next: (res: any) => {
        this.snackBar.open(res.message, "", {
          duration: 2700, panelClass:['snack-bar-success']
       });
       this.dialogRef.close();
      },
      error: (error:any) => {
        this.snackBar.open(error.error, "", {
          duration: 2700, panelClass: ['snack-bar-server-error']
       });
      }
    })} else {
      this.snackBar.open("Reason is required!", "", {
        duration: 2700, panelClass: ['snack-bar-front-error']
     });
    }
    
  }


}
