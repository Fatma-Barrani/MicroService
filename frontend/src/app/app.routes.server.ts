import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: 'examens/edit/:id',
    renderMode: RenderMode.Client
  },
  {
    path: 'cours/edit/:id',
    renderMode: RenderMode.Client
  },
  {
    path: 'enseignant/examens/edit/:id',
    renderMode: RenderMode.Client
  },
  {
    path: 'enseignant/cours/edit/:id',
    renderMode: RenderMode.Client
  },
  {
    path: '**',
    renderMode: RenderMode.Prerender
  }
];
