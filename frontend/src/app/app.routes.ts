import { Routes } from '@angular/router';
import { AuthGuard } from './auth-guard';
import { LoginComponent } from './login/login';
import { Main } from './Pages/main/main';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginComponent  
  },
  {
    path: 'main',
    component: Main,
    canActivate: [AuthGuard]
  }
];