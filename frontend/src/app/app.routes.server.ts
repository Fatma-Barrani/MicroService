import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  { path: 'examens/edit/:id', renderMode: RenderMode.Server },  // ← plus de slash au début
  { path: '**', renderMode: RenderMode.Prerender }
];