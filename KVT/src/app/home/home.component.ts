import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, FormControl } from '@angular/forms';
import { Router } from '@angular/router';

export interface CentarDTO {
  id: number;
  ime: string;
  ophis: string;
  datumKreacije: Date;
  adresa: string;
  grad: string;
  rating: number;
  imagePath?: string;
  pdfPath?: string;
  hasPdf?: boolean;
}

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.component.html',
  imports: [CommonModule, ReactiveFormsModule]
})
export class HomeComponent implements OnInit {
  centri: CentarDTO[] = [];
  errorMessage: string = '';
  searchForm: FormGroup;
  isSearching: boolean = false;
  showAdvancedSearch: boolean = false;

  private searchApiUrl = 'http://localhost:8080/api/centri/search';
  private pretragaApiUrl = 'http://localhost:8080/api/centri/pretraga';

  constructor(private http: HttpClient, private fb: FormBuilder, private router: Router) {
    // Initialize the form with unified search and optional advanced filters
    this.searchForm = this.fb.group({
      // Unified search field
      query: [''],
      // Advanced search fields (optional)
      grad: [''],
      ocenamin: [''],
      ocenamax: [''],
      discipline: this.fb.array([]),
      ponedeljak: this.fb.array([]),
      utorak: this.fb.array([]),
      sreda: this.fb.array([]),
      cetvrtak: this.fb.array([]),
      petak: this.fb.array([]),
      subota: this.fb.array([]),
      nedelja: this.fb.array([]),
    });
  }

  ngOnInit(): void {
    const token = this.getToken();
    if (!token) {
      this.router.navigate(['/login']);
      return;
    }

    // Perform an initial search with all blank/default parameters
    this.onSubmitAdvanced();
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  toggleAdvancedSearch(): void {
    this.showAdvancedSearch = !this.showAdvancedSearch;
  }

  // Unified search method
  onSearchUnified(): void {
    const token = this.getToken();
    if (!token) return;

    const query = this.searchForm.value.query;
    if (!query || query.trim() === '') {
      // If search is empty, show all centers using advanced search
      this.onSubmitAdvanced();
      return;
    }

    this.isSearching = true;
    this.unifiedSearch(query, token).subscribe({
      next: (data: CentarDTO[]) => {
        this.centri = data;
        this.isSearching = false;
        console.log('Search results:', this.centri);
      },
      error: (error) => {
        this.errorMessage = 'An error occurred while searching.';
        this.isSearching = false;
        console.error(error);
      }
    });
  }

  // Unified search API call
  unifiedSearch(query: string, token: string): Observable<CentarDTO[]> {
    let params = new HttpParams().set('query', query);
    
    const headers = new HttpHeaders({
      Authorization: `${token}`
    });

    return this.http.get<CentarDTO[]>(this.searchApiUrl, { headers, params });
  }

  getWorkingHours(day: string): FormArray {
    return this.searchForm.get(day) as FormArray;
  }

  getDisciplines(): FormArray {
    return this.searchForm.get('discipline') as FormArray;
  }

  addWorkingHour(day: string): void {
    this.getWorkingHours(day).push(new FormControl(''));
  }

  removeWorkingHour(day: string, index: number): void {
    this.getWorkingHours(day).removeAt(index);
  }

  addDiscipline(): void {
    this.getDisciplines().push(new FormControl(''));
  }

  removeDiscipline(index: number): void {
    this.getDisciplines().removeAt(index);
  }

  onVidi(centar: any) {
    console.log('Vidi button clicked for:', centar);
    this.router.navigate([`/centri/${centar.id}`]);
  }

  downloadPdf(centarId: number, event: Event) {
    event.stopPropagation();
    const token = this.getToken();
    if (!token) return;

    const headers = new HttpHeaders({
      Authorization: `${token}`
    });

    this.http.get(`http://localhost:8080/api/centri/${centarId}/pdf`, {
      headers,
      responseType: 'blob'
    }).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `centar-${centarId}.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Error downloading PDF:', err);
        alert('Failed to download PDF');
      }
    });
  }

  // Submit advanced search
  onSubmitAdvanced(): void {
    const token = this.getToken();
    if (!token) return;

    const formValues = this.searchForm.value;

    const ponedeljak = formValues.ponedeljak.filter((v: string) => v !== '');
    const utorak = formValues.utorak.filter((v: string) => v !== '');
    const sreda = formValues.sreda.filter((v: string) => v !== '');
    const cetvrtak = formValues.cetvrtak.filter((v: string) => v !== '');
    const petak = formValues.petak.filter((v: string) => v !== '');
    const subota = formValues.subota.filter((v: string) => v !== '');
    const nedelja = formValues.nedelja.filter((v: string) => v !== '');
    const discipline = formValues.discipline.filter((d: string) => d !== '');

    this.fetchCentri(
      formValues.grad,
      ponedeljak,
      utorak,
      sreda,
      cetvrtak,
      petak,
      subota,
      nedelja,
      formValues.ocenamin,
      formValues.ocenamax,
      discipline,
      token
    ).subscribe({
      next: (data: CentarDTO[]) => {
        this.centri = data;
        console.log(this.centri);
      },
      error: (error) => {
        this.errorMessage = 'An error occurred while fetching data.';
        console.error(error);
      }
    });
  }

  fetchCentri(
    grad: string,
    ponedeljak: string[],
    utorak: string[],
    sreda: string[],
    cetvrtak: string[],
    petak: string[],
    subota: string[],
    nedelja: string[],
    ocenamin: string,
    ocenamax: string,
    discipline: string[],
    token: string
  ): Observable<CentarDTO[]> {
    let params = new HttpParams();

    params = params.set('grad', grad || '');
    params = params.set('ocenamin', ocenamin || '');
    params = params.set('ocenamax', ocenamax || '');

    discipline.forEach(d => {
      params = params.append('disciplina', d);
    });

    ponedeljak.forEach(hour => {
      params = params.append('ponedeljak', hour);
    });

    utorak.forEach(hour => {
      params = params.append('utorak', hour);
    });

    sreda.forEach(hour => {
      params = params.append('sreda', hour);
    });

    cetvrtak.forEach(hour => {
      params = params.append('cetvrtak', hour);
    });

    petak.forEach(hour => {
      params = params.append('petak', hour);
    });

    subota.forEach(hour => {
      params = params.append('subota', hour);
    });

    nedelja.forEach(hour => {
      params = params.append('nedelja', hour);
    });

    const headers = new HttpHeaders({
      Authorization: `${token}`
    });

    return this.http.get<CentarDTO[]>(this.pretragaApiUrl, { headers, params });
  }
}
