import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LoginComponent } from './login/login.component';

export const routes: Routes = [
  // Redirect the root path to the dashboard
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  // Define the route for the dashboard
  {
    path: 'dashboard',
    title: "Dashboard",
    component: DashboardComponent
  },
  // Route for login
  {
    path: 'login',
    title: "Login Page",
    component: LoginComponent
  }
  // Route for register
  //Route for profile
];
