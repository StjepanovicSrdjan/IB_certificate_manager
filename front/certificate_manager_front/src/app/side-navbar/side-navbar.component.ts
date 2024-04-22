import { CertificateService } from './../services/certificate.service';
import { UserService } from './../services/user.service';
import { NavigationEnd, Router } from '@angular/router';
import { AuthService } from './../services/auth.service';
import { Component } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { GenerateRequestDialogComponent } from '../generate-request-dialog/generate-request-dialog.component';

@Component({
  selector: 'app-side-navbar',
  templateUrl: './side-navbar.component.html',
  styleUrls: ['./side-navbar.component.css']
})
export class SideNavbarComponent {

  name = "Tina";
  url = '';
  reqByMe: boolean = false;
  reqFromMe: boolean = false;
  role: any;

  constructor(private authService: AuthService, 
              private router: Router, 
              private userService: UserService,
              private certificateService: CertificateService,
              private snackBar: MatSnackBar,
              private dialog: MatDialog) {
                
                
  }

  ngOnInit(): void {
    let loggedUser = this.authService.getUser();
    this.name = loggedUser? loggedUser.name: "";
    this.handleSmallScreens();

    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.url = event.urlAfterRedirects;
      }
    });
    this.role = this.authService.getRole();
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['login']);
  }


  handleSmallScreens(): void {
    (<HTMLButtonElement>document.querySelector('.navbar-toggler'))
      .addEventListener('click', () => {
      let navbarMenu = <HTMLDivElement>document.querySelector('#side-navbar-container')
      let navbarMenuSmaller = <HTMLDivElement>document.querySelector('#navbar-smaller')
      let closeBtn = <HTMLDivElement>document.querySelector('#close-btn i')
  
      if (navbarMenu.style.display === 'block') {
        navbarMenu.style.display = 'none';
        closeBtn.style.display = 'none';
        navbarMenuSmaller.style.display = 'block';
        return ;
      }
  
      navbarMenu.style.display = 'block';
      closeBtn.style.display = 'block';

      navbarMenuSmaller.style.display = 'none';
    })
  }

  close() {
    let navbarMenu = <HTMLDivElement>document.querySelector('#side-navbar-container');
    let navbarMenuSmaller = <HTMLDivElement>document.querySelector('#navbar-smaller');
    let closeBtn = <HTMLDivElement>document.querySelector('#close-btn i');

    navbarMenu.style.display = 'none';
    closeBtn.style.display = 'none';

    navbarMenuSmaller.style.display = 'block';
  }

  showCertificates(type: string) {
    if (type == 'all') {
      this.certificateService.getAllCertificates().subscribe({
        next: (value) => {
          this.certificateService.setCertificatesToDisplay(value);
        },
        error: (err) => {
          this.snackBar.open("Error while trying to fetch all certificates.", "", {
            duration: 2700, panelClass: ['snack-bar-server-error']
         });
          console.log("Error wile trying to fetch all certificates.")
        },
      });
      this.router.navigate(['all-certificates']);
    } else {
      if (type == "my") {
        this.certificateService.getMyCertificates().subscribe({
          next: (value) => {
            this.certificateService.setCertificatesToDisplay(value);
          },
          error: (err) => {
            this.snackBar.open("Error wile trying to fetch all certificates.", "", {
              duration: 2700, panelClass: ['snack-bar-server-error']
           });
            console.log("Error wile trying to fetch your certificates.")
          },
        });
        this.router.navigate(['my-certificates']);
      }
    }
  }

  openRequestReview(option: boolean) {
    if (option) {
      this.reqByMe = true;
      this.reqFromMe = false;
    }
    else {
      this.reqByMe = false;
      this.reqFromMe = true;
    }
    this.certificateService.setIsByMeSelected(option);
    this.router.navigate(['request-review']);
  }

  showValidate(){
    this.router.navigate(['/validate']);
  }

  createRootCertificate() {
    this.certificateService.generateRoot().subscribe({
      next: (res: any) => {
        this.snackBar.open(res.message, "", {
          duration: 2700, panelClass: ['snack-bar-success']
       });
      }, 
      error: (err) => {
        this.snackBar.open(err.error, "", {
          duration: 2700, panelClass: ['snack-bar-server-error']
       });
        console.log(err);
      },
    })
  }

}
