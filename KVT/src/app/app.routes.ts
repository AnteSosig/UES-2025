import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { HomeComponent } from './home/home.component';
import { CentarComponent } from './centar/centar.component';
import { ZahtevTableComponent } from './zahtev-table/zahtev-table.component';
import { NoviCentarComponent } from './novi-centar/novi-centar.component';
import { EditCentarComponent } from './edit-centar/edit-centar.component';
import { UserProfileComponent } from './user-profile/user-profile.component';
import { EditProfileComponent } from './edit-profile/edit-profile.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' }, 
  { path: 'home', component: HomeComponent },
  { path: 'centri/:id', component: CentarComponent },
  { path: 'svizahtevi', component: ZahtevTableComponent },
  { path: 'novicentar', component: NoviCentarComponent },
  { path: 'edit-centar/:id', component: EditCentarComponent },
  { path: 'profile', component: UserProfileComponent },
  { path: 'edit-profile', component: EditProfileComponent }, 
];
