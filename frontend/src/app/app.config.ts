import { ApplicationConfig, APP_INITIALIZER, provideZoneChangeDetection, importProvidersFrom, PLATFORM_ID } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { isPlatformBrowser } from '@angular/common';
import { authInterceptor } from './services/interceptor';


function initializeKeycloak(keycloak: KeycloakService, platformId: object) {
  return () => {
    // TRÈS IMPORTANT : Keycloak ne doit s'initialiser que dans le navigateur
    if (!isPlatformBrowser(platformId)) {
      return Promise.resolve();
    }

    return keycloak.init({
      config: {
        url: 'http://localhost:8080',
        realm: 'examen-realm',
        clientId: 'examen-client',
      },
      initOptions: {
        onLoad: 'login-required', // Redirige vers Keycloak si non connecté
        checkLoginIframe: false,
      },
      enableBearerInterceptor: false, // On met false car on utilise notre propre intercepteur personnalisé
    });
  };
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideClientHydration(withEventReplay()),
    
    // Correction ici : Utilisation de withInterceptors avec la fonction
    provideHttpClient(
      withFetch(),
      withInterceptors([authInterceptor]) 
    ),

    importProvidersFrom(KeycloakAngularModule),
    KeycloakService,

    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService, PLATFORM_ID],
    },
  ]
};