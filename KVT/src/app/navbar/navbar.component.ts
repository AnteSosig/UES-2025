import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {
  isAdmin: boolean = false;
  isLoggedIn: boolean = false;

  constructor(private router: Router) {
    // Listen for navigation events to update navbar state
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.checkUserStatus();
    });
  }

  ngOnInit(): void {
    this.checkUserStatus();
  }

  checkUserStatus(): void {
    const token = localStorage.getItem('token');
    const uloga = localStorage.getItem('uloga');
    
    this.isLoggedIn = !!token;
    this.isAdmin = uloga === 'ADMIN';
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('uloga');
    console.log('Token removed from localStorage');
    this.isLoggedIn = false;
    this.isAdmin = false;
    this.router.navigate(['/login']);
  }
}
