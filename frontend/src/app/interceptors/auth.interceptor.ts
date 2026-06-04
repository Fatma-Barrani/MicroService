import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { KeycloakService } from 'keycloak-angular';
import { Observable, from, of } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';
import { isPlatformBrowser } from '@angular/common';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private keycloak: KeycloakService,
    @Inject(PLATFORM_ID) private platformId: object
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    // Si SSR (serveur), ne pas ajouter le token
    if (!isPlatformBrowser(this.platformId)) {
      return next.handle(req);
    }

    return from(this.keycloak.getToken()).pipe(
      switchMap(token => {
        if (token) {
          const authReq = req.clone({
            setHeaders: { Authorization: `Bearer ${token}` }
          });
          return next.handle(authReq);
        }
        return next.handle(req);
      }),
      catchError(() => next.handle(req))
    );
  }
}