import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-edit-centar',
  standalone: true,
  templateUrl: './edit-centar.component.html',
  styleUrls: ['./edit-centar.component.css'],
  imports: [CommonModule, ReactiveFormsModule]
})
export class EditCentarComponent implements OnInit {
  centarId: string = '';
  centarDetails: any;
  editForm: FormGroup;
  workHoursForm: FormGroup;
  disciplinaForm: FormGroup;
  currentWorkHours: any[] = [];
  errorMessage: string = '';
  successMessage: string = '';

  // File upload fields
  selectedImage: File | null = null;
  selectedPdf: File | null = null;
  uploadSuccessMessage: string = '';
  uploadErrorMessage: string = '';

  daysOfWeek = ['Ponedeljak', 'Utorak', 'Sreda', 'Cetvrtak', 'Petak', 'Subota', 'Nedelja'];

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.editForm = this.fb.group({
      ime: ['', Validators.required],
      ophis: ['', Validators.required],
      adresa: ['', Validators.required],
      grad: ['', Validators.required],
      obrisi: [true]
    });

    this.workHoursForm = this.fb.group({
      danNedelje: ['', Validators.required],
      vremeOtvaranja: ['', Validators.required],
      vremeZatvaranja: ['', Validators.required]
    });

    this.disciplinaForm = this.fb.group({
      disciplina: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.centarId = this.route.snapshot.paramMap.get('id')!;
    this.fetchCentarDetails();
    this.fetchWorkHours();
  }

  fetchCentarDetails() {
    const token = localStorage.getItem('token');
    if (token) {
      const headers = new HttpHeaders().set('authorization', token);
      
      this.http.get(`http://localhost:8080/api/centri/${this.centarId}`, { headers })
        .subscribe(
          (data) => {
            this.centarDetails = data;
            this.editForm.patchValue({
              ime: this.centarDetails.ime,
              ophis: this.centarDetails.ophis,
              adresa: this.centarDetails.adresa,
              grad: this.centarDetails.grad
            });
          },
          (error) => {
            this.errorMessage = "Error fetching centar details.";
            console.error(error);
          }
        );
    }
  }

  fetchWorkHours() {
    const token = localStorage.getItem('token');
    if (token) {
      const headers = new HttpHeaders().set('authorization', token);
  
      this.http.get(`http://localhost:8080/api/centri/radnavremena?id=${this.centarId}`, { headers })
        .subscribe(
          (data: any) => {
            this.currentWorkHours = data as any[];
          },
          (error) => {
            this.errorMessage = "Error fetching work hours.";
            console.error(error);
          }
        );
    }
  }

  onSubmitEdit() {
    const token = localStorage.getItem('token');
    if (token) {
      const headers = new HttpHeaders().set('authorization', token);
      const formData = {
        id: this.centarId,
        ime: this.editForm.value.ime,
        ophis: this.editForm.value.ophis,
        adresa: this.editForm.value.adresa,
        grad: this.editForm.value.grad,
        obrisi: this.editForm.value.obrisi
      };

      this.http.post('http://localhost:8080/api/centri/izmenicentar', null, { 
        headers,
        params: formData
      }).subscribe(
        () => {
          this.successMessage = "Centar updated successfully!";
          this.router.navigate([`/centri/${this.centarId}`]);
        },
        (error) => {
          this.errorMessage = "Error updating centar.";
          console.error(error);
        }
      );
    }
  }

  onSubmitWorkHours() {
    const token = localStorage.getItem('token');
    if (token) {
      const headers = new HttpHeaders().set('authorization', token);
      const workHoursData = {
        centar: this.centarId,
        danNedelje: this.workHoursForm.value.danNedelje,
        vremeOtvaranja: this.convertToSqlTime(this.workHoursForm.value.vremeOtvaranja),
        vremeZatvaranja: this.convertToSqlTime(this.workHoursForm.value.vremeZatvaranja)
      };

      this.http.post('http://localhost:8080/api/centri/novoradnovreme', workHoursData, { headers })
        .subscribe({
          next: () => {
            this.successMessage = 'Work hours added successfully!';
            this.workHoursForm.reset();
            this.fetchWorkHours();
          },
          error: (err) => {
            this.errorMessage = `Failed to add work hours: ${err.message}`;
          }
        });
    }
  }

  onSubmitDisciplina() {
    const token = localStorage.getItem('token');
    if (token) {
      const headers = new HttpHeaders().set('authorization', token);
      const disciplinas = this.parseDisciplina(this.disciplinaForm.value.disciplina);

      this.http.post(`http://localhost:8080/api/centri/izmenicentar`, null, { 
        headers,
        params: { 
          id : this.centarId,
          disciplina: disciplinas.join(',') }
      }).subscribe(
        () => {
          this.successMessage = "Disciplines updated successfully!";
        },
        (error) => {
          this.errorMessage = "Error updating disciplines.";
          console.error(error);
        }
      );
    }
  }

  // FILE UPLOAD METHODS

  onImageSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedImage = file;
    }
  }

  onPdfSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedPdf = file;
    }
  }

  onUploadFiles() {
    const token = localStorage.getItem('token');
    if (!token) {
      this.uploadErrorMessage = "Not authorized";
      return;
    }

    if (!this.selectedImage && !this.selectedPdf) {
      this.uploadErrorMessage = "Please select at least one file to upload";
      return;
    }

    const headers = new HttpHeaders().set('authorization', token);
    const formData = new FormData();

    if (this.selectedImage) {
      formData.append('image', this.selectedImage);
    }
    if (this.selectedPdf) {
      formData.append('pdf', this.selectedPdf);
    }

    this.http.post(`http://localhost:8080/api/centri/${this.centarId}/upload`, formData, { headers })
      .subscribe({
        next: (response: any) => {
          this.uploadSuccessMessage = "Files uploaded successfully!";
          this.uploadErrorMessage = '';
          this.selectedImage = null;
          this.selectedPdf = null;
          this.centarDetails = response; // Update center details
          // Reset file inputs
          const imageInput = document.getElementById('imageInput') as HTMLInputElement;
          const pdfInput = document.getElementById('pdfInput') as HTMLInputElement;
          if (imageInput) imageInput.value = '';
          if (pdfInput) pdfInput.value = '';
        },
        error: (err) => {
          this.uploadErrorMessage = `Failed to upload files: ${err.message}`;
          this.uploadSuccessMessage = '';
          console.error(err);
        }
      });
  }

  parseDisciplina(disciplinaString: string): string[] {
    return disciplinaString.split(',').map(item => item.trim());
  }

  convertToSqlTime(timeString: string): string {
    const time = new Date();
    const [hours, minutes] = timeString.split(':');
    let newHours = parseInt(hours, 10) + 2;

    if (newHours >= 24) {
      newHours -= 24;
    }
    
    time.setHours(newHours, parseInt(minutes, 10), 0, 0);
  
    return time.toISOString().split('T')[1].split('.')[0];
  }

  deleteWorkHour(id: number) {
    const token = localStorage.getItem('token');
    if (token) {
      const headers = new HttpHeaders().set('authorization', token);
      
      this.http.post(`http://localhost:8080/api/centri/ukloniradnovreme?id=${id}`, null, { headers })
        .subscribe({
          next: () => {
            this.successMessage = 'Work hour deleted successfully!';
            this.fetchWorkHours();
          },
          error: (err) => {
            this.errorMessage = `Failed to delete work hour: ${err.message}`;
          }
        });
    }
  }
}
