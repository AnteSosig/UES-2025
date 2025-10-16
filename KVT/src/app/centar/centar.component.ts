import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-centar',
  standalone: true,
  templateUrl: './centar.component.html',
  styleUrls: ['./centar.component.css'],
  imports: [CommonModule, ReactiveFormsModule]  // Import ReactiveFormsModule here
})
export class CentarComponent implements OnInit {
  centarId: string = '';  // Initialize as empty string
  centarDetails: any;  // To store the centar data
  errorMessage: string = '';  // Initialize errorMessage as an empty string
  uloga: string | null = '';  // Store the user role
  reservationForm: FormGroup;  // FormGroup for the reservation form
  reviewForm: FormGroup;  // FormGroup for the review form
  reservationSuccessMessage: string = '';  // To hold success messages for reservation
  reviewSuccessMessage: string = '';  // To hold success messages for review
  pdfDownloadError: string = '';  // To hold PDF download errors

  constructor(private route: ActivatedRoute, private http: HttpClient, private fb: FormBuilder, private router: Router) {
    // Initialize the reservation form
    this.reservationForm = this.fb.group({
      datum: ['', Validators.required],
      pocetak: ['', Validators.required],
      kraj: ['', Validators.required]
    });

    // Initialize the review form
    this.reviewForm = this.fb.group({
      opremljenost: ['', Validators.required],
      osoblje: ['', Validators.required],
      higijena: ['', Validators.required],
      prostorija: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    // Retrieve the ID from the URL
    this.centarId = this.route.snapshot.paramMap.get('id')!;

    // Retrieve the token and user role from local storage
    const token = localStorage.getItem('token');
    this.uloga = localStorage.getItem('uloga');  // Adjust according to how you store the role

    if (token) {
      // Set up the headers with the Authorization token
      const headers = new HttpHeaders().set('authorization', token);

      // Make the HTTP GET request to the backend
      this.http.get(`http://localhost:8080/api/centri/${this.centarId}`, { headers })
        .subscribe(
          (data) => {
            this.centarDetails = data;  // Store the data
          },
          (error) => {
            this.errorMessage = "Error fetching centar details or unauthorized access.";
            console.error('Error:', error); // Handle the error
          }
        );
    } else {
      this.errorMessage = "Authorization token not found. Please log in.";
    }
  }

  // Method for the edit button
  onEditClick(): void {
    // Navigate to the edit page using the router
    this.router.navigate([`/edit-centar/${this.centarId}`]);
  }

  // Submit reservation form
  onSubmitReservation() {
    const token = localStorage.getItem('token');
    const korisnikId = 123;  // Replace with actual logged-in user ID if available

    if (token) {
      const headers = new HttpHeaders().set('authorization', token);

      const pocetakTime = this.addHours(this.reservationForm.value.pocetak, 2); // Add 2 hours
      const krajTime = this.addHours(this.reservationForm.value.kraj, 2);       // Add 2 hours

      const formData = {
        id: null,
        datum: this.reservationForm.value.datum,  // Date in YYYY-MM-DD format
        pocetak: this.convertToSqlTime(pocetakTime),  // Time converted to HH:mm:ss format
        kraj: this.convertToSqlTime(krajTime),        // Time converted to HH:mm:ss format
        centar: this.centarId,
        korisnik: korisnikId,
        active: true
      };

      this.http.post('http://localhost:8080/api/centri/rezervisi', formData, { headers })
        .subscribe({
          next: () => {
            this.reservationSuccessMessage = 'Reservation successful!';
            this.reservationForm.reset();
          },
          error: (err) => {
            this.errorMessage = `Failed to make reservation: ${err.message}`;
          }
        });
    }
  }

  // Submit review form
  onSubmitReview() {
    const token = localStorage.getItem('token');
    if (token) {
      const headers = new HttpHeaders().set('authorization', token);
      const reviewData = {
        opremljenost: this.reviewForm.value.opremljenost,
        osoblje: this.reviewForm.value.osoblje,
        higijena: this.reviewForm.value.higijena,
        prostorija: this.reviewForm.value.prostorija,
        centar: this.centarId  // The current center ID
      };

      // POST request to submit the review
      this.http.post('http://localhost:8080/api/centri/novaocena', reviewData, { headers })
        .subscribe({
          next: () => {
            this.reviewSuccessMessage = 'Review submitted successfully!';
            this.reviewForm.reset();
          },
          error: (err) => {
            this.errorMessage = `Failed to submit review: ${err.message}`;
          }
        });
    }
  }

  // Helper to add hours to a time string
  addHours(timeString: string, hoursToAdd: number): string {
    const time = new Date();
    const [hours, minutes] = timeString.split(':');
    time.setHours(parseInt(hours, 10) + hoursToAdd, parseInt(minutes, 10), 0, 0);  // Add hours

    // Return the new time as HH:mm format
    return time.toTimeString().split(':').slice(0, 2).join(':');
  }

  // Helper to convert time string to java.sql.Time compatible format
  convertToSqlTime(timeString: string): string {
    const time = new Date();
    const [hours, minutes] = timeString.split(':');
    time.setHours(parseInt(hours, 10), parseInt(minutes, 10), 0, 0);  // Set hours, minutes, and seconds

    return time.toISOString().split('T')[1].split('.')[0];  // Return in HH:mm:ss format
  }

  // Download PDF file
  downloadPdf() {
    const token = localStorage.getItem('token');
    if (!token) {
      this.pdfDownloadError = 'Not authorized';
      return;
    }

    const headers = new HttpHeaders().set('authorization', token);
    
    this.http.get(`http://localhost:8080/api/centri/${this.centarId}/pdf`, {
      headers,
      responseType: 'blob'
    }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `centar-${this.centarId}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        this.pdfDownloadError = '';
      },
      error: (err) => {
        this.pdfDownloadError = 'Failed to download PDF';
        console.error(err);
      }
    });
  }

  // Get proper image URL
  getImageUrl(imagePath: string): string {
    if (!imagePath) {
      return '';
    }
    // Just return the API URL - no auth needed now
    return `http://localhost:8080/api/centri/${this.centarId}/image`;
  }

  // Navigate back to home page
  goBack(): void {
    this.router.navigate(['/home']);
  }
}
