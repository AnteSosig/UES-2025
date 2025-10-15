import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-zahtev-table',
  standalone: true, // standalone component
  imports: [CommonModule, HttpClientModule], // necessary imports
  templateUrl: './zahtev-table.component.html',
  styleUrls: ['./zahtev-table.component.css']
})
export class ZahtevTableComponent implements OnInit {

  zahtevi: any[] = [];
  errorMessage: string = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchZahtevi();
  }

  // Fetch the list of zahtevi
  fetchZahtevi() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': token || ''
    });

    this.http.get('http://localhost:8080/api/korisnici/svizahtevi', { headers: headers })
      .subscribe({
        next: (data: any) => this.zahtevi = data,
        error: (err) => this.errorMessage = 'Could not fetch the data: ' + err.message
      });
  }

  // Accept request: Sends POST request to accept the zahtev
  acceptRequest(id: number) {
    this.handleZahtev(id, true);
  }

  // Reject request: Sends POST request to reject the zahtev
  rejectRequest(id: number) {
    this.handleZahtev(id, false);
  }

  // Function to handle both accept and reject actions
  handleZahtev(id: number, prihvati: boolean) {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': token || ''
    });

    const params = new HttpParams()
      .set('id', id.toString())
      .set('prihvati', prihvati.toString());

    // POST request with query params and headers
    this.http.post('http://localhost:8080/api/korisnici/prihvatizahtev', {}, { headers: headers, params: params })
      .subscribe({
        next: () => {
          // Success: Update UI or notify the user
          console.log(`Request ${prihvati ? 'accepted' : 'rejected'} successfully for ID: ${id}`);
          this.fetchZahtevi();  // Refresh the list after action
        },
        error: (err) => {
          // Handle error
          this.errorMessage = `Failed to ${prihvati ? 'accept' : 'reject'} request: ${err.message}`;
        }
      });
  }
}
