import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { UserDTO } from './user.service';

export interface ResetPasswordDTO{
  newPassword: string,
  code: string
}

export interface RotatePasswordDTO{
  email: string,
  oldPassword: string,
  newPassword: string
}

@Injectable({
  providedIn: 'root'
})
export class VerificationService {

  private subject = new BehaviorSubject<any>({});
  private cause = new BehaviorSubject<any>({});
  private email = new BehaviorSubject<any>({});

  sendUserDTO(message: UserDTO): void {
    this.subject.next(message);
  }

  recieveUserDTO(): Observable<UserDTO> {
    return this.subject.asObservable();
  }

  sendCause(cause: string) : void {
    this.cause.next(cause);
  }

  recieveCause(): Observable<string> {
    return this.cause.asObservable();
  }

  sendEmail(email: string) : void {
    this.email.next(email);
  }

  recieveEmail(): Observable<string> {
    return this.email.asObservable();
  }

  constructor(private http: HttpClient) { }

  sendVerificationMail(email: string): Observable<any>{
    const options: any = {
      responseType: 'json',
      rejectUnauthorized: false,
    };
    return this.http.post<any>(environment.apiHost + "/user/send/verification/email/" + email, options);
  }

  sendVerificationSMS(email: string): Observable<any>{
    const options: any = {
      responseType: 'json',
      rejectUnauthorized: false,
    };
    return this.http.post<any>(environment.apiHost + "/user/send/verification/sms/" + email, options);
  }

  verifyRegistration(verificationCode: number): Observable<any> {
    const options: any = {
      responseType: 'json',
      rejectUnauthorized: false,
    };
    return this.http.get<any>(environment.apiHost + "/user/activate/"+verificationCode, options);
  }

  sendResetPasswordEmail(email: string): Observable<any>{
    const options: any = {
      responseType: 'json',
      rejectUnauthorized: false,
    };
    return this.http.get<any>(environment.apiHost + "/user/reset/password/email/" + email, options);
  }

  sendResetPasswordSms(email: string): Observable<any>{
    const options: any = {
      responseType: 'json',
      rejectUnauthorized: false,
    };
    
    console.log(environment.apiHost + "/user/"+email+"/reset/password/sms");
    return this.http.get<any>(environment.apiHost + "/user/reset/password/sms/" + email, options);
  }

  resetPassword(resetPasswordDTO: ResetPasswordDTO): Observable<any>{
    const options: any = {
      responseType: 'json',
      rejectUnauthorized: false,
    };
    console.log(resetPasswordDTO);
    return this.http.put<any>(environment.apiHost + "/user/resetPassword", resetPasswordDTO, options);
  }

  rotatePassword(rotatePasswordDTO: RotatePasswordDTO): Observable<any>{
    const options: any = {
      responseType: 'json',
      rejectUnauthorized: false,
    };
    return this.http.put<any>(environment.apiHost + "/user/rotatePassword", rotatePasswordDTO, options);
  }
  
  sendTwoFactorEmail(email: string): Observable<any> {
    const options: any = {
      responseType: 'json',
      rejectUnauthorized: false,
    };
    return this.http.post<any>(environment.apiHost + "/user/send/twofactor/email/" + email, options);
  }

  sendTwoFactorSMS(phoneNumber: string): Observable<any> {
    const options: any = {
      responseType: 'json',
      rejectUnauthorized: false,
    };
    return this.http.post<any>(environment.apiHost + "/user/send/twofactor/sms/" + phoneNumber, options);
  }

  verifyTwoFactor(verificationCode: number): Observable<any> {
    const options: any = {
      responseType: 'json',
      rejectUnauthorized: false,
    };
    return this.http.get<any>(environment.apiHost + "/user/verify/twofactor/"+verificationCode, options);
  }

}
