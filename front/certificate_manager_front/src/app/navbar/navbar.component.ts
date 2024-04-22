import { RouterModule } from '@angular/router';
import { AuthService } from './../services/auth.service';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit{

  @ViewChild('nav') nav: ElementRef | undefined;

  role: string = "";

  constructor(){

  }

  ngOnInit(): void {
    this.handleSmallScreens();
  }

  handleSmallScreens(): void {
    (<HTMLButtonElement>document.querySelector('.navbar-toggler'))
      .addEventListener('click', () => {
      let navbarMenu = <HTMLDivElement>document.querySelector('.navbar-menu')
  
      if (navbarMenu.style.display === 'flex') {
        navbarMenu.style.display = 'none'
        return 
      }
  
      navbarMenu.style.display = 'flex'
    })
  }


  openHome(role: string){
    
  }
}
