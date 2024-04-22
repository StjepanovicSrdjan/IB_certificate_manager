import { Injectable } from '@angular/core';
import { User } from './auth.service';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CertificateService {

  private certificatesToDisplay = new BehaviorSubject<Cerificate[]>([]);

  private isByMeSelected$ = new BehaviorSubject<boolean>(true);
  getIsByMeSelected(): Observable<any> {
    return this.isByMeSelected$;
  }

  setIsByMeSelected(is: boolean): void {
    this.isByMeSelected$.next(is);
  }

  constructor(private http: HttpClient) { }

  setCertificatesToDisplay(certificates: Cerificate[]) {
    this.certificatesToDisplay.next(certificates);
  }

  getCertificatesToDisplay() {
    return this.certificatesToDisplay.asObservable();
  }

  getMyCertificates(): Observable<any> {
    return this.http.get<any>(environment.apiHost + "/certificate/mine");
  }

  getAllCertificates(): Observable<any> {
    return this.http.get<any>(environment.apiHost + "/certificate");
  }

  validateByUpload(file: String): Observable<any> {
    const options: any = {
        responseType: 'json',
      };
    return this.http.post<any>(environment.apiHost + "/certificate/validate-upload", file, options);
  }

  validateBySerialNumber(serialNumber: String): Observable<any> {
    const options: any = {
        responseType: 'json',
      };
    return this.http.get<any>(environment.apiHost + "/certificate/validate/" + serialNumber, options);
  }


  getAllRequestes() : Observable<any> {
    const options: any = {
      responseType: 'json',
    };
    return this.http.get<any>(environment.apiHost + "/certificate/request", options);
  }

  getAllRequestesByMe() : Observable<any> {
    const options: any = {
      responseType: 'json',
    };
    return this.http.get<any>(environment.apiHost + "/certificate/request/byMe", options);
  }

  getAllRequestesFromMe() : Observable<any> {
    const options: any = {
      responseType: 'json',
    };
    return this.http.get<any>(environment.apiHost + "/certificate/request/fromMe", options);
  }

  acceptRequestes(id: number) : Observable<any> {
    const options: any = {
      responseType: 'json',
    };
    return this.http.put<any>(environment.apiHost + "/certificate/request/accept/" + id, options);
  }

  declineRequestes(id: number, reason: String) : Observable<any> {
    const options: any = {
      responseType: 'json',
    };
    return this.http.put<any>(environment.apiHost + "/certificate/request/deny/" + id, reason, options);
  }

  withdraw(serialNumber: String, reason: String): Observable<any> {
    const options: any = {
        responseType: 'json',
      };
    return this.http.put<any>(environment.apiHost + "/certificate/withdraw/" + serialNumber, {reason: reason}, options);
  }

  download(serialNumber: String): Observable<any> {
    const options: any = {
      responseType: 'blob',
    };
    return this.http.get<any>(environment.apiHost + "/certificate/download/" + serialNumber, options);
  }

  downloadKey(serialNumber: String): Observable<any> {
    const options: any = {
      responseType: 'blob',
    };
    return this.http.get<any>(environment.apiHost + "/certificate/download-key/" + serialNumber, options);
  }

  generateRequest(cReqDTO: CertificateRequestDTO): Observable<any> {
    const options: any = {
        responseType: 'json',
      };
    return this.http.post<any>(environment.apiHost + "/certificate/request", cReqDTO, options);
  }

  generateRoot(): Observable<any>{
    const options: any = {
      responseType: 'json',
    };
  return this.http.post<any>(environment.apiHost + "/certificate/root", options);
  }


}

export interface Cerificate {
  id: number,
  serialNumber: string,
  validFrom: string,
  validTo: string,
  valid: boolean,
  type: any,
  issuer: User,
  issuedTo: User
}

export interface CertificateRequest {
  id: number,
  date: string,
  status: string,
  rejectionReason: string, 
  validTo: string,
  issuer: Cerificate,
  type: string
}

export interface CertificateRequestDTO {
  validTo: string,
  issuerSerialNumber: string,
  type: string
}
