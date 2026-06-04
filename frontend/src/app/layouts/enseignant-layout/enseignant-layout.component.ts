import { Component } from '@angular/core';
import { SidebarEnseignantComponent } from "../sidebar/sidebar-enseignant/sidebar-enseignant.component";
import { RouterOutlet } from "@angular/router";

@Component({
  selector: 'app-enseignant-layout',
  imports: [SidebarEnseignantComponent, RouterOutlet],
  templateUrl: './enseignant-layout.component.html',
  styleUrl: './enseignant-layout.component.css'
})
export class EnseignantLayoutComponent {

}
