import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from 'src/app/login/login.component';
import { MailVerificationComponent } from 'src/app/mail-verification/mail-verification.component';
import { RegisterComponent } from 'src/app/register/register.component';
import { VerificationChoiceComponent } from 'src/app/verification-choice/verification-choice.component';

const routes: Routes = [
  {path: "login", component: LoginComponent},
  {path: "", component: LoginComponent},
  {path: "register", component: RegisterComponent},
  {path: "verification/mail", component: MailVerificationComponent},
  {path: "verification", component: VerificationChoiceComponent}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
