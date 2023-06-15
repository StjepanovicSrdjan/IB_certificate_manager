// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
    production: false,
    apiHost: 'http://localhost:4388/api',
    recaptcha: {
      siteKey: '6Lf9SxgmAAAAAP9sRlEr3Jsdb59jxfpZQoMc1Oej'
    },
    oAuth: {
      googleClientId: '390215664588-epukb0euf3502ne062h358r0r5ipahje.apps.googleusercontent.com'
    }
  };
  
  /*
   * For easier debugging in development mode, you can import the following file
   * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
   *
   * This import should be commented out in production mode because it will have a negative impact
   * on performance if an error is thrown.
   */
  // import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
  