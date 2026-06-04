import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpInterceptorFn } from '@angular/common/http';
import { KeycloakService } from 'keycloak-angular';
import { from, switchMap, of } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const keycloak = inject(KeycloakService);
  const platformId = inject(PLATFORM_ID);

  // 🚨 IMPORTANT: SSR SAFE GUARD
  if (!isPlatformBrowser(platformId)) {
    return next(req);
  }

  try {
    return from(keycloak.getToken()).pipe(
      switchMap((token) => {
        if (!token) return next(req);

        return next(
          req.clone({
            setHeaders: {
              Authorization: `Bearer ${token}`
            }
          })
        );
      })
    );
  } catch (e) {
    return next(req);
  }
};