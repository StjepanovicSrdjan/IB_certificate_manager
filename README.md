# Certivus - Threat assessment
`using dependency check`

### google-oauth-client-1.31.2.jar
**CVE-2021-22573**
The vulnerability is that IDToken verifier does not verify if token is properly signed. Signature verification makes sure that the token's payload comes from valid provider, not from someone else. An attacker can provide a compromised token with custom payload. The token will pass the validation on the client side. 
`high`

**Solution**
Upgrading to version 1.33.3 or above should mitigate the risks.

### spring-boot-starter-web-3.0.5.jar
**CVE-2023-20883**
In Spring Boot versions 3.0.0 - 3.0.6, 2.7.0 - 2.7.11, 2.6.0 - 2.6.14, 2.5.0 - 2.5.14 and older unsupported versions, there is potential for a denial-of-service (DoS) attack if Spring MVC is used together with a reverse proxy cache.
`critical`

**Solution**
The app doesn't combine the dependency with reverse proxy cache, so there's no risk.
Additionally, upgrading to a higher version would remove the threat.

### More?
Detailed report available in dependency-check-report.html
