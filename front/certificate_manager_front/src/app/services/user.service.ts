import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http:HttpClient) { }

  findById(id: number): Observable<any> {
    return this.http.get<any>(environment.apiHost + "/user/" + id);
  }

  registerUser(user: UserDTO, captcha: string): Observable<any> {
    console.log(environment.apiHost);
    return this.http.post<any>(environment.apiHost + "/user", user, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'captcha': captcha
      })
    });
  }

  verify(code: String): Observable<any> {
    return this.http.get<any>(environment.apiHost + '/user/activate/' + code);
  }
}  

export interface UserDTO {
  name: string;
  lastname: string;
  phoneNumber: string;
  email: string;
  password: string;
}

export interface ResponseMessageDTO {
  message: string

}
export interface UserDTO {
  name: string;
  lastname: string;
  phoneNumber: string;
  email: string;
  password: string;
}