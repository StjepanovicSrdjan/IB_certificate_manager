import { Observable } from 'rxjs';

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthConfig, JwksValidationHandler, OAuthService } from 'angular-oauth2-oidc';
import { environment } from 'src/environments/environment';

const authConfig: AuthConfig = {
  issuer: 'https://accounts.google.com',
  redirectUri: "https://localhost:4200" + '/login',
  clientId: environment.oAuth.googleClientId,
  scope: 'openid profile email',
  strictDiscoveryDocumentValidation: false,
  responseType: 'code',
  disablePKCE: true
}

@Injectable({
  providedIn: 'root'
})
export class OAuth2Service {

  constructor(private oauthService: OAuthService, private http: HttpClient) {
    // this.oauthService.configure(authConfig);
    // this.oauthService.loadDiscoveryDocumentAndTryLogin();
    console.log(authConfig.redirectUri!);
  }

  loginWithGoogle() {
    const authorizationUrl = 'https://accounts.google.com/o/oauth2/v2/auth';
    const clientId = authConfig.clientId;
    const redirectUri = authConfig.redirectUri!;
    const responseType = authConfig.responseType;
    const scope = authConfig.scope!;
    const pkce = authConfig.disablePKCE;
    console.log(redirectUri);
    const url = `${authorizationUrl}?client_id=${clientId}&redirect_uri=${encodeURIComponent(redirectUri)}&response_type=${responseType}&scope=${encodeURIComponent(scope)}&disable_pkce=${pkce}`;

    window.location.href = url;
  }

  signInToBack(code: String, state: String): Observable<any> {
    return this.http.get<any>(environment.apiHost + "/oauth/callback?code=" + code + "&state=" + state);
  }

}
