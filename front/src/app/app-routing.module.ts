import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './shared/components/home/home.component';

import { NgModule } from '@angular/core';
import { UnauthGuard } from './guards/unauth.guard';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
    { path: '', redirectTo: 'home', pathMatch: 'full' }, // Route par défaut
    {
        path: 'home', canActivate: [UnauthGuard],
        component: HomeComponent
    },
    {
        path: 'auth',
        loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule)
    },






];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule],
})
export class AppRoutingModule { }