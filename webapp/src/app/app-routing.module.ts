import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PageNotFoundComponent } from './error/page-not-found/page-not-found.component';
import { LightingControlComponent } from './lighting-control/lighting-control.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {environment} from "../environments/environment";


const routes: Routes = [
  { path: 'lighting', component: LightingControlComponent},
  { path: '', redirectTo: '/lighting', pathMatch: 'full'},
  { path: '**', component: PageNotFoundComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {enableTracing: !environment.production})],
  exports: [RouterModule, BrowserAnimationsModule]
})
export class AppRoutingModule { }
