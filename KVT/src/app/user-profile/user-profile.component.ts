import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css'],
  standalone: true,  // Mark this as standalone
  imports: [CommonModule]  // Add CommonModule here
})
export class UserProfileComponent implements OnInit {
  userInfo: any = null;  // Store user information here
  reservations: any[] = [];  // Store reservations here
  errorMessage: string = '';
  reservationErrorMessage: string = '';

  constructor(private http: HttpClient, private router: Router) { }

  ngOnInit(): void {
    this.fetchUserInfo();
    this.fetchReservations();  // Fetch reservations on component init
  }

  goToEditPage() {
    this.router.navigate(['/edit-profile']);
  }

  fetchUserInfo() {
    const token = localStorage.getItem('token');  // Assuming token is stored in localStorage

    if (token) {
      const headers = new HttpHeaders().set('authorization', token);

      this.http.get('http://localhost:8080/api/korisnici/ja', { headers })
        .subscribe(
          (data) => {
            this.userInfo = data;  // Store user data
          },
          (error) => {
            this.errorMessage = 'Failed to load user information';
            console.error(error);
          }
        );
    } else {
      this.errorMessage = 'Authorization token not found. Please log in.';
    }
  }

  // Fetch user's reservations
  fetchReservations() {
    const token = localStorage.getItem('token');

    if (token) {
      const headers = new HttpHeaders().set('authorization', token);

      this.http.get('http://localhost:8080/api/centri/mojerezervacije', { headers })
      .subscribe(
        (data: any) => {
          this.reservations = data as any[];  // Cast the response to an array of reservations
        },
        (error) => {
          this.reservationErrorMessage = 'Failed to load reservations';
          console.error(error);
        }
      );
    } else {
      this.reservationErrorMessage = 'Authorization token not found. Please log in.';
    }
  }
}
