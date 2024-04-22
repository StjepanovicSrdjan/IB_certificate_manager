import { ChangeDetectorRef, Component } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CertificateService } from '../services/certificate.service';

@Component({
  selector: 'app-certificate-validity',
  templateUrl: './certificate-validity.component.html',
  styleUrls: ['./certificate-validity.component.css']
})
export class CertificateValidityComponent {

  filePath: string = "";
  file: File = {} as File;
  firstOption: boolean = true;
  validity: string = '';

  constructor(private ref: ChangeDetectorRef, private certificateService: CertificateService, private snackBar: MatSnackBar){

  }

  serialNumberForm = new FormGroup({
    serialNumber: new FormControl('', [Validators.required])
  })

  certNameForm = new FormGroup({
    certName: new FormControl('', [])
  })

  private disableForm() {
    (<any>Object).values(this.certNameForm.controls).forEach((control: FormControl) => {
      control.disable();
    });
  }

  ngOnInit(): void {
    this.disableForm();
  }

  clickedFirst(){
    this.firstOption = true;
    this.ref.detectChanges();
  }

  clickedSecond(){
    this.firstOption = false;
    this.ref.detectChanges();

  }

  onFileSelect(event: any){
    if (event.target.files){
      var reader = new FileReader();
      this.file = event.target.files[0];
      reader.readAsDataURL(this.file);
      reader.onload=(e: any)=>{
        this.filePath = reader.result as string;
      }
      console.log(this.certNameForm.value.certName);
      this.certNameForm.setValue({
        certName: this.file.name,
      })
    }
  }

  validateBySerialNumber(): any{
    if (this.serialNumberForm.valid){
    this.certificateService.validateBySerialNumber(this.serialNumberForm.value.serialNumber!).subscribe({
      next: (result) => {
      if (result.message == 'This certificate is valid!')
          this.validity = "valid";
        else
          this.validity = "not";
      },
      error: (error) => {
        this.validity = "";
        this.snackBar.open(error.error, "", {
          duration: 2700,panelClass: ['snack-bar-server-error']
       });
      }
    });
  } else {
    this.snackBar.open("Check inputs again!", "", {
      duration: 2700, panelClass: ['snack-bar-front-error']
   });
  }
  }

  validateByUpload(){
    if (this.file.name.split('.').pop()!.toLowerCase() != "crt"){
      this.snackBar.open("Uploaded file is not .crt extension!", "", {
        duration: 2700,panelClass: ['snack-bar-server-error']
     });
     return;
    }
    this.certificateService.validateByUpload(this.filePath).subscribe({
      next: (result) => {
        if (result.message == 'This certificate is valid!')
          this.validity = "valid";
        else
          this.validity = "not";
      },
      error: (error) => {
        this.validity = "";
        this.snackBar.open(error.error, "", {
          duration: 2700,
       });
      },
    });
  }

}
