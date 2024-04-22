import { AuthService } from './services/auth.service';
import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'certificate_manager_front';
  loggedIn = false;

  constructor(private authService: AuthService) {
    this.authService.recieveLoggedIn().subscribe({
      next: (value) => {
        this.loggedIn = value;
        if (!this.loggedIn) {
          this.loggedIn = this.authService.getUserFromStorage() == null? false: true;
        }
      },
      error: (err) => {
        console.log("Error getting current logged in information.")
      },
    })
  }
}
